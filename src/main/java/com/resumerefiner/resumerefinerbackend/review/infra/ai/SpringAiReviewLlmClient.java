package com.resumerefiner.resumerefinerbackend.review.infra.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainRuleViolationException;
import com.resumerefiner.resumerefinerbackend.review.application.port.out.ReviewLlmClient;
import com.resumerefiner.resumerefinerbackend.review.domain.ReviewTone;
import com.resumerefiner.resumerefinerbackend.review.domain.vo.ReviewCustomizationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringAiReviewLlmClient implements ReviewLlmClient {
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    private static final int OUTPUT_SCHEMA_VERSION = 1;

    @Override
    public Result generateReview(
            String inputSnapshotJson,
            int inputSchemaVersion,
            ReviewTone tone,
            ReviewCustomizationRequest customizationRequest
    ) {
        if (inputSnapshotJson == null || inputSnapshotJson.isBlank()) {
            throw new DomainRuleViolationException("inputSnapshotJson is null or empty");
        }

        String system = """
            You are an expert resume reviewer.
            Return ONLY valid JSON. No markdown. No extra text.

            Output JSON schema (schemaVersion=%d):
            {
              "schemaVersion": %d,
              "summary": "string",
              "overallImprovedText": "string",
              "sectionResults": [
                {
                  "sectionKey": "string",
                  "issues": [{"type":"string","message":"string"}],
                  "improvements": [{"before":"string","after":"string","rationale":"string"}]
                }
              ]
            }

            Rules:
            - Must be valid JSON.
            - schemaVersion must be %d.
            - Respect the requested tone.
            """.formatted(OUTPUT_SCHEMA_VERSION, OUTPUT_SCHEMA_VERSION, OUTPUT_SCHEMA_VERSION);

        String customizationPart = (customizationRequest == null)
                ? "customizationRequest: null"
                : "customizationRequest: " + customizationRequest.getJson();

        String user = """
            tone: %s
            inputSchemaVersion: %d
            %s

            inputSnapshotJson:
            %s
            """.formatted(tone.name(), inputSchemaVersion, customizationPart, inputSnapshotJson);

        // 옵션은 모델/온도 등 네 설정에 맞게 조정
        ChatOptions options = ChatOptions.builder()
                // .model("gpt-4.1-mini") // 프로퍼티에서 주입되면 생략 가능
                .temperature(0.2)
                .build();

        String content = chatClient.prompt()
                .options(options)
                .system(system)
                .user(user)
                .call()
                .content();

        if (content == null || content.isBlank()) {
            throw new DomainRuleViolationException("LLM returned blank output");
        }

        // ✅ JSON 파싱 검증 (깨진 JSON 저장 방지)
        try {
            objectMapper.readTree(content);
        } catch (Exception e) {
            throw new DomainRuleViolationException("LLM returned invalid JSON");
        }

        return new Result("spring-ai", OUTPUT_SCHEMA_VERSION, content);
    }
}

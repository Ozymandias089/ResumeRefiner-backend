package com.resumerefiner.resumerefinerbackend.review.application.service;

import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.InvalidCredentialsException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.ResourceNotFoundException;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import com.resumerefiner.resumerefinerbackend.review.application.dto.internal.ReviewListItemDTO;
import com.resumerefiner.resumerefinerbackend.review.application.dto.response.GetReviewPageResponseDTO;
import com.resumerefiner.resumerefinerbackend.review.application.port.in.PageMyReviewsUseCase;
import com.resumerefiner.resumerefinerbackend.review.domain.Review;
import com.resumerefiner.resumerefinerbackend.review.domain.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageMyReviewsService implements PageMyReviewsUseCase {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ResumeRepository resumeRepository;

    @Override
    @Transactional(readOnly = true)
    public GetReviewPageResponseDTO page(PageMyReviewsCommand command) {
        Long memberId = memberRepository.findMemberIdByHandle(command.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Member not found"));

        var pageable = PageRequest.of(
                Math.max(command.page(), 0),
                clampSize(command.size())
        );

        Page<Review> pageResult;

        if (command.slug() != null && command.slug().isPresent()) {
            Resume resume = resumeRepository.findBySlug(command.slug().get())
                    .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

            if (!resume.getMemberId().equals(memberId)) {
                throw new InvalidCredentialsException("Member id mismatch");
            }

            pageResult = reviewRepository.findPageByMemberIdAndResumeId(memberId, resume.getId(), pageable);
        } else {
            pageResult = reviewRepository.findPageByMemberId(memberId, pageable);
        }

        var items = pageResult.getContent().stream()
                .map(r -> ReviewListItemDTO.builder()
                        .reviewId(r.getId())
                        .slug(resolveSlug(command, r)) // 아래 helper 참고
                        .resumeVersion(r.getResumeVersion())
                        .createdAt(r.getCreatedAt())
                        .title(buildTitle(command, r))
                        .build()
                )
                .toList();

        return GetReviewPageResponseDTO.builder()
                .reviews(items)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .hasPrev(pageResult.hasPrevious())
                .hasNext(pageResult.hasNext())
                .build();
    }
    private int clampSize(int size) {
        if (size <= 0) return 20;
        return Math.min(size, 50);
    }

    private String buildTitle(PageMyReviewsCommand command, Review r) {
        // resumeTitle이 없으니 slug로 만들자(나중에 resumeTitle 넣고 싶으면 여기만 수정)
        String slug = resolveSlug(command, r);
        int seq = r.getSequencePerVersion() != null ? r.getSequencePerVersion() : 0;
        return "%s · v%d · #%d".formatted(slug, r.getResumeVersion(), seq);
    }

    private String resolveSlug(PageMyReviewsCommand command, Review r) {
        if (command.slug() != null && command.slug().isPresent()) {
            return command.slug().toString();
        }

        return resumeRepository.findSlugById(r.getResumeId())
                .map(ResumeSlug::getValue)
                .orElse("UNKNOWN");
    }

}

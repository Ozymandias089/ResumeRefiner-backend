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
import com.resumerefiner.resumerefinerbackend.review.application.port.in.ReviewQueryUseCase;
import com.resumerefiner.resumerefinerbackend.review.domain.Review;
import com.resumerefiner.resumerefinerbackend.review.domain.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageMyReviewsService implements PageMyReviewsUseCase, ReviewQueryUseCase {

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

        // slug가 있으면 "이력서별"
        if (command.slug() != null && command.slug().isPresent()) {
            Resume resume = resumeRepository.findBySlug(command.slug().get())
                    .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

            if (!resume.getMemberId().equals(memberId)) {
                throw new InvalidCredentialsException("Member id mismatch");
            }

            pageResult = reviewRepository.findPageByMemberIdAndResumeId(memberId, resume.getId(), pageable);

        } else {
            // 없으면 "내 전체"
            pageResult = reviewRepository.findPageByMemberId(memberId, pageable);
        }

        var items = pageResult.getContent().stream()
                .map(r -> toListItem(r, resolveSlug(command, r)))
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

    @Override
    @Transactional(readOnly = true)
    public Optional<ReviewListItemDTO> get(GetLatestReviewQueryCommand command) {
        // 1) MemberId Load
        Long memberId = memberRepository.findMemberIdByHandle(command.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Member not found"));

        // 2) Resume Load
        Resume resume = resumeRepository.findBySlug(command.slug())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        // 3) Validate Resume Owner
        if (!resume.getMemberId().equals(memberId)) {
            log.warn("Member id mismatch. memberId={}, resumeOwnerId={}", memberId, resume.getMemberId());
            throw new InvalidCredentialsException("Member id mismatch");
        }

        // 4) Latest Review
        return reviewRepository.findLatestByResumeIdAndMemberId(resume.getId(), memberId)
                .map(review -> toListItem(review, resume.getSlug().getValue()));
    }

    // ===== helpers =====

    private int clampSize(int size) {
        if (size <= 0) return 20;
        return Math.min(size, 50);
    }

    /**
     * 공용: Review -> ReviewListItemDTO (title 포함)
     */
    private ReviewListItemDTO toListItem(Review review, String slug) {
        return ReviewListItemDTO.builder()
                .reviewId(review.getId())
                .slug(slug)
                .resumeVersion(review.getResumeVersion())
                .createdAt(review.getCreatedAt())
                .title(
                        buildTitle(
                                slug,
                                review.getResumeVersion(),
                                review.getSequencePerVersion()
                        )
                )
                .build();
    }

    /**
     * 공용 title: "{slug} · v{resumeVersion} · #{seq}"
     */
    private String buildTitle(String slug, long resumeVersion, Integer seq) {
        int s = (seq == null ? 0 : seq);
        return "%s · v%d · #%d".formatted(slug, resumeVersion, s);
    }

    /**
     * slug resolver
     * - 이력서별 조회면 command.slug에서 바로
     * - 전체 조회면 resumeId로 slug 조회 (MVP: N+1 가능)
     */
    private String resolveSlug(PageMyReviewsCommand command, Review r) {
        if (command.slug() != null && command.slug().isPresent()) {
            return command.slug().get().getValue();
        }

        return resumeRepository.findSlugById(r.getResumeId())
                .map(ResumeSlug::getValue)
                .orElse("UNKNOWN");
    }
}

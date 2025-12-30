package com.resumerefiner.resumerefinerbackend.review.application.service;

import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.InvalidCredentialsException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.ResourceNotFoundException;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.resume.application.ports.out.ResumeSlugTitleRow;
import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
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

            final String fixedSlug = resume.getSlug().getValue();
            final String fixedTitle = resume.getTitle();

            var items = pageResult.getContent().stream()
                    .map(r -> toListItem(r, fixedSlug, fixedTitle))
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

        // 없으면 "내 전체"
        pageResult = reviewRepository.findPageByMemberId(memberId, pageable);

        var items = pageResult.getContent().stream()
                .map(r -> {
                    ResumeSlugTitleRow row = resolveSlugTitleRow(r.getResumeId());
                    String slug = (row != null && row.getSlug() != null)
                            ? row.getSlug().getValue()
                            : "UNKNOWN";
                    String title = (row != null && row.getTitle() != null && !row.getTitle().isBlank())
                            ? row.getTitle()
                            : "UNTITLED";
                    return toListItem(r, slug, title);
                })
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
        Long memberId = memberRepository.findMemberIdByHandle(command.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Member not found"));

        Resume resume = resumeRepository.findBySlug(command.slug())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        if (!resume.getMemberId().equals(memberId)) {
            log.warn("Member id mismatch. memberId={}, resumeOwnerId={}", memberId, resume.getMemberId());
            throw new InvalidCredentialsException("Member id mismatch");
        }

        return reviewRepository.findLatestByResumeIdAndMemberId(resume.getId(), memberId)
                .map(review -> toListItem(
                        review,
                        resume.getSlug().getValue(),
                        resume.getTitle()
                ));
    }

    // ===== helpers =====

    private int clampSize(int size) {
        if (size <= 0) return 20;
        return Math.min(size, 50);
    }

    private ReviewListItemDTO toListItem(Review review, String slug, String resumeTitle) {
        return ReviewListItemDTO.builder()
                .reviewId(review.getId())
                .slug(slug)
                .resumeVersion(review.getResumeVersion())
                .createdAt(review.getCreatedAt())
                .title(review.buildTitle(resumeTitle))
                .build();
    }

    /**
     * MVP: resumeId로 slug/title 조회
     * (N+1 가능. 이후 배치 조회로 개선 가능)
     */
    private ResumeSlugTitleRow resolveSlugTitleRow(Long resumeId) {
        return resumeRepository.findSlugTitleById(resumeId).orElse(null);
    }
}

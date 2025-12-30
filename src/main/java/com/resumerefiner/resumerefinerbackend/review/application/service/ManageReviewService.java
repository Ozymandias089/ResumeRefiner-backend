package com.resumerefiner.resumerefinerbackend.review.application.service;

import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.InternalServerException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.InvalidCredentialsException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.ResourceNotFoundException;
import com.resumerefiner.resumerefinerbackend.global.shared.error.custom.domain.DomainConflictException;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.resume.domain.Resume;
import com.resumerefiner.resumerefinerbackend.resume.domain.ResumeRepository;
import com.resumerefiner.resumerefinerbackend.review.application.port.in.ManageReviewUseCase;
import com.resumerefiner.resumerefinerbackend.review.domain.Review;
import com.resumerefiner.resumerefinerbackend.review.domain.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManageReviewService implements ManageReviewUseCase {
    private final ReviewRepository reviewRepository;
    private final ResumeRepository resumeRepository;
    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public void delete(DeleteReviewCommand command) {
        Long memberId = memberRepository.findMemberIdByHandle(command.handle())
                .orElseThrow(() -> new InvalidCredentialsException("Member not found"));

        // 1) URL slug로 Resume 로드
        Resume resume = resumeRepository.findBySlug(command.slug())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found"));

        // 2) reviewId로 Review 로드
        Review review = reviewRepository.findById(command.reviewId())
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        // 3) 소유권/연결 검증
        if (!memberId.equals(resume.getMemberId())) {
            log.warn("Member does not own the resume. memberId={}, resumeOwnerId={}", memberId, resume.getMemberId());
            throw new InvalidCredentialsException("Member id mismatch");
        }
        if (!memberId.equals(review.getMemberId())) {
            log.warn("Member does not own the review. memberId={}, reviewOwnerId={}", memberId, review.getMemberId());
            throw new InvalidCredentialsException("Member id mismatch");
        }
        if (!resume.getId().equals(review.getResumeId())) {
            log.warn("Resume-review mismatch. resumeId={}, reviewResumeId={}", resume.getId(), review.getResumeId());
            throw new DomainConflictException("Resume-review mismatch");
        }

        // 4) 삭제
        reviewRepository.delete(review);
    }
}

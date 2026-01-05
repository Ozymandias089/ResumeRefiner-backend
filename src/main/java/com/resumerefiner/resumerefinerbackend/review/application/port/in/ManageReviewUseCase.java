package com.resumerefiner.resumerefinerbackend.review.application.port.in;

import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.ResumeSlug;
import lombok.Builder;

public interface ManageReviewUseCase {
    void delete(DeleteReviewCommand command);

    @Builder
    record DeleteReviewCommand(Handle handle, ResumeSlug slug, long reviewId) {}
}

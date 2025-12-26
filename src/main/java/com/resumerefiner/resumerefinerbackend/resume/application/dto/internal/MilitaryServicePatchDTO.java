package com.resumerefiner.resumerefinerbackend.resume.application.dto.internal;

import com.resumerefiner.resumerefinerbackend.resume.domain.vo.MilitaryBranch;
import com.resumerefiner.resumerefinerbackend.resume.domain.vo.MilitaryStatus;
import jakarta.validation.constraints.Size;

public record MilitaryServicePatchDTO(
        MilitaryStatus militaryStatus,
        MilitaryBranch branch,
        @Size(max = 50) String period,
        @Size(max = 50) String rank,
        @Size(max = 500) String notes
) {}

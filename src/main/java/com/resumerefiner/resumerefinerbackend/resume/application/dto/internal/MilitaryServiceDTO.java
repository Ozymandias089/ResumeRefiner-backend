package com.resumerefiner.resumerefinerbackend.resume.application.dto.internal;

import jakarta.validation.constraints.*;

public record MilitaryServiceDTO(
        @NotBlank
        @Pattern(
                regexp = "^(NOT_APPLICABLE|NOT_SERVED|SERVING|SERVED|EXEMPT)$",
                message = "invalid militaryStatus"
        )
        String militaryStatus,

        @Pattern(
                regexp = "^(ARMY|NAVY|AIR_FORCE|MARINE|SOCIAL_SERVICE|OTHER)$",
                message = "invalid branch"
        )
        String branch,

        @Size(max = 50) String period,
        @Size(max = 50) String rank,
        @Size(max = 500) String notes
) {}

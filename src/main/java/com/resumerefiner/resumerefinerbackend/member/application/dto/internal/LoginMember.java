package com.resumerefiner.resumerefinerbackend.member.application.dto.internal;

import java.io.Serializable;

public record LoginMember(
        Long id,
        String handle,
        String role,
        boolean active,
        int credits
) implements Serializable {}
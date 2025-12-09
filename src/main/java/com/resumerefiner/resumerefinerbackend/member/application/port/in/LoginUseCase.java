package com.resumerefiner.resumerefinerbackend.member.application.port.in;

import com.resumerefiner.resumerefinerbackend.member.application.dto.MemberSummaryDTO;

public interface LoginUseCase {
    MemberSummaryDTO login(LogInCommand command);

    record LogInCommand(String email, String password){}
}

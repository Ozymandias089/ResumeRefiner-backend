package com.resumerefiner.resumerefinerbackend.member.application.port.in;

import com.resumerefiner.resumerefinerbackend.member.application.dto.response.MemberSummaryDTO;

public interface LoginUseCase {
    MemberSummaryDTO login(LogInCommand command);

    record LogInCommand(String email, String password){}
}

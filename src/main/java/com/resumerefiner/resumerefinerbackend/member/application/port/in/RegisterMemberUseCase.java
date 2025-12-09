package com.resumerefiner.resumerefinerbackend.member.application.port.in;

import com.resumerefiner.resumerefinerbackend.member.application.dto.MemberSummaryDTO;

public interface RegisterMemberUseCase {
    MemberSummaryDTO register(RegisterMemberCommand command);

    record RegisterMemberCommand(String email, String password, String handle, String name){}
}

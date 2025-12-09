package com.resumerefiner.resumerefinerbackend.member.application.port.in;

import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.member.application.dto.MemberSummaryDTO;

public interface GetMeUseCase {
    MemberSummaryDTO getMe(GetMeCommand q);

    record GetMeCommand(Handle handle){}
}

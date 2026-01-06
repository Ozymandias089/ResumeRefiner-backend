package com.resumerefiner.resumerefinerbackend.member.application.port.in;

import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.MemberSummaryDTO;

public interface GetMeUseCase {
    MemberSummaryDTO getMe(GetMeQuery q);

    record GetMeQuery(Handle handle){}
}

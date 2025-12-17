package com.resumerefiner.resumerefinerbackend.member.application.port.in;

import com.resumerefiner.resumerefinerbackend.member.application.dto.response.MemberDetailsResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;

/**
 * 프로필 상세정보 조회용 UseCase
 */
public interface GetProfileUseCase {
    // 공개 프로필 API용 스텁. 과연 필요할까에 대한 고찰 필요
//    MemberDetailsResponseDTO getPublicProfile(GetProfileCommand command);

    MemberDetailsResponseDTO getMyProfile(GetProfileCommand command);
    record GetProfileCommand(Handle handle){}
}

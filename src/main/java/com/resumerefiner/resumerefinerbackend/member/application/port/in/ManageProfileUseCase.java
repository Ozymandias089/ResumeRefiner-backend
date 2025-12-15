package com.resumerefiner.resumerefinerbackend.member.application.port.in;

import com.resumerefiner.resumerefinerbackend.member.application.dto.response.ChangePasswordResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.MemberDetailsResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;

public interface ManageProfileUseCase {

    MemberDetailsResponseDTO changeUserInfo(ChangeInfoCommand command);

    ChangePasswordResponseDTO changePassword(ChangePasswordCommand command);

    record ChangeInfoCommand(Handle handle, String password, String newName){}

    record ChangePasswordCommand(Handle handle, String currentPassword, String newPassword){}
}

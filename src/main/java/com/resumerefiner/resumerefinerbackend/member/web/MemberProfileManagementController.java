package com.resumerefiner.resumerefinerbackend.member.web;

import com.resumerefiner.resumerefinerbackend.global.security.AuthenticatedMember;
import com.resumerefiner.resumerefinerbackend.member.application.dto.request.ChangePasswordRequestDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.request.ChangeProfileInfoRequestDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.ChangePasswordResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.MemberDetailsResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.GetProfileUseCase;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.GetProfileUseCase.GetProfileQuery;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.ManageProfileUseCase;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.ManageProfileUseCase.ChangePasswordCommand;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.ManageProfileUseCase.ChangeInfoCommand;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Email;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class MemberProfileManagementController {
    private final GetProfileUseCase getProfileUseCase;
    private final ManageProfileUseCase manageProfileUseCase;

    @GetMapping(produces = "application/json")
    public ResponseEntity<MemberDetailsResponseDTO> getProfile(@AuthenticatedMember Handle handle) {
        return ResponseEntity.ok(
                getProfileUseCase.getMyProfile(new GetProfileQuery(handle))
        );
    }

    @PatchMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<MemberDetailsResponseDTO> updateProfile(
            @AuthenticatedMember Handle handle,
            @Valid @RequestBody ChangeProfileInfoRequestDTO dto
    ) {

        String name = (dto.name() == null || dto.name().isBlank())
                ? null
                : dto.name();

        Email email = (dto.email() == null || dto.email().isBlank())
                ? null
                : Email.of(dto.email());

        return ResponseEntity.ok(
                manageProfileUseCase.changeUserInfo(new ChangeInfoCommand(handle, email, name))
        );
    }

    @PatchMapping(path = "/password", consumes = "application/json", produces = "application/json")
    public ResponseEntity<ChangePasswordResponseDTO> changePassword(
            @AuthenticatedMember Handle handle,
            @Valid @RequestBody ChangePasswordRequestDTO dto
    ) {
        return ResponseEntity
                .ok(manageProfileUseCase.changePassword(
                        new ChangePasswordCommand(handle, dto.oldPassword(), dto.newPassword()))
                );
    }
}

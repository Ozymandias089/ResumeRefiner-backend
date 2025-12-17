package com.resumerefiner.resumerefinerbackend.member.web;

import com.resumerefiner.resumerefinerbackend.member.application.dto.response.ValidateEmailResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.ValidateHandleResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.ValidateInputUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Validated
@RequiredArgsConstructor
public class ValidatorController {
    private final ValidateInputUseCase validateInputUseCase;

    @GetMapping("/handle/check")
    public ValidateHandleResponseDTO checkHandle(@Valid @RequestParam String handle) {
        return validateInputUseCase.validateHandle(handle);
    }

    @GetMapping("/email/check")
    public ValidateEmailResponseDTO checkEmail(@Valid @RequestParam String email) {
        return validateInputUseCase.validateEmail(email);
    }
}

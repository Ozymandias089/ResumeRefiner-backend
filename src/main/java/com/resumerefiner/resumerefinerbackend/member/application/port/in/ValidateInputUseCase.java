package com.resumerefiner.resumerefinerbackend.member.application.port.in;

import com.resumerefiner.resumerefinerbackend.member.application.dto.ValidateEmailResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.ValidateHandleResponseDTO;

public interface ValidateInputUseCase {
    ValidateHandleResponseDTO validateHandle(String handle);
    ValidateEmailResponseDTO validateEmail(String email);
}

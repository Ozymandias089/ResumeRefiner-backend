package com.resumerefiner.resumerefinerbackend.member.application.port.in;

import com.resumerefiner.resumerefinerbackend.member.application.dto.response.ValidateEmailResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.ValidateHandleResponseDTO;

public interface ValidateInputUseCase {
    ValidateHandleResponseDTO validateHandle(String handle);
    ValidateEmailResponseDTO validateEmail(String email);
}

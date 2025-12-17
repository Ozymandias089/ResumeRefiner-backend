package com.resumerefiner.resumerefinerbackend.member.application.service;

import com.resumerefiner.resumerefinerbackend.member.application.dto.response.ValidateEmailResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.dto.response.ValidateHandleResponseDTO;
import com.resumerefiner.resumerefinerbackend.member.application.port.in.ValidateInputUseCase;
import com.resumerefiner.resumerefinerbackend.member.domain.MemberRepository;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Email;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InputValidatorService implements ValidateInputUseCase {
    private final MemberRepository memberRepository;

    @Override
    public ValidateHandleResponseDTO validateHandle(String handle) {
        Handle target = new Handle(handle);

        boolean availability = !memberRepository.existsByHandle(target);
        log.info("Validate handle {} availability {}", handle, availability);

        return new ValidateHandleResponseDTO(handle, availability);
    }

    @Override
    public ValidateEmailResponseDTO validateEmail(String email) {
        Email target = new Email(email);

        boolean availability = !memberRepository.existsByEmail(target);
        log.info("Validate email {} availability {}", email, availability);

        return new ValidateEmailResponseDTO(email, availability);
    }
}

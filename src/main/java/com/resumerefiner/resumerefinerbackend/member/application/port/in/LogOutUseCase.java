package com.resumerefiner.resumerefinerbackend.member.application.port.in;

import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;

public interface LogOutUseCase {
    void logout(LogoutCommand command);

    record LogoutCommand(Handle handle){}
}

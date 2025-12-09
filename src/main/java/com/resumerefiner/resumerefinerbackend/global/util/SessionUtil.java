package com.resumerefiner.resumerefinerbackend.global.util;

import com.resumerefiner.resumerefinerbackend.member.application.dto.LoginMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public final class SessionUtil {
    public static final String LOGIN_MEMBER_KEY = "LOGIN_MEMBER";

    public static void setLoginMember(HttpServletRequest request, LoginMember loginMember) {
        request.getSession(true).setAttribute(LOGIN_MEMBER_KEY, loginMember);
    }

    public static LoginMember getLoginMember(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        return (LoginMember) session.getAttribute(LOGIN_MEMBER_KEY);
    }

    public static void invalidate(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}

package com.resumerefiner.resumerefinerbackend.global.security;

import com.resumerefiner.resumerefinerbackend.member.domain.Member;
import com.resumerefiner.resumerefinerbackend.member.domain.Role;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Email;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class AuthenticatedMemberPrincipal
        implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    private final String handle;
    private final String passwordHash;
    private final String role;
    private final boolean active;

    public AuthenticatedMemberPrincipal(
            Handle handle,
            String passwordHash,
            Role role,
            boolean active
    ) {
        this.handle = handle.toString(); // 🔑 세션 안정성
        this.passwordHash = passwordHash;
        this.role = role.name();
        this.active = active;
    }

    // ===== domain-friendly getter =====
    public Handle getHandle() {
        return Handle.of(handle);
    }

    // ===== UserDetails =====
    @Override
    public String getUsername() {
        return handle; // username 개념으로 handle 사용
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override public boolean isEnabled() { return active; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}

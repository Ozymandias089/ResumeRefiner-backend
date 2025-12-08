package com.resumerefiner.resumerefinerbackend.member.domain;

import com.resumerefiner.resumerefinerbackend.global.jpa.BaseTimeEntity;
import com.resumerefiner.resumerefinerbackend.global.shared.domain.AggregateRoot;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Email;
import com.resumerefiner.resumerefinerbackend.member.domain.vo.Handle;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(
        name = "member",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_member_handle", columnNames = "handle"),
                @UniqueConstraint(name = "uk_member_email", columnNames = "email")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity implements AggregateRoot {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Embedded
    private Handle handle;

    @Getter
    @Embedded
    private Email email;

    @Getter
    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Getter
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Getter
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 20)
    private Provider provider;

    @Getter
    @Column(name = "provider_user_id", length = 255)
    private String providerUserId;

    @Getter
    @Column(name = "profile_image_id")
    private Long profileImageId;

    @Getter
    @Column(name = "credits", nullable = false)
    private int credits;

    private Member(Handle handle, Email email, String passwordHash, String name, Role role, Provider provider, String providerUserId, Long profileImageId, int credits) {
        this.handle = Objects.requireNonNull(handle);
        this.email = Objects.requireNonNull(email);
        this.passwordHash = passwordHash;
        this.name = Objects.requireNonNull(name);
        this.role = (role != null) ? role : Role.USER;
        this.isActive = true; // 기본 활성
        this.provider = Objects.requireNonNull(provider);
        this.providerUserId = providerUserId;
        this.profileImageId = profileImageId;
        this.credits = credits;
    }

    public static Member registerLocal(String handle, String email, String passwordHash, String name) {
        return new Member(
                Handle.of(handle),
                Email.of(email),
                passwordHash,
                name,
                Role.USER,
                Provider.LOCAL,
                null,
                null,
                0
        );
    }

    public static Member registerSocial(String handle, String email, String name, Provider provider, String providerUserId) {
        return new Member(
                Handle.of(handle),
                Email.of(email),
                null,
                name,
                Role.USER,
                provider,
                providerUserId,
                null,
                0
        );
    }

    // ==== 비즈니스 메서드 ====

    public void changeName(String newName) {
        if (newName == null || newName.isBlank()) throw new IllegalArgumentException("name must not be blank");
        this.name = newName;
    }

    public void deactivate() { this.isActive = false; }

    public void activate() { this.isActive = true; }

    public void changeProfileImage(Long mediaFileId) { this.profileImageId = mediaFileId; }

    public void adjustCredits(int delta) {
        int next = this.credits + delta;
        if (next < 0) {
            throw new IllegalStateException("credits cannot be negative");
        }
        this.credits = next;
    }
}

package com.bnroll.commercedomain.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_users_phone", columnNames = "phone")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔑 Identity
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // 👤 Full name (scalable instead of single name field)
    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String phone;

    // 🏢 Multi-tenant support (store owner system)
    private Long companyId;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<RoleName> roles = new HashSet<>();

    // 🟢 Account status
    private boolean active = false;
    private boolean locked = false;
    private boolean deleted = false;
    private boolean verified = false;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private LocaleCode locale = LocaleCode.BN;

    // 🔐 Two-Factor Authentication (2FA)
    private boolean twoFactorEnabled = false;

    // secret key for TOTP (Google Authenticator style)
    private String twoFactorSecret;

    // optional backup codes (can be JSON or separate table later)
    @Column(columnDefinition = "TEXT")
    private String backupCodes;

    // 🔑 Security tracking
    private int failedLoginAttempts = 0;

    private LocalDateTime lastLoginAt;

    private String lastLoginIp;

    // ⏱ timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
package com.bnroll.commercedomain.entity.auth;

import com.bnroll.commercedomain.entity.user.LoginType;
import com.bnroll.commercedomain.entity.user.RoleName;
import com.bnroll.commercedomain.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "login_attempts")
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String identifier;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    private RoleName role;

    private boolean success;

    private String failureReason;

    private String ipAddress;

    private String userAgent;

    private String device;

    private String browser;

    private String os;

    private String country;

    private String city;

    private LocalDateTime attemptedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
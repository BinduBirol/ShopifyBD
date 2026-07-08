package com.bnroll.auth.service;

import com.bnroll.auth.repository.LoginAttemptRepository;
import com.bnroll.commercedomain.entity.auth.LoginAttempt;
import com.bnroll.commercedomain.entity.user.LoginType;
import com.bnroll.commercedomain.entity.user.RoleName;
import com.bnroll.commercedomain.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final LoginAttemptRepository loginAttemptRepository;

    @Override
    public void log(
            User user,
            String identifier,
            LoginType loginType,
            RoleName role,
            boolean success,
            String failureReason,
            HttpServletRequest request) {

        LoginAttempt attempt = new LoginAttempt();

        attempt.setUser(user);
        attempt.setIdentifier(identifier);
        attempt.setLoginType(loginType);
        attempt.setRole(role);
        attempt.setSuccess(success);
        attempt.setFailureReason(failureReason);

        attempt.setIpAddress(request.getRemoteAddr());
        attempt.setUserAgent(request.getHeader("User-Agent"));

        attempt.setAttemptedAt(LocalDateTime.now());

        loginAttemptRepository.save(attempt);
    }
}
package com.bnroll.auth.service;

import com.bnroll.commercedomain.entity.user.LoginType;
import com.bnroll.commercedomain.entity.user.RoleName;
import com.bnroll.commercedomain.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;

public interface LoginAttemptService {

    void log(
            User user,
            String identifier,
            LoginType loginType,
            RoleName role,
            boolean success,
            String failureReason,
            HttpServletRequest request
    );


}
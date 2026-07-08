package com.bnroll.auth.repository;

import com.bnroll.commercedomain.entity.auth.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginAttemptRepository
        extends JpaRepository<LoginAttempt, Long> {
}
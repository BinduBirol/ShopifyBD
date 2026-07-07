package com.bnroll.auth.repository;

import com.bnroll.commercedomain.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByPhone(@NotBlank(message = "{phone.required}") String phone);

    boolean existsByEmail(@NotBlank(message = "{email.required}") String email);

    Optional<User> findByPhone(@NotBlank(message = "{identifier.required}") String identifier);
}

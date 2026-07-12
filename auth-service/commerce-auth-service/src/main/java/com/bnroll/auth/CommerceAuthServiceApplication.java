package com.bnroll.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(
        scanBasePackages = "com.bnroll",
        exclude = UserDetailsServiceAutoConfiguration.class
)
@EntityScan("com.bnroll.commercedomain.entity")
public class CommerceAuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                CommerceAuthServiceApplication.class,
                args
        );
    }
}
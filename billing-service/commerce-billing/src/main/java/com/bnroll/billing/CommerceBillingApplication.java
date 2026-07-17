package com.bnroll.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class, scanBasePackages = "com.bnroll")
@EntityScan({
        "com.bnroll.billing.entity",
        "com.bnroll.commercedomain.entity"
})
@EnableJpaRepositories("com.bnroll.billing.repository")
public class CommerceBillingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommerceBillingApplication.class, args);
    }

}

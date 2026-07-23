package com.bnroll.auth.config;

import com.bnroll.logging.RequestIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class LoggingConfig {

    @Bean
    public FilterRegistrationBean<RequestIdFilter> requestIdFilter() {

        FilterRegistrationBean<RequestIdFilter> registration =
                new FilterRegistrationBean<>();

        registration.setFilter(new RequestIdFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return registration;
    }
}
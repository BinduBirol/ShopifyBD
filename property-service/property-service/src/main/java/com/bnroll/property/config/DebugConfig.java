package com.bnroll.property.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class DebugConfig {

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @PostConstruct
    public void init() {
        System.out.println("ddl-auto = " + ddlAuto);
    }

    @Bean
    ApplicationRunner datasourceInfo(DataSource dataSource) {
        return args -> {
            try (var conn = dataSource.getConnection()) {
                System.out.println("URL      : " + conn.getMetaData().getURL());
                System.out.println("Database : " + conn.getCatalog());
                System.out.println("User     : " + conn.getMetaData().getUserName());
            }
        };
    }
}
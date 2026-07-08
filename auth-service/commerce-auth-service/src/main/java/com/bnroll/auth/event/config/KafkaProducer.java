package com.bnroll.auth.event.config;

import com.bnroll.auth.event.dto.LoginFailedEvent;
import com.bnroll.auth.event.dto.LoginSuccessEvent;
import com.bnroll.auth.event.dto.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String USER_REGISTERED_TOPIC = "user-registered";
    private static final String LOGIN_FAILED_TOPIC = "login-failed";
    private static final String LOGIN_SUCCESS_TOPIC = "login-success";


    public void sendUserRegisteredEvent(UserRegisteredEvent event) {

        kafkaTemplate.send(
                USER_REGISTERED_TOPIC,
                event.userId().toString(),
                event
        );
    }


    public void sendLoginFailedEvent(LoginFailedEvent event) {

        kafkaTemplate.send(
                LOGIN_FAILED_TOPIC,
                event.identifier(),
                event
        );
    }


    public void sendLoginSuccessEvent(LoginSuccessEvent event) {

        kafkaTemplate.send(
                LOGIN_SUCCESS_TOPIC,
                event.userId().toString(),
                event
        );
    }
}
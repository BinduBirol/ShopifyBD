package com.bnroll.auth.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthException extends RuntimeException {

    private final String code;
    private final HttpStatus status;
    private final Object[] args;
    private final String correlationId;


    public AuthException(
            String code,
            HttpStatus status
    ) {
        super(code);

        this.code = code;
        this.status = status;
        this.args = new Object[]{};
        this.correlationId = null;
    }


    public AuthException(
            String code,
            HttpStatus status,
            Object... args
    ) {
        super(code);

        this.code = code;
        this.status = status;
        this.args = args;
        this.correlationId = null;
    }


    public AuthException(
            String code,
            HttpStatus status,
            String correlationId
    ) {
        super(code);

        this.code = code;
        this.status = status;
        this.correlationId = correlationId;
        this.args = new Object[]{};
    }


    public AuthException(
            String code,
            HttpStatus status,
            String correlationId,
            Object[] args
    ) {
        super(code);

        this.code = code;
        this.status = status;
        this.correlationId = correlationId;
        this.args = args;
    }
}
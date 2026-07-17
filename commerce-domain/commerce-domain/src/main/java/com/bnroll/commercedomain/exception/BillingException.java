package com.bnroll.commercedomain.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BillingException extends RuntimeException {

    private final String code;
    private final HttpStatus status;
    private final Object[] args;
    private final String correlationId;


    public BillingException(String code, HttpStatus status) {
        super(code);
        this.code = code;
        this.status = status;
        this.args = new Object[0];
        this.correlationId = null;
    }


    public BillingException(String code, HttpStatus status, Object... args) {
        super(code);
        this.code = code;
        this.status = status;
        this.args = args;
        this.correlationId = null;
    }


    public BillingException(String code, HttpStatus status, String correlationId) {
        super(code);
        this.code = code;
        this.status = status;
        this.correlationId = correlationId;
        this.args = new Object[0];
    }


    public BillingException(String code, HttpStatus status, String correlationId, Object[] args) {
        super(code);
        this.code = code;
        this.status = status;
        this.correlationId = correlationId;
        this.args = args;
    }
}
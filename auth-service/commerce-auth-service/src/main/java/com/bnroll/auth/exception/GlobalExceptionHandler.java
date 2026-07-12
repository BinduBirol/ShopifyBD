package com.bnroll.auth.exception;

import com.bnroll.common.dto.response.ApiError;
import com.bnroll.common.dto.response.ApiResponse;
import com.bnroll.common.i18n.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.exc.InvalidFormatException;


import java.time.LocalDateTime;
import java.util.Arrays;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageService messageService;
    @Value("${spring.application.name}")
    private String serviceName;

    private final MessageSource messageSource;

    // AUTH ERROR
    @ExceptionHandler(AuthException.class)
    public ApiResponse<?> handleAuthException(AuthException ex,
                                              HttpServletRequest request, Locale locale) {


        return ApiResponse.builder()
                .success(false)
                .error(ApiError.builder()
                        .code(ex.getCode())
                        .message(messageSource.getMessage(ex.getCode(), ex.getArgs(), locale))
                        .status(ex.getStatus().value())
                        .service(serviceName)
                        .build())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .correlationId(ex.getCorrelationId())
                .version("v1")
                .build();
    }



    // FIELD ERROR
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request, Locale locale) {

        FieldError error1 = ex.getBindingResult().getFieldErrors().get(0);

        System.out.println("DefaultMessage = " + error1.getDefaultMessage());
        System.out.println("Codes = " + Arrays.toString(error1.getCodes()));
        System.out.println("Arguments = " + Arrays.toString(error1.getArguments()));

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> messageSource.getMessage(error, locale)
                ));

        return ApiResponse.builder()
                .success(false)
                .error(ApiError.builder()
                        .code("validation.failed")
                        .message(messageService.get("validation.failed", locale))
                        .status(ex.getStatusCode().value())
                        .service(serviceName)
                        .fieldErrors(fieldErrors)
                        .build())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .version("v1")
                .build();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request,
            Locale locale) {

        Map<String, String> fieldErrors = new HashMap<>();

        Throwable cause = ex.getMostSpecificCause();

        if (cause instanceof InvalidFormatException ife) {

            String field = ife.getPath().isEmpty()
                    ? "request"
                    : String.valueOf(ife.getPath().get(0).getDescription());

            if ("loginType".equals(field)) {
                fieldErrors.put(field, messageService.get("loginType.invalid", locale));
            } else if ("role".equals(field)) {
                fieldErrors.put(field, messageService.get("role.invalid", locale));
            } else {
                fieldErrors.put(field, messageService.get("invalid.enum.value", locale));
            }
        }

        return ApiResponse.builder()
                .success(false)
                .error(ApiError.builder()
                        .code("validation.failed")
                        .message(messageService.get("validation.failed", locale))
                        .status(HttpStatus.BAD_REQUEST.value())
                        .fieldErrors(fieldErrors)
                        .service(serviceName)
                        .build())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .version("v1")
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiResponse<?> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request,
            Locale locale) {

        String code = "data.integrity.violation";

        Throwable cause = ex.getRootCause();

        if (cause != null && cause.getMessage() != null) {
            String message = cause.getMessage();

            if (message.contains("users.UK") && message.contains("phone")) {
                code = "phone.already.exists";
            } else if (message.contains("users.UK") && message.contains("email")) {
                code = "email.already.exists";
            }
        }

        return ApiResponse.builder()
                .success(false)
                .error(ApiError.builder()
                        .code(code)
                        .message(messageService.get(code, locale))
                        .status(HttpStatus.CONFLICT.value())
                        .service(serviceName)
                        .build())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .version("v1")
                .build();
    }


    // GENERIC ERROR
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleGeneralException(Exception ex, HttpServletRequest request, Locale locale) {


        return ApiResponse.builder()
                .success(false)
                .error(ApiError.builder()
                        .code("internal.server.error")
                        .message(messageService.get("internal.server.error", locale))
                        .status(500)
                        .service(serviceName)
                        .build())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .version("v1")
                .build();
    }


}
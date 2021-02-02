package com.florian.learn_reactive_spring.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    /*@ExceptionHandler({RuntimeException.class})
    public Mono<ResponseEntity<String>> handleException(Exception exception) {
        log.error("Handling runtime exception in controller advice");
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage()));
    }*/

    @ExceptionHandler({AccessDeniedException.class})
    public Mono<ResponseEntity<String>> handleAccessDenied(AccessDeniedException exception) {
        log.error("Handling access denied exception in controller advice");
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage()));
    }
}

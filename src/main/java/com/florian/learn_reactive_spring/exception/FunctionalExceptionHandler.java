package com.florian.learn_reactive_spring.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static org.springframework.boot.web.error.ErrorAttributeOptions.Include.*;

@Component
@Slf4j
public class FunctionalExceptionHandler extends AbstractErrorWebExceptionHandler {


    public FunctionalExceptionHandler(ErrorAttributes errorAttributes, ApplicationContext applicationContext, ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        setMessageWriters(serverCodecConfigurer.getWriters());
        setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions
                .route(RequestPredicates.all(),this::renderResponse);
    }

    private Mono<ServerResponse> renderResponse(ServerRequest serverRequest) {
        Map<String, Object> errorAttributesMap = getErrorAttributes(serverRequest, ErrorAttributeOptions.of(EXCEPTION,STACK_TRACE,MESSAGE));
        log.error("Error attributes: {}", errorAttributesMap);
        if (errorAttributesMap.get("exception").toString().toLowerCase().contains("accessdeniedexception")) {
            try {
                return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .location(new URI("http",serverRequest.uri().getUserInfo(),
                                serverRequest.uri().getHost(),serverRequest.uri().getPort(),
                                "/login",serverRequest.uri().getQuery(),serverRequest.uri().getFragment()))
                        .body(BodyInserters.fromValue(errorAttributesMap.get("message")))
                        ;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        return ServerResponse.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(errorAttributesMap.get("message")));
    }
}

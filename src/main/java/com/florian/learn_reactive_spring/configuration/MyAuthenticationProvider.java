package com.florian.learn_reactive_spring.configuration;

import com.florian.learn_reactive_spring.utilities.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MyAuthenticationProvider implements ReactiveAuthenticationManager {
    @Autowired
    private JWTUtils jwtUtils;


    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String jwtToken = authentication.getName();

        return jwtUtils.parseToken(jwtToken);
    }
}

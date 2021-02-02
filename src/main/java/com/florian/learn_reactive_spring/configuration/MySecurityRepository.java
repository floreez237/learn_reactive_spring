package com.florian.learn_reactive_spring.configuration;

import com.florian.learn_reactive_spring.constants.SecurityConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class MySecurityRepository implements ServerSecurityContextRepository {

    @Autowired
    private ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        throw new UnsupportedOperationException("Save operation not supported");
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String jwtToken = exchange.getRequest().getHeaders().getFirst(SecurityConstants.JWT_HEADER);
        if (jwtToken == null) {
            log.error("NO JWT GIVEN");
            return Mono.empty();
        } else {
            return reactiveAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtToken, jwtToken))
                    .map(SecurityContextImpl::new);
        }
    }
}

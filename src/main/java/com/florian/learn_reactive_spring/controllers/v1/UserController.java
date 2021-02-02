package com.florian.learn_reactive_spring.controllers.v1;

import com.florian.learn_reactive_spring.constants.SecurityConstants;
import com.florian.learn_reactive_spring.document.AppUser;
import com.florian.learn_reactive_spring.repository.AppUserRepository;
import com.florian.learn_reactive_spring.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class UserController {
    private final LoginService loginService;

    public UserController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public Mono<String> login(@RequestBody AppUser appUser, ServerWebExchange webExchange) {
        Mono<String> jwt = loginService.loginUser(appUser);

        return jwt.doOnNext(token -> {
            webExchange.getResponse().getHeaders().add(SecurityConstants.JWT_HEADER, token);
           SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(appUser, null, Collections.singletonList(new SimpleGrantedAuthority("USER"))));
        });
    }
}

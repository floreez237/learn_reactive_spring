package com.florian.learn_reactive_spring.services;

import com.florian.learn_reactive_spring.document.AppUser;
import com.florian.learn_reactive_spring.utilities.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class LoginService {
    @Autowired
    @Qualifier("myUserDetailsService")
    private ReactiveUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    public Mono<String> loginUser(AppUser user) {
        Mono<UserDetails> userDetailsMono = userDetailsService.findByUsername(user.getUsername());

        return userDetailsMono.hasElement()
                .flatMap(aBoolean -> {
                    if (aBoolean) {
                        return Mono.empty();
                    }
                    throw new BadCredentialsException("Unknown Username");
                }).then(userDetailsMono)
                .map(userDetails -> userDetails.getPassword().equals(passwordEncoder.encode( user.getPassword())))
                .log("Correctness of Password")
                .map(isPasswordCorrect -> {
                    if (isPasswordCorrect) {
                        log.info("password is Correct");
                        return User.withUsername(user.getUsername())
                                .password(user.getPassword())
                                .roles("USER")
                                .build();
                    }
                    throw new BadCredentialsException("Wrong Password");
                }).flatMap(jwtUtils::generateToken);
    }
}

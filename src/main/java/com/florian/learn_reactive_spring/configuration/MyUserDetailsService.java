package com.florian.learn_reactive_spring.configuration;

import com.florian.learn_reactive_spring.repository.AppUserRepository;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class MyUserDetailsService implements ReactiveUserDetailsService {
    private final AppUserRepository appUserRepository;

    public MyUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return appUserRepository.findByUsername(username)
                .map(appUser -> User.withUsername(appUser.getUsername())
                        .roles("USER")
                        .disabled(!appUser.isEnabled())
                        .password(appUser.getPassword())
                        .build());
    }
}

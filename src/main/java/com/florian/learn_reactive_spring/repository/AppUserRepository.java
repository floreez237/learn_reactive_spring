package com.florian.learn_reactive_spring.repository;

import com.florian.learn_reactive_spring.document.AppUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface AppUserRepository extends ReactiveMongoRepository<AppUser, String> {
    Mono<AppUser> findByUsername(String username);
}

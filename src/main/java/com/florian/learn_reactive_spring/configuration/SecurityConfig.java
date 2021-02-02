package com.florian.learn_reactive_spring.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

import static com.florian.learn_reactive_spring.constants.ItemConstants.ITEM_ENDPOINT_V1;
import static com.florian.learn_reactive_spring.constants.ItemConstants.ITEM_FUNCTIONAL_ENDPOINT_V1;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig{

    @Autowired
    private ReactiveAuthenticationManager reactiveAuthenticationManager;
    @Autowired
    private ServerSecurityContextRepository securityRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity) {
        return httpSecurity.anonymous()
                .and().formLogin().and()
                .httpBasic().disable()
                .csrf().disable()
                .authorizeExchange(exchanges -> {
                    exchanges.pathMatchers(ITEM_FUNCTIONAL_ENDPOINT_V1.concat("/**")).permitAll()
                            .pathMatchers(ITEM_ENDPOINT_V1.concat("/**")).authenticated()
                            .anyExchange().permitAll();

                })
                .authenticationManager(reactiveAuthenticationManager)
                .securityContextRepository(securityRepository)
                .build();

    }

    /*@Bean
    public MapReactiveUserDetailsService detailsService() {
        UserDetails user = User.withDefaultPasswordEncoder().username("florian")
                .password("lowe")
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }*/

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}

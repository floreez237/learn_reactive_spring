package com.florian.learn_reactive_spring.utilities;
import com.florian.learn_reactive_spring.constants.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import sun.rmi.runtime.Log;
import sun.security.provider.PolicyParser;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.*;

@Component
@Slf4j
public class JWTUtils {

    public Mono<String> generateToken(UserDetails userDetails) {
        SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));
        String jwt = Jwts.builder().setIssuer("Spring Security").setSubject("JWT Token")
                .claim("username", userDetails.getUsername())//to add values to the payload part of the token
                .claim("authorities", populateWithAuthorities(userDetails.getAuthorities()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((long) ((new Date()).getTime() + 3e14)))
                .signWith(key).compact();

        return Mono.just(jwt);
    }

    public Mono<Authentication> parseToken(String jwt) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(
                    SecurityConstants.JWT_KEY.getBytes(StandardCharsets.UTF_8));

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
            String username = String.valueOf(claims.get("username"));
            String authorities = String.valueOf(claims.get("authorities"));
            Authentication auth = new UsernamePasswordAuthenticationToken(username, null,
                    AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
            return Mono.just(auth);
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new BadCredentialsException("Invalid Token received!");
        }
    }

    private String populateWithAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities) {
        StringBuilder stringBuilder = new StringBuilder();
        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>(grantedAuthorities);
        if (grantedAuthorities.size() >= 1) {
            stringBuilder.append(grantedAuthorityList.get(0));
            for (int i = 1; i < grantedAuthorityList.size(); i++) {
                stringBuilder.append(",").append(grantedAuthorityList.get(i));
            }
            return stringBuilder.toString();
        } else {
            return "";
        }
    }
}

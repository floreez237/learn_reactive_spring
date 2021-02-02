package com.florian.learn_reactive_spring.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.security.Principal;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AppUser implements Principal {
    @Id
    private String id;
    private String username;
    private String password;
    private boolean isEnabled = false;

    @Override
    public String getName() {
        return getUsername();
    }

}

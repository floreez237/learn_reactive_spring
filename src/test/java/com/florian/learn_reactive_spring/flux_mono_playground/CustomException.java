package com.florian.learn_reactive_spring.flux_mono_playground;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomException extends Throwable {
    private String message;

    public CustomException(Throwable cause) {
        this.message = cause.getMessage();
    }

}

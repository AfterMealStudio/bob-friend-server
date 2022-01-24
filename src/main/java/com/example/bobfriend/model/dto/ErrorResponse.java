package com.example.bobfriend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    String message;
    String error;

    public static ErrorResponse of(String message, Class<? extends Throwable> throwable) {
        return new ErrorResponse(message, throwable.getSimpleName());
    }
}

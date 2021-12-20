package com.example.bobfriend.model.dto.token;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Validation {
    private Boolean isValid;

    public Validation(Boolean isValid) {
        this.isValid = isValid;
    }
}
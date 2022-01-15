package com.example.bobfriend.model.dto.recruitment;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
@Getter
@Setter
public class Addresses {
    private Collection<Address> addresses;

    public Addresses(Collection<Address> addresses) {
        this.addresses = addresses;
    }
}

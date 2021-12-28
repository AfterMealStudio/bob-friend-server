package com.example.bobfriend.model.dto.recruitment;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
@Getter
@Setter
public class AddressCollection {
    private Collection<Address> addresses;

    public AddressCollection(Collection<Address> addresses) {
        this.addresses = addresses;
    }
}

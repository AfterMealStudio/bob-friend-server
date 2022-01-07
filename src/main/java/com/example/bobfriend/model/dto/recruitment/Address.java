package com.example.bobfriend.model.dto.recruitment;

import com.example.bobfriend.model.entity.Recruitment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(exclude = "count")
public class Address {
    private Double latitude;
    private Double longitude;
    private String address;
    private Integer count;

    public Address(Recruitment recruitment) {
        this.latitude = recruitment.getLatitude();
        this.longitude = recruitment.getLongitude();
        this.address = recruitment.getRestaurantAddress();
        this.count = 0;
    }
}

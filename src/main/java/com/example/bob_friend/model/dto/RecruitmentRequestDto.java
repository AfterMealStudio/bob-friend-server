package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.entity.Sex;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;

@Data
@NoArgsConstructor
public class RecruitmentRequestDto {
    private String title;
    private String content;
    private Integer totalNumberOfPeople;
    private String restaurantName;
    private String restaurantAddress;
    private Double latitude;
    private Double longitude;
    private Sex sexRestriction;
    private LocalDateTime appointmentTime;

    public RecruitmentRequestDto(Recruitment recruitment) {
        this.title = recruitment.getTitle();
        this.content = recruitment.getContent();
        this.totalNumberOfPeople = recruitment.getTotalNumberOfPeople();
        this.restaurantName = recruitment.getRestaurantName();
        this.restaurantAddress = recruitment.getRestaurantAddress();
        this.latitude = recruitment.getLatitude();
        this.longitude = recruitment.getLongitude();
        this.sexRestriction = recruitment.getSexRestriction();
        this.appointmentTime = recruitment.getAppointmentTime();
    }

    public Recruitment convertToDomain() {
        return Recruitment.builder()
                .title(this.title)
                .content(this.content)
                .members(new HashSet<>())
                .currentNumberOfPeople(1)
                .totalNumberOfPeople(this.totalNumberOfPeople)
                .restaurantName(this.restaurantName)
                .restaurantAddress(this.restaurantAddress)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .sexRestriction(this.sexRestriction)
                .appointmentTime(this.appointmentTime)
                .active(true)
                .full(false)
                .build();
    }
}

package com.example.bobfriend.model.dto.recruitment;

import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.Sex;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Create {
    @NotBlank
    private String title;
    @NotNull
    private String content;
    @Min(2)
    private Integer totalNumberOfPeople;
    @NotBlank
    private String restaurantName;
    @NotBlank
    private String restaurantAddress;

    private Double latitude;
    private Double longitude;

    @NotNull
    private Sex sexRestriction;

    @FutureOrPresent
    private LocalDateTime appointmentTime;

    private Integer ageRestrictStart;
    private Integer ageRestrictEnd;

    public Create(Recruitment recruitment) {
        this.title = recruitment.getTitle();
        this.content = recruitment.getContent();
        this.totalNumberOfPeople = recruitment.getTotalNumberOfPeople();
        this.restaurantName = recruitment.getRestaurantName();
        this.restaurantAddress = recruitment.getRestaurantAddress();
        this.latitude = recruitment.getLatitude();
        this.longitude = recruitment.getLongitude();
        this.sexRestriction = recruitment.getSexRestriction();
        this.ageRestrictStart = recruitment.getAgeRestrictionStart();
        this.ageRestrictEnd = recruitment.getAgeRestrictionEnd();
        this.appointmentTime = recruitment.getAppointmentTime();
    }

    public Recruitment convertToDomain() {
        return Recruitment.builder()
                .title(this.title)
                .content(this.content)
                .totalNumberOfPeople(this.totalNumberOfPeople)
                .restaurantName(this.restaurantName)
                .restaurantAddress(this.restaurantAddress)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .sexRestriction(this.sexRestriction)
                .ageRestrictionStart(this.ageRestrictStart)
                .ageRestrictionEnd(this.ageRestrictEnd)
                .appointmentTime(this.appointmentTime)
                .build();
    }
}

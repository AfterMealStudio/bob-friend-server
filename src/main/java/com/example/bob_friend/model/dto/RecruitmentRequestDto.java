package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.LinkedList;

@Data
@NoArgsConstructor
public class RecruitmentRequestDto {
    private String title;
    private String content;
    private Member author;
    private Integer totalNumberOfPeople;
    private String restaurantName;
    private String restaurantAddress;
    private Double latitude;
    private Double longitude;
    private LocalDateTime appointmentTime;

    public RecruitmentRequestDto(Recruitment recruitment) {
        this.title = recruitment.getTitle();
        this.content = recruitment.getContent();
        this.author = recruitment.getAuthor();
        this.totalNumberOfPeople = recruitment.getTotalNumberOfPeople();
        this.restaurantName = recruitment.getRestaurantName();
        this.restaurantAddress = recruitment.getRestaurantAddress();
        this.latitude = recruitment.getLatitude();
        this.longitude = recruitment.getLongitude();
        this.appointmentTime = recruitment.getAppointmentTime();
    }

    public Recruitment convertToDomain() {
        return Recruitment.builder()
                .title(this.title)
                .content(this.content)
                .author(this.author)
                .members(new LinkedList<Member>())
                .currentNumberOfPeople(1)
                .totalNumberOfPeople(this.totalNumberOfPeople)
                .restaurantName(this.restaurantName)
                .restaurantAddress(this.restaurantAddress)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .appointmentTime(this.appointmentTime)
                .active(true)
                .full(false)
                .build();
    }
}

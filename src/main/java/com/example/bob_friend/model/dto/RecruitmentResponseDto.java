package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Recruitment;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@Data
@NoArgsConstructor
public class RecruitmentResponseDto {
    private Long id;
    private String title;
    private String content;
    private MemberResponseDto author;
    private List<MemberResponseDto> members;
    private Integer totalNumberOfPeople;
    private Integer currentNumberOfPeople;
    private Boolean full;
    private String restaurantName;
    private String restaurantAddress;
    private Double latitude;
    private Double longitude;
    private LocalDateTime appointmentTime;
    private LocalDate createdAt;

    public RecruitmentResponseDto(Recruitment recruitment) {
        this.id = recruitment.getId();
        this.title = recruitment.getTitle();
        this.content = recruitment.getContent();
        this.author = new MemberResponseDto(recruitment.getAuthor());
        this.members = recruitment.getMembers().stream().map(MemberResponseDto::new).collect(Collectors.toList());
        this.currentNumberOfPeople = recruitment.getCurrentNumberOfPeople();
        this.totalNumberOfPeople = recruitment.getTotalNumberOfPeople();
        this.full = recruitment.isFull();
        this.restaurantName = recruitment.getRestaurantName();
        this.restaurantAddress = recruitment.getRestaurantAddress();
        this.latitude = recruitment.getLatitude();
        this.longitude = recruitment.getLongitude();
        this.appointmentTime = recruitment.getAppointmentTime();
        this.createdAt = recruitment.getCreatedAt().toLocalDate();
    }
}

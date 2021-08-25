package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RecruitmentResponseDto {
    private Long id;
    private String title;
    private String content;
    private Member author;
    private Integer totalNumberOfPeople;
    private Integer currentNumberOfPeople;
    private Boolean full;
    private String restaurantName;
    private Double latitude;
    private Double longitude;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;

    public RecruitmentResponseDto(Recruitment recruitment) {
        this.id = recruitment.getId();
        this.title = recruitment.getTitle();
        this.content = recruitment.getContent();
        this.author = recruitment.getAuthor();
        this.currentNumberOfPeople = recruitment.getCurrentNumberOfPeople();
        this.totalNumberOfPeople = recruitment.getTotalNumberOfPeople();
        this.full = recruitment.getFull();
        this.restaurantName = recruitment.getRestaurantName();
        this.latitude = recruitment.getLatitude();
        this.longitude = recruitment.getLongitude();
        this.startAt = recruitment.getStartAt();
        this.endAt = recruitment.getEndAt();
        this.createdAt = recruitment.getCreatedAt();
    }
}

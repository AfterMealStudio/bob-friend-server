package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.entity.Sex;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class RecruitmentDto {
    @Data
    @NoArgsConstructor
    public static class Request {
        private String title;
        private String content;
        private Integer totalNumberOfPeople;
        private String restaurantName;
        private String restaurantAddress;
        private Double latitude;
        private Double longitude;
        private Sex sexRestriction;
        private LocalDateTime appointmentTime;

        public Request(Recruitment recruitment) {
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

    @ToString
    @Data
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private String author;
        private Set<MemberDto.Preview> members;
        private Integer totalNumberOfPeople;
        private Integer currentNumberOfPeople;
        private Boolean full;
        private String restaurantName;
        private String restaurantAddress;
        private Double latitude;
        private Double longitude;
        private Sex sexRestriction;
        private LocalDateTime appointmentTime;
        private LocalDate createdAt;

        public Response(Recruitment recruitment) {
            this.id = recruitment.getId();
            this.title = recruitment.getTitle();
            this.content = recruitment.getContent();
            this.author = recruitment.getAuthor().getNickname();
            this.members = recruitment.getMembers().stream()
                    .map(member -> new MemberDto.Preview(member))
                    .collect(Collectors.toSet());
            this.currentNumberOfPeople = recruitment.getCurrentNumberOfPeople();
            this.totalNumberOfPeople = recruitment.getTotalNumberOfPeople();
            this.full = recruitment.isFull();
            this.restaurantName = recruitment.getRestaurantName();
            this.restaurantAddress = recruitment.getRestaurantAddress();
            this.latitude = recruitment.getLatitude();
            this.longitude = recruitment.getLongitude();
            this.appointmentTime = recruitment.getAppointmentTime();
            this.sexRestriction = recruitment.getSexRestriction();
            this.createdAt = recruitment.getCreatedAt().toLocalDate();
        }
    }
}

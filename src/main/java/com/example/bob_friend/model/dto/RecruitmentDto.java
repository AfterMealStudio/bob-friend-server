package com.example.bob_friend.model.dto;

import com.example.bob_friend.model.entity.Comment;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import com.example.bob_friend.model.entity.Sex;
import lombok.*;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
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
    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private MemberDto.Preview author;
        private Set<MemberDto.Preview> members;
        private Integer amountOfComments;
        private Set<CommentDto.Response> comments;
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
            this.author = new MemberDto.Preview(recruitment.getAuthor());
            this.members = recruitment.getMembers().stream()
                    .map(member -> new MemberDto.Preview(member))
                    .collect(Collectors.toSet());
            this.amountOfComments = recruitment.getComments().size();
            this.comments = recruitment.getComments().stream()
                    .map(CommentDto.Response::new)
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

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class ResponseList {
        private Long id;
        private String title;
        private String content;
        private MemberDto.Preview author;
        private Set<MemberDto.Preview> members;
        private Integer amountOfComments;
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

        public ResponseList(Recruitment recruitment) {
            this.id = recruitment.getId();
            this.title = recruitment.getTitle();
            this.content = recruitment.getContent();
            this.author = new MemberDto.Preview(recruitment.getAuthor());
            this.members = recruitment.getMembers().stream()
                    .map(member -> new MemberDto.Preview(member))
                    .collect(Collectors.toSet());
            this.amountOfComments = getAmountOfComments(recruitment);
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

        private int getAmountOfComments(Recruitment recruitment) {
            int amountOfReplies = 0;
            Set<Comment> comments = recruitment.getComments();
            for (Comment comment :
                    comments) {
                amountOfReplies += comment.getReplies().size();
            }
            return amountOfReplies + comments.size();
        }
    }


    @Getter
    @Setter
    @ToString
    public static class Address {
        private double latitude;
        private double longitude;
        private String address;
        private int count;


        public Address(Recruitment recruitment) {
            this.latitude = recruitment.getLatitude();
            this.longitude = recruitment.getLongitude();
            this.address = recruitment.getRestaurantAddress();
            this.count = 0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Address address1 = (Address) o;
            return Double.compare(address1.latitude, latitude) == 0 && Double.compare(address1.longitude, longitude) == 0 && address.equals(address1.address);
        }

        @Override
        public int hashCode() {
            return Objects.hash(latitude, longitude, address);
        }
    }
}

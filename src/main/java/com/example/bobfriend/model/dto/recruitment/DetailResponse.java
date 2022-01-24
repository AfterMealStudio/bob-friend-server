package com.example.bobfriend.model.dto.recruitment;

import com.example.bobfriend.model.dto.comment.Response;
import com.example.bobfriend.model.dto.member.Preview;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.Sex;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class DetailResponse extends BaseResponse {
    private String content;
    private Set<Preview> members;
    private String restaurantName;
    private String restaurantAddress;
    private Double latitude;
    private Double longitude;
    private List<Response> comments;
    private Sex sexRestriction;
    private LocalDateTime appointmentTime;

    public DetailResponse(Recruitment recruitment) {
        super(recruitment);
        this.content = recruitment.getContent();
        this.members = recruitment.getMembers().stream()
                .map(member -> new Preview(member))
                .collect(Collectors.toSet());
        this.restaurantName = recruitment.getRestaurantName();
        this.restaurantAddress = recruitment.getRestaurantAddress();
        this.latitude = recruitment.getLatitude();
        this.longitude = recruitment.getLongitude();
        this.appointmentTime = recruitment.getAppointmentTime();
        this.sexRestriction = recruitment.getSexRestriction();
        this.comments = recruitment.getComments().stream()
                .map(Response::new)
                .sorted(Comparator.comparing(Response::getCreatedAt))
                .collect(Collectors.toList());

    }
}

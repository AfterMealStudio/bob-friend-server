package com.example.bobfriend.model.dto.recruitment;

import com.example.bobfriend.model.dto.member.Preview;
import com.example.bobfriend.model.entity.Comment;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.Sex;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public abstract class BaseResponse {
    protected Long id;
    protected String title;
    protected String content;
    protected Preview author;
    protected Set<Preview> members;
    protected Integer amountOfComments;

    protected Integer totalNumberOfPeople;
    protected Integer currentNumberOfPeople;
    protected Boolean full;
    protected String restaurantName;
    protected String restaurantAddress;
    protected Double latitude;
    protected Double longitude;
    protected Sex sexRestriction;
    protected Integer reportCount;
    protected LocalDateTime appointmentTime;
    protected LocalDate createdAt;

    public BaseResponse(Recruitment recruitment) {
        this.id = recruitment.getId();
        this.title = recruitment.getTitle();
        this.content = recruitment.getContent();
        this.author = new Preview(recruitment.getAuthor());
        this.members = recruitment.getMembers().stream()
                .map(member -> new Preview(member))
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
        this.reportCount = recruitment.getReportCount();
    }


    private int getAmountOfComments(Recruitment recruitment) {
        int amountOfReplies = 0;
        List<Comment> comments = recruitment.getComments();
        for (Comment comment :
                comments) {
            amountOfReplies += comment.getReplies().size();
        }
        return amountOfReplies + comments.size();
    }
}


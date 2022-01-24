package com.example.bobfriend.model.dto.recruitment;

import com.example.bobfriend.model.dto.member.Preview;
import com.example.bobfriend.model.entity.Comment;
import com.example.bobfriend.model.entity.Recruitment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of =
        {"id", "createdAt"})
public abstract class BaseResponse {
    protected Long id;
    protected String title;
    protected Preview author;
    protected Integer amountOfComments;
    protected Integer totalNumberOfPeople;
    protected Integer currentNumberOfPeople;
    protected LocalDate createdAt;


    public BaseResponse(Recruitment recruitment) {
        this.id = recruitment.getId();
        this.title = recruitment.getTitle();
        this.author = new Preview(recruitment.getAuthor());
        this.amountOfComments = getAmountOfComments(recruitment);
        this.currentNumberOfPeople = recruitment.getCurrentNumberOfPeople();
        this.totalNumberOfPeople = recruitment.getTotalNumberOfPeople();
        this.createdAt = recruitment.getCreatedAt().toLocalDate();
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


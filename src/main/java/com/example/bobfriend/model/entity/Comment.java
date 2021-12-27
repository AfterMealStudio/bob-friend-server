package com.example.bobfriend.model.entity;

import com.example.bobfriend.model.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comment")
@PrimaryKeyJoinColumn(name = "comment_id")
@DiscriminatorValue(value = "comment")
public class Comment extends Writing {

    @ManyToOne
    @JoinColumn(name = "recruitment_id")
    private Recruitment recruitment;


    @OneToMany(targetEntity = Reply.class)
    @JoinColumn(name = "comment_id")
    private List<Reply> replies;


    public void clear() {
        this.author = null;
        this.content = null;
    }

    @Override
    public void setup() {
        super.setup();
        replies = new ArrayList<>();
    }

    public void report() {
        this.reportCount++;
        if (this.reportCount > Constant.REPORT_LIMIT && this.author != null) {
            this.getAuthor().increaseReportCount();
            this.content = null;
            this.reportCount = 0;
        }
    }
}

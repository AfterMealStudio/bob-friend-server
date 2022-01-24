package com.example.bobfriend.model.entity;

import com.example.bobfriend.model.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Getter
@Entity
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reply")
@PrimaryKeyJoinColumn(name = "reply_id")
@DiscriminatorValue(value = "reply")
public class Reply extends Writing {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;


    public void setComment(Comment comment) {
        this.comment = comment;
        comment.getReplies().add(this);
    }

    public void delete() {
        this.comment.getReplies().remove(this);
        this.comment = null;
    }

    public void report() {
        this.reportCount++;
        if (this.reportCount > Constant.REPORT_LIMIT) {
            this.getAuthor().increaseReportCount();
            this.content = null;
            this.reportCount = 0;
        }
    }
}

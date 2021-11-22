package com.example.bob_friend.model.entity;

import com.example.bob_friend.model.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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


    public void setAuthor(Member author) {
        this.author = author;
    }

    public void setRecruitment(Recruitment recruitment) {
        this.recruitment = recruitment;
    }

    public void clear() {
        this.author = null;
        this.content = null;
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

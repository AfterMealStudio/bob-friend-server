package com.example.bob_friend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member author;

    @ManyToOne
    @JoinColumn(name = "recruitment_id")
    private Recruitment recruitment;

    @Column(name = "content")
    private String content;

    @OneToMany(targetEntity = Reply.class)
    @JoinColumn(name = "comment_id")
    private Set<Reply> replies;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void createAt() {
        this.createdAt = LocalDateTime.now();
    }

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
}

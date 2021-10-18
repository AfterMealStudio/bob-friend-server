package com.example.bob_friend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reply")
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member author;

    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void createAt() {
        this.createdAt = LocalDateTime.now();
    }

    public void setAuthor(Member author) {
        this.author = author;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}

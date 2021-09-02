package com.example.bob_friend.model.entity;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
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
}

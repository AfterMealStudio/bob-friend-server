package com.example.bob_friend.model.entity;

import com.example.bob_friend.model.exception.MemberWithdrawalException;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TYPE")
public abstract class Writing {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    protected Member author;

    @Column(name = "content")
    protected String content;

    @Column(name = "report_count")
    protected Integer reportCount;

    @Column(name = "created_at")
    protected LocalDateTime createdAt;


    public Member getAuthor() {
        if (author == null)
            throw new MemberWithdrawalException();
        return author;
    }

    @PrePersist
    public void setup() {
        this.reportCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public abstract void report();
}

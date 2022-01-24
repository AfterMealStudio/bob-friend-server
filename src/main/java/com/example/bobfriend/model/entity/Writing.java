package com.example.bobfriend.model.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@EqualsAndHashCode(of = {"id", "createdAt"})
public abstract class Writing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    protected Member author;

    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    protected String content;

    @Column(name = "report_count")
    protected Integer reportCount;

    @Column(name = "created_at")
    protected LocalDateTime createdAt;


    @Transient
    public String getDiscriminatorValue() {
        DiscriminatorValue discriminatorValue = this.getClass().getAnnotation(DiscriminatorValue.class);
        return discriminatorValue.value();
    }

    public void setAuthor(Member author) {
        this.author = author;
    }

    public Member getAuthor() {
        if (author == null) {
            return Member.builder()
                    .id(-1)
                    .nickname("unknown")
                    .email("unknown")
                    .rating(0.0)
                    .numberOfJoin(0)
                    .active(false)
                    .build();
        }
        return author;
    }

    @PrePersist
    public void setup() {
        this.reportCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public abstract void report();
}

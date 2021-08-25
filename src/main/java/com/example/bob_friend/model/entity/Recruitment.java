package com.example.bob_friend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recruitment")
public class Recruitment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitment_id")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "author")
    private Member author;

    @Column(name = "members")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "total_number_of_people")
    private Integer totalNumberOfPeople;

    @Column(name = "current_number_of_people")
    private Integer currentNumberOfPeople;

    @Column(name = "full")
    private Boolean full;

    @Column(name = "restaurant_name")
    private String restaurantName;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "start_at")
    private LocalDateTime startAt;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @PrePersist
    public void createAt() {
        this.createdAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Recruitment{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

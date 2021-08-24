package com.example.bob_friend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    private long id;
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

    @Override
    public String toString() {
        return "Recruitment{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

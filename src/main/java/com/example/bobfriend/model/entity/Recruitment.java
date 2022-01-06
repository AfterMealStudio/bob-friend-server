package com.example.bobfriend.model.entity;

import com.example.bobfriend.model.Constant;
import com.example.bobfriend.model.exception.RecruitmentIsFullException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recruitment")
@DiscriminatorValue(value = "recruitment")
@PrimaryKeyJoinColumn(name = "recruitment_id")
public class Recruitment extends Writing {

    @Column(name = "title")
    private String title;

    @OneToMany()
    @JoinTable(name = "recruitment_member",
            joinColumns = {@JoinColumn(name = "recruitment_id", referencedColumnName = "recruitment_id")},
            inverseJoinColumns = @JoinColumn(name = "member_id", referencedColumnName = "member_id"))
    private Set<Member> members;

    @Column(name = "total_number_of_people")
    private Integer totalNumberOfPeople;

    @Column(name = "restaurant_name")
    private String restaurantName;

    @Column(name = "restaurant_address")
    private String restaurantAddress;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "end_at")
    private LocalDateTime endAt;

    @Column(name = "appointment_time")
    private LocalDateTime appointmentTime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recruitment")
    private List<Comment> comments;

    @Column(name = "active")
    private boolean active;


    @Column(name = "sex_restriction")
    @Convert(converter = SexConverter.class)
    private Sex sexRestriction;


    @Override
    public String toString() {
        return "Recruitment{" +
                "title='" + this.getTitle() + '\'' +
                ", members=" + this.getMembers() +
                ", id=" + id +
                ", author=" + this.getAuthor() +
                '}';
    }

    public void setAuthor(Member author) {
        this.author = author;
    }


    public void report() {
        this.reportCount++;
        if (this.reportCount > Constant.REPORT_LIMIT) {
            this.getAuthor().increaseReportCount();
            this.active = false;
            this.reportCount = 0;
        }
    }

    public void addMember(Member member) {
        if (this.getAuthor().equals(member))
            // 작성자가 탈퇴했거나 작성자가 참여하려고 할 경우 종료
            return;
        if (getCurrentNumberOfPeople() < totalNumberOfPeople) {
            this.members.add(member);
        } else {
            throw new RecruitmentIsFullException();
        }
    }

    public void removeMember(Member member) {
        getMembers().remove(member);
    }

    public boolean hasMember(Member member) {
        return members.contains(member);
    }

    public Integer getCurrentNumberOfPeople() {
        return getMembers().size();
    }

    public boolean isFull() {
        return getMembers().size() == getTotalNumberOfPeople();
    }

    public void close() {
        this.active = false;
    }

    public Sex getSexRestriction() {
        return sexRestriction;
    }

    @Override
    public void setup() {
        super.setup();
        comments = new LinkedList<>();
        members = Set.of(author);
        active = true;
        endAt = getAppointmentTime().plusDays(1);
    }
}

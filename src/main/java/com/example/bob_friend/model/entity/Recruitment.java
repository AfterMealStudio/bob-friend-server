package com.example.bob_friend.model.entity;

import com.example.bob_friend.model.Constant;
import com.example.bob_friend.model.exception.RecruitmentIsFullException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(cascade = CascadeType.ALL)
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
    private Set<Comment> comments;

    @Column(name = "active")
    private boolean active;


    @Column(name = "sex_restriction")
    @Convert(converter = SexConverter.class)
    private Sex sexRestriction;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Report> reports;

    @Override
    public String toString() {
        return "Recruitment{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
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
            throw new RecruitmentIsFullException(this.id);
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
        comments = new HashSet<>();
        members = Set.of(author);
        active = true;
        endAt = getAppointmentTime().plusDays(1);
    }
}

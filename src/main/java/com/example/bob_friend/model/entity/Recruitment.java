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

    @Column(name = "current_number_of_people")
    private Integer currentNumberOfPeople;

    @Column(name = "full")
    private boolean full;

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

    private void increaseCurrentNumberOfPeople() {
        this.currentNumberOfPeople += 1;
        if (currentNumberOfPeople >= totalNumberOfPeople)
            setFull(true);
    }

    private void decreaseCurrentNumberOfPeople() {
        this.currentNumberOfPeople -= 1;
        if (currentNumberOfPeople < totalNumberOfPeople)
            setFull(false);
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
        if (currentNumberOfPeople < totalNumberOfPeople) {
            this.members.add(member);
            increaseCurrentNumberOfPeople();
        } else {
            throw new RecruitmentIsFullException(this.id);
        }
    }

    public void removeMember(Member member) {
        if (this.members.remove(member))
            decreaseCurrentNumberOfPeople();
    }

    public boolean hasMember(Member member) {
        return members.contains(member);
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public void close() {
        this.active = false;
    }

    public Sex getSexRestriction() {
        return sexRestriction;
    }

    public void setSexRestriction(Sex sexRestriction) {
        this.sexRestriction = sexRestriction;
    }
}

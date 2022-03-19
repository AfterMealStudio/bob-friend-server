package com.example.bobfriend.util;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.Sex;

import java.time.LocalDateTime;

public class TestRecruitmentGenerator {
    private Member testAuthor;

    public void setAuthor(Member member) {
        this.testAuthor = member;
    }

    public Recruitment getTestRecruitmentWithContent(String content) {
        return getBaseRecruitmentBuilder()
                .content(content)
                .build();
    }

    public Recruitment getTestRecruitmentWithAppointmentTime(LocalDateTime appointmentTime) {
        return getBaseRecruitmentBuilder()
                .appointmentTime(appointmentTime)
                .build();
    }

    public Recruitment getTestRecruitmentWithRestaurantAddress(String restaurantAddress) {
        return getBaseRecruitmentBuilder()
                .restaurantAddress(restaurantAddress)
                .build();
    }

    public Recruitment getTestRecruitmentWithLocation(double lat, double lon) {
        return getBaseRecruitmentBuilder()
                .latitude(lat)
                .longitude(lon)
                .build();
    }

    public Recruitment getTestRecruitment() {
        return getBaseRecruitmentBuilder()
                .build();
    }

    public Recruitment getTestRecruitmentWithTitle(String title) {
        return getBaseRecruitmentBuilder()
                .title(title)
                .build();
    }

    public Recruitment getTestRecruitmentWithSexRestriction(Sex sex) {
        return getBaseRecruitmentBuilder()
                .sexRestriction(sex)
                .build();
    }

    public Recruitment getTestRecruitmentWithRestaurantName(String restaurantName) {
        return getBaseRecruitmentBuilder()
                .restaurantName(restaurantName)
                .build();
    }

    private Recruitment.RecruitmentBuilder<?, ?> getBaseRecruitmentBuilder() {
        return Recruitment.builder()
                .author(testAuthor)
                .totalNumberOfPeople(4)
                .restaurantName("testRestaurantName")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .sexRestriction(Sex.FEMALE)
                .content("testContent")
                .title("testTitle");
    }
}

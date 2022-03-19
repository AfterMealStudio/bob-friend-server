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
        Recruitment recruitment = getBaseRecruitmentBuilder()
                .appointmentTime(appointmentTime)
                .build();
        recruitment.setup();
        return recruitment;
    }

    public Recruitment getTestRecruitmentWithRestaurantAddress(String restaurantAddress) {
        Recruitment recruitment = getBaseRecruitmentBuilder()
                .restaurantAddress(restaurantAddress)
                .build();
        recruitment.setup();
        return recruitment;
    }

    public Recruitment getTestRecruitmentWithLocation(double lat, double lon) {
        Recruitment recruitment = getBaseRecruitmentBuilder()
                .latitude(lat)
                .longitude(lon)
                .build();
        recruitment.setup();
        return recruitment;
    }

    public Recruitment getTestRecruitment() {
        Recruitment recruitment = getBaseRecruitmentBuilder()
                .build();
        recruitment.setup();
        return recruitment;
    }

    public Recruitment getTestRecruitmentWithTitle(String title) {
        Recruitment recruitment = getBaseRecruitmentBuilder()
                .title(title)
                .build();
        recruitment.setup();
        return recruitment;
    }

    public Recruitment getTestRecruitmentWithSexRestriction(Sex sex) {
        Recruitment recruitment = getBaseRecruitmentBuilder()
                .sexRestriction(sex)
                .build();
        recruitment.setup();
        return recruitment;
    }

    public Recruitment getTestRecruitmentWithRestaurantName(String restaurantName) {
        Recruitment recruitment = getBaseRecruitmentBuilder()
                .restaurantName(restaurantName)
                .build();
        recruitment.setup();
        return recruitment;
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

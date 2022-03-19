package com.example.bobfriend.util;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.Sex;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class TestRecruitmentGenerator {
    private Member testAuthor;
    public void setAuthor(Member member) {
        this.testAuthor = member;
    }

    public Recruitment getTestRecruitmentWithContent(String content) {
        return Recruitment.builder()
                .title("test")
                .content(content)
                .author(testAuthor)
                .totalNumberOfPeople(4)
                .sexRestriction(Sex.FEMALE)
                .restaurantName("test")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .build();
    }

    public Recruitment getTestRecruitmentWithAppointmentTime(LocalDateTime appointmentTime) {
        return Recruitment.builder()
                .title("test")
                .content("test")
                .author(testAuthor)
                .totalNumberOfPeople(4)
                .sexRestriction(Sex.FEMALE)
                .restaurantName("test")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .appointmentTime(appointmentTime)
                .build();
    }

    public Recruitment getTestRecruitmentWithRestaurantName(String restaurantName) {
        return Recruitment.builder()
                .title("test")
                .content("test")
                .author(testAuthor)
                .totalNumberOfPeople(4)
                .sexRestriction(Sex.FEMALE)
                .restaurantName(restaurantName)
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .build();
    }

    public Recruitment getTestRecruitmentWithRestaurantAddress(String restaurantAddress) {
        return Recruitment.builder()
                .title("title 1")
                .content("content 1")
                .author(testAuthor)
                .totalNumberOfPeople(4)
                .sexRestriction(Sex.FEMALE)
                .members(Set.of(testAuthor))
                .restaurantName("testRestaurantName 1")
                .restaurantAddress(restaurantAddress)
                .latitude(0.0)
                .longitude(0.0)
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .build();
    }

    public Recruitment getTestRecruitmentWithLocation(double lat, double lon) {
        return Recruitment.builder()
                .latitude(lat)
                .longitude(lon)
                .restaurantAddress("test")
                .author(testAuthor)
                .appointmentTime(LocalDateTime.now())
                .sexRestriction(Sex.NONE)
                .build();
    }

    public Recruitment getTestRecruitment() {
        return Recruitment.builder()
                .title("title")
                .content("content")
                .author(testAuthor)
                .totalNumberOfPeople(4)
                .sexRestriction(Sex.FEMALE)
                .restaurantName("testRestaurantName")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .build();
    }

    public Recruitment getTestRecruitmentWithTitle(String title) {
        return Recruitment.builder()
                .title(title)
                .content("test")
                .author(testAuthor)
                .totalNumberOfPeople(4)
                .sexRestriction(Sex.FEMALE)
                .restaurantName("test")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .build();
    }

    public Recruitment getTestRecruitmentWithSexRestriction(Sex sex) {
        return Recruitment.builder()
                .title("test")
                .content("content")
                .author(testAuthor)
                .totalNumberOfPeople(4)
                .sexRestriction(sex)
                .restaurantName("test")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .build();
    }
}

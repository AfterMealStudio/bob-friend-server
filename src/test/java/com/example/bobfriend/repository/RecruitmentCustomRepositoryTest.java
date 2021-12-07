package com.example.bobfriend.repository;

import com.example.bobfriend.model.dto.Condition;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.Sex;
import com.example.bobfriend.testconfig.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
//@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RecruitmentCustomRepositoryTest {

    @Autowired
    private RecruitmentCustomRepositoryImpl recruitmentCustomRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member testAuthor;
    private Recruitment testRecruitment1, testRecruitment2;
    private Pageable pageable = Pageable.ofSize(10);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private String createdAt = "202111211726";


    @BeforeEach
    void setup() {
        testAuthor = Member.builder()
                .email("testAuthor@test.com")
                .nickname("testAuthor")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.now())
                .build();

        testRecruitment1 = Recruitment.builder()
                .title("title 1")
                .content("content 1")
                .author(testAuthor)
                .totalNumberOfPeople(4)
                .sexRestriction(Sex.FEMALE)
                .members(Set.of(testAuthor))
                .restaurantName("testRestaurantName 1")
                .restaurantAddress("testRestaurantAddress 1")
                .latitude(0.0)
                .longitude(0.0)
                .appointmentTime(LocalDateTime.parse(createdAt, formatter))
                .build();

        testRecruitment2 = Recruitment.builder()
                .title("title 2")
                .content("content 2")
                .author(testAuthor)
                .totalNumberOfPeople(4)
                .sexRestriction(Sex.FEMALE)
                .members(Set.of(testAuthor))
                .restaurantName("testRestaurantName 2")
                .restaurantAddress("testRestaurantAddress 2")
                .latitude(0.0)
                .longitude(0.0)
                .appointmentTime(LocalDateTime.parse(createdAt, formatter))
                .build();

        memberRepository.save(testAuthor);
        List list = Arrays.asList(testRecruitment1, testRecruitment2);
        recruitmentRepository.saveAll(list);

    }


    @Test
    void findAllTest() {
        Page<Recruitment> all = recruitmentCustomRepository.findAll(Pageable.ofSize(10));

        assertThat(all.getContent().size(), equalTo(2));
    }

    @Test
    void searchByTitleTest() {
        Recruitment recruitment1 = Recruitment.builder()
                .title("test")
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

        recruitmentRepository.save(recruitment1);

        Condition.Search search = new Condition.Search();
        search.setKeyword("title");

        Page<Recruitment> searchByTitle =
                recruitmentCustomRepository
                        .searchByTitle(search, pageable);
        assertThat(searchByTitle.getContent().size(), equalTo(2));
    }

    @Test
    void searchByContentTest() {
        Recruitment recruitment1 = Recruitment.builder()
                .title("test")
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

        recruitmentRepository.save(recruitment1);

        Condition.Search search = new Condition.Search();
        search.setKeyword("content");

        Page<Recruitment> searchByTitle =
                recruitmentCustomRepository
                        .searchByContent(search, Pageable.ofSize(10));
        assertThat(searchByTitle.getContent().size(), equalTo(2));
    }

    @Test
    void searchByRestaurantTest() {
        Recruitment recruitment1 = Recruitment.builder()
                .title("test")
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

        recruitmentRepository.save(recruitment1);

        Condition.Search search = new Condition.Search();
        search.setKeyword("testRestaurantName");

        Page<Recruitment> searchByTitle =
                recruitmentCustomRepository
                        .searchByRestaurant(search, Pageable.ofSize(10));
        assertThat(searchByTitle.getContent().size(), equalTo(2));
    }

    @Test
    void searchByAppointmentTimeTest() {
        Recruitment recruitment1 = Recruitment.builder()
                .title("test")
                .content("test")
                .author(testAuthor)
                .totalNumberOfPeople(4)
                .sexRestriction(Sex.FEMALE)
                .restaurantName("test")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .appointmentTime(LocalDateTime.parse("202211211725", formatter))
                .build();

        recruitmentRepository.save(recruitment1);

        Condition.Search search = new Condition.Search();
        search.setKeyword("test");
        search.setStart(createdAt);
        search.setEnd(createdAt.substring(0, 8) + "2000");

        Page<Recruitment> searchByTitle =
                recruitmentCustomRepository.searchByAll(search, Pageable.ofSize(10));

        assertThat(searchByTitle.getContent().size(), equalTo(2));
    }

    @Test
    void searchByAllTest() {
        Recruitment recruitment1 = Recruitment.builder()
                .title("test")
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

        recruitmentRepository.save(recruitment1);

        Condition.Search search = new Condition.Search();
        search.setKeyword("test");

        Page<Recruitment> searchByTitle =
                recruitmentCustomRepository
                        .searchByAll(search, Pageable.ofSize(10));
        assertThat(searchByTitle.getContent().size(), equalTo(3));
    }


    @Test
    void findAllByAuthorTest() {
        Page<Recruitment> allByAuthor =
                recruitmentCustomRepository
                        .findAllByAuthor(testAuthor, Pageable.ofSize(10));
        assertThat(allByAuthor.getContent().size(), equalTo(2));
    }

    @Test
    void findAllByRestaurantTest() {
        String restaurantName = testRecruitment1.getRestaurantName();
        String restaurantAddress = testRecruitment1.getRestaurantAddress();
        Condition.Search search = new Condition.Search();
        search.setRestaurantName(restaurantName);
        search.setRestaurantAddress(restaurantAddress);

        Page<Recruitment> allByRestaurant =
                recruitmentCustomRepository
                        .findAllByRestaurant(search, Pageable.ofSize(10));
        assertThat(allByRestaurant.getContent().size(), equalTo(1));
    }

    @Test
    void findAllAvailableTest() {
        Member member1 = Member.builder()
                .email("testAuthor@test.com")
                .nickname("testAuthor")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.parse(
                        "1997-06-04"
                ))
                .build();

        Recruitment recruitment1 = Recruitment.builder()
                .title("test")
                .content("test")
                .author(testAuthor)
                .totalNumberOfPeople(4)
                .sexRestriction(Sex.FEMALE)
                .members(Set.of(testAuthor))
                .restaurantName("test")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .build();

        Recruitment recruitment2 = Recruitment.builder()
                .title("test")
                .content("test")
                .author(testAuthor)
                .totalNumberOfPeople(4)
                .sexRestriction(Sex.FEMALE)
                .members(Set.of(testAuthor))
                .ageRestrictionStart(0)
                .ageRestrictionEnd(20)
                .restaurantName("test")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .build();

        memberRepository.save(member1);
        recruitmentRepository.save(recruitment1);
        recruitmentRepository.save(recruitment2);

        Page<Recruitment> allAvailable =
                recruitmentCustomRepository
                        .findAllAvailable(member1, pageable);

        assertThat(allAvailable.getContent().size(), equalTo(3));
    }

    @Test
    void findAllJoinedTest() {
        Member member1 = Member.builder()
                .email("testAuthor@test.com")
                .nickname("testAuthor")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.parse(
                        "1997-06-04"
                ))
                .build();

        Recruitment recruitment1 = Recruitment.builder()
                .title("test")
                .content("test")
                .author(member1)
                .totalNumberOfPeople(4)
                .sexRestriction(Sex.FEMALE)
                .restaurantName("test")
                .restaurantAddress("testRestaurantAddress")
                .latitude(0.0)
                .longitude(0.0)
                .appointmentTime(LocalDateTime.now().plusHours(4))
                .build();

        memberRepository.save(member1);
        recruitmentRepository.save(recruitment1);
        Page<Recruitment> allJoined =
                recruitmentCustomRepository
                        .findAllJoined(member1, pageable);
        assertThat(allJoined.getContent().size(), equalTo(1));
    }

}

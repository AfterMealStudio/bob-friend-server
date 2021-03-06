package com.example.bobfriend.repository;

import com.example.bobfriend.model.dto.Condition;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.Sex;
import com.example.bobfriend.testconfig.JpaTestConfig;
import com.example.bobfriend.util.TestMemberGenerator;
import com.example.bobfriend.util.TestRecruitmentGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaTestConfig.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RecruitmentCustomRepositoryTest {

    @Autowired
    private RecruitmentCustomRepositoryImpl recruitmentCustomRepository;

    @Autowired
    private RecruitmentRepository recruitmentRepository;

    @Autowired
    private MemberRepository memberRepository;

    private TestMemberGenerator testMemberGenerator = new TestMemberGenerator();
    private TestRecruitmentGenerator testRecruitmentGenerator = new TestRecruitmentGenerator();

    private Member testAuthor;
    private Recruitment testRecruitment1, testRecruitment2;
    private Pageable pageable = Pageable.ofSize(10);
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    @BeforeEach
    void setup() {
        testAuthor = testMemberGenerator.getTestAuthor();
        testRecruitmentGenerator.setAuthor(testAuthor);

        testRecruitment1 = testRecruitmentGenerator.getTestRecruitment();

        testRecruitment2 = testRecruitmentGenerator.getTestRecruitment();

        memberRepository.save(testAuthor);
        List list = Arrays.asList(testRecruitment1, testRecruitment2);
        recruitmentRepository.saveAll(list);

    }


    @Test
    @DisplayName("????????? ???????????? ???????????? ????????? recruitment??? ????????????.")
    void searchByTitleTest() {
        String title = "jeju";
        Recruitment recruitment1 = testRecruitmentGenerator.getTestRecruitmentWithTitle(title);

        recruitmentRepository.save(recruitment1);

        Condition.Search search = new Condition.Search();
        search.setKeyword(title);

        Page<Recruitment> searchByTitle =
                recruitmentCustomRepository
                        .searchByTitle(search, pageable);
        assertThat(searchByTitle.getContent().size(), equalTo(1));
    }

    @Test
    @DisplayName("????????? ???????????? ???????????? ????????? recruitment??? ????????????.")
    void searchByContentTest() {
        String content = "jeju";
        Recruitment recruitment1 = testRecruitmentGenerator.getTestRecruitmentWithContent(content);

        recruitmentRepository.save(recruitment1);

        Condition.Search search = new Condition.Search();
        search.setKeyword(content);

        Page<Recruitment> searchByTitle =
                recruitmentCustomRepository
                        .searchByContent(search, Pageable.ofSize(10));
        assertThat(searchByTitle.getContent().size(), equalTo(1));
    }

    @Test
    @DisplayName("??????????????? ???????????? ???????????? ????????? recruitment??? ????????????.")
    void searchByRestaurantTest() {
        String restaurantName = "jeju";
        Recruitment recruitment1 = testRecruitmentGenerator.getTestRecruitmentWithRestaurantName(restaurantName);

        recruitmentRepository.save(recruitment1);

        Condition.Search search = new Condition.Search();
        search.setKeyword("testRestaurantName");

        Page<Recruitment> searchByTitle =
                recruitmentCustomRepository
                        .searchByRestaurant(search, Pageable.ofSize(10));
        assertThat(searchByTitle.getContent().size(), equalTo(2));
    }

    @Test
    @DisplayName("????????? ?????? ????????? recruitment??? ????????????.")
    void searchByAppointmentTimeTest() {
        String time = "202011211725";
        LocalDateTime appointmentTime = LocalDateTime.parse(time, formatter);
        Recruitment recruitment1 = testRecruitmentGenerator.getTestRecruitmentWithAppointmentTime(appointmentTime);

        recruitmentRepository.save(recruitment1);

        Condition.Search search = new Condition.Search();
        search.setKeyword("test");
        search.setStart(time);
        search.setEnd(time.substring(0, 8) + "2000");

        Page<Recruitment> searchByTitle =
                recruitmentCustomRepository.searchByAll(search, Pageable.ofSize(10));

        assertThat(searchByTitle.getContent().size(), equalTo(1));
    }

    @Test
    @DisplayName("??????, ??????, ??????????????? ???????????? ???????????? ????????? recruitment??? ????????????.")
    void searchByAllTest() {
        Recruitment recruitment1 = testRecruitmentGenerator.getTestRecruitment();

        recruitmentRepository.save(recruitment1);

        Condition.Search search = new Condition.Search();
        search.setKeyword("test");

        Page<Recruitment> searchByTitle =
                recruitmentCustomRepository
                        .searchByAll(search, Pageable.ofSize(10));
        assertThat(searchByTitle.getContent().size(), equalTo(3));
    }


    @Test
    @DisplayName("???????????? recruitment??? ????????????.")
    void findAllByAuthorTest() {
        Page<Recruitment> allByAuthor =
                recruitmentCustomRepository
                        .findAllByAuthor(testAuthor, Pageable.ofSize(10));
        assertThat(allByAuthor.getContent().size(), equalTo(2));
    }


    @Test
    @DisplayName("????????? recruitment??? ????????????.")
    void findAllByAddressTest() {
        String restaurantAddress1 = "anotherRestaurantAddress";
        Recruitment recruitment = testRecruitmentGenerator.getTestRecruitmentWithRestaurantAddress(restaurantAddress1);

        recruitmentRepository.save(recruitment);

        String restaurantAddress = testRecruitment1.getRestaurantAddress();

        Page<Recruitment> allByRestaurant =
                recruitmentCustomRepository
                        .findAllByAddress(restaurantAddress, Pageable.ofSize(10));
        assertThat(allByRestaurant.getContent().size(), equalTo(2));
    }


    @Test
    @DisplayName("?????? ???????????? ??????????????? recruitment??? ????????????.")
    void findAllAvailableTest() {
        Member member1 = testMemberGenerator.getTestMember();

        Recruitment recruitment1 = testRecruitmentGenerator.getTestRecruitment();

        Recruitment recruitment2 = testRecruitmentGenerator.getTestRecruitmentWithSexRestriction(Sex.MALE);

        memberRepository.save(member1);
        recruitmentRepository.save(recruitment1);
        recruitmentRepository.save(recruitment2);

        Page<Recruitment> allAvailable =
                recruitmentCustomRepository
                        .findAllAvailable(member1, pageable);

        assertThat(allAvailable.getContent().size(), equalTo(3));
    }

    @Test
    @DisplayName("?????? ???????????? ?????????????????? recruitment??? ????????????.")
    void findAllJoinedTest() {
        Member member = testMemberGenerator.getTestMember();

        Recruitment recruitment = testRecruitmentGenerator.getTestRecruitment();

        memberRepository.save(member);
        recruitmentRepository.save(recruitment);

        recruitment.addMember(member);

        Page<Recruitment> allJoined =
                recruitmentCustomRepository
                        .findAllJoined(member, pageable);
        assertThat(allJoined.getContent().size(), equalTo(1));
    }


    @Test
    @DisplayName("recruitment??? ????????? ???????????? ????????????.")
    void findAllByLocation() {
        List<Recruitment> recruitments = new ArrayList();
        double lat = 33.4566084914484;
        double lon = 126.56207301534569;

        double distance = 0.01;
        for (int i = 0; i < 50; i++) {
            Recruitment recruitment = testRecruitmentGenerator.getTestRecruitmentWithLocation(lat + (i * distance), lon);
            recruitments.add(recruitment);
        }
        recruitmentRepository.saveAll(recruitments);

        List<Recruitment> allByLocation =
                recruitmentCustomRepository.findAllByLocation(lat, lon, distance);

        // ?????? ????????? ??????(distance)?????? ????????? ?????? ?????????
        int cnt = 0;
        for (Recruitment r : recruitments) {
            if (r.getLatitude().compareTo(lat + distance) <= 0)
                cnt++;
        }

        assertThat(allByLocation.size(), equalTo(cnt));
    }


    @Test
    @DisplayName("recruitment??? ???????????? ????????????.")
    void findAllSortTest() {
        recruitmentRepository.deleteAll();

        for (int i = 0; i < 10; i++) {
            Recruitment recruitment = testRecruitmentGenerator.getTestRecruitment();
            recruitmentRepository.save(recruitment);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Pageable pageable = PageRequest.of(0, 10,
                Sort.Direction.ASC, "createdAt"
        );
        Page<Recruitment> allByAuthor = recruitmentCustomRepository.findAllByAuthor(testAuthor, pageable);
        Iterator<Recruitment> iterator = allByAuthor.iterator();
        Recruitment prev = iterator.next();
        while (iterator.hasNext()) {
            Recruitment current = iterator.next();
            assertTrue(
                    current.getCreatedAt().isAfter(
                            prev.getCreatedAt()));
            prev = current;
        }
    }


}

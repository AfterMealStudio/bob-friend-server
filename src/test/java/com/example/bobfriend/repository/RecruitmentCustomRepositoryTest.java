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
    @DisplayName("제목에 해당하는 키워드가 포함된 recruitment를 반환한다.")
    void searchByTitleTest() {
        String title = "test";
        Recruitment recruitment1 = testRecruitmentGenerator.getTestRecruitmentWithTitle(title);

        recruitmentRepository.save(recruitment1);

        Condition.Search search = new Condition.Search();
        search.setKeyword("title");

        Page<Recruitment> searchByTitle =
                recruitmentCustomRepository
                        .searchByTitle(search, pageable);
        assertThat(searchByTitle.getContent().size(), equalTo(2));
    }

    @Test
    @DisplayName("내용에 해당하는 키워드가 포함된 recruitment를 반환한다.")
    void searchByContentTest() {
        String content = "test";
        Recruitment recruitment1 = testRecruitmentGenerator.getTestRecruitmentWithContent(content);

        recruitmentRepository.save(recruitment1);

        Condition.Search search = new Condition.Search();
        search.setKeyword("content");

        Page<Recruitment> searchByTitle =
                recruitmentCustomRepository
                        .searchByContent(search, Pageable.ofSize(10));
        assertThat(searchByTitle.getContent().size(), equalTo(2));
    }

    @Test
    @DisplayName("식당이름에 해당하는 키워드가 포함된 recruitment를 반환한다.")
    void searchByRestaurantTest() {
        String restaurantName = "test";
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
    @DisplayName("입력한 시간 사이의 recruitment를 반환한다.")
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
    @DisplayName("제목, 내용, 식당이름에 해당하는 키워드가 포함된 recruitment를 반환한다.")
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
    @DisplayName("작성자로 recruitment를 조회한다.")
    void findAllByAuthorTest() {
        Page<Recruitment> allByAuthor =
                recruitmentCustomRepository
                        .findAllByAuthor(testAuthor, Pageable.ofSize(10));
        assertThat(allByAuthor.getContent().size(), equalTo(2));
    }


    @Test
    @DisplayName("주소로 recruitment를 조회한다.")
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
    @DisplayName("현재 사용자가 참여가능한 recruitment를 조회한다.")
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
    @DisplayName("현재 사용자가 참여하고있는 recruitment를 조회한다.")
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
    @DisplayName("recruitment를 생성된 위치별로 조회한다.")
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

        // 미리 설정한 거리(distance)보다 가까운 장소 카운트
        int cnt = 0;
        for (Recruitment r : recruitments) {
            if (r.getLatitude().compareTo(lat + distance) <= 0)
                cnt++;
        }

        assertThat(allByLocation.size(), equalTo(cnt));
    }


    @Test
    @DisplayName("recruitment를 정렬해서 조회한다.")
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

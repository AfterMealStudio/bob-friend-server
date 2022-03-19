package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.recruitment.Address;
import com.example.bobfriend.model.dto.recruitment.Addresses;
import com.example.bobfriend.model.dto.recruitment.DetailResponse;
import com.example.bobfriend.model.dto.recruitment.SimpleResponse;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.exception.RecruitmentNotFoundException;
import com.example.bobfriend.repository.RecruitmentRepository;
import com.example.bobfriend.util.TestMemberGenerator;
import com.example.bobfriend.util.TestRecruitmentGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecruitmentFindServiceTest {
    @Mock
    RecruitmentRepository recruitmentRepository;
    @Mock
    MemberService memberService;

    @InjectMocks
    RecruitmentFindService recruitmentFindService;


    Recruitment testRecruitment;
    Member testAuthor;
    private PageRequest pageRequest = PageRequest.of(0, 10);
    private TestRecruitmentGenerator testRecruitmentGenerator = new TestRecruitmentGenerator();
    private TestMemberGenerator testMemberGenerator = new TestMemberGenerator();

    @BeforeEach
    public void setup() {
        testAuthor = testMemberGenerator.getTestAuthor();
        testRecruitmentGenerator.setAuthor(testAuthor);

        testRecruitment = testRecruitmentGenerator.getTestRecruitment();
    }


    @Test
    @DisplayName("findById호출 시 DetailResponse가 반환된다.")
    public void findByIdSuccess() {
        when(recruitmentRepository.findById(testRecruitment.getId()))
                .thenReturn(Optional.ofNullable(testRecruitment));

        DetailResponse byId = recruitmentFindService.findById(testRecruitment.getId());

        DetailResponse dtoFromEntity = new DetailResponse(testRecruitment);

        assertThat(byId, equalTo(dtoFromEntity));
    }


    @Test
    @DisplayName("findById호출 시 해당하는 recruitment가 없을 경우 예외가 발생한다.")
    public void findByIdFail() {
        when(recruitmentRepository.findById(0L))
                .thenReturn(Optional.empty());

        assertThrows(RecruitmentNotFoundException.class,
                () -> recruitmentFindService.findById(0L)
        );

    }


    @Test
    @DisplayName("findAll호출 시 Page<SimpleResponse>가 반환된다.")
    public void findAll() {
        List<Recruitment> recruitmentList = Arrays.asList(testRecruitment);

        List<SimpleResponse> collect = recruitmentList.stream()
                .map(r -> new SimpleResponse(r))
                .collect(Collectors.toList());

        Page<SimpleResponse> page = new PageImpl<>(collect);

        when(recruitmentRepository.findAll(pageRequest))
                .thenReturn(new PageImpl<>(recruitmentList));

        Page<SimpleResponse> responseDtoList = recruitmentFindService.findAll(pageRequest);

        assertThat(responseDtoList, equalTo(page));
    }


    @Test
    @DisplayName("현재 사용자가 참여한 recruitment를 Page<SimpleResponse>로 반환한다.")
    public void findAllJoinedRecruitments() {
        Member testMember = getCurrentMember();
        Recruitment recruitment = testRecruitmentGenerator.getTestRecruitment();
        List<Recruitment> recruitmentList = Arrays.asList(testRecruitment, recruitment);
        when(recruitmentRepository.findAllJoined(testMember, pageRequest))
                .thenReturn(new PageImpl<>(recruitmentList));

        Page<SimpleResponse> responseDtoList =
                recruitmentFindService.findAllJoined(pageRequest);

        assertThat(responseDtoList.toList(),
                equalTo(recruitmentList.stream()
                        .map(r -> new SimpleResponse(r))
                        .collect(Collectors.toList())));
    }

    private Member getCurrentMember() {
        Member testMember = testMemberGenerator.getTestMember();
        when(memberService.getCurrentMember()).thenReturn(testMember);
        return testMember;
    }


    @Test
    @DisplayName("현재 사용자가 참여가능한 recruitment를 Page<SimpleResponse>로 반환한다.")
    public void findAllAvailableRecruitments() {
        Member testMember = getCurrentMember();
        Recruitment recruitment = testRecruitmentGenerator.getTestRecruitment();

        List<Recruitment> recruitmentList = Arrays.asList(recruitment);
        when(recruitmentRepository.findAllAvailable(testMember, pageRequest))
                .thenReturn(new PageImpl<>(recruitmentList));

        Page<SimpleResponse> responseDtoList =
                recruitmentFindService.findAllAvailable(pageRequest);

        assertThat(responseDtoList.toList(),
                equalTo(Arrays.asList(new SimpleResponse(recruitment))));
    }


    @Test
    @DisplayName("현재 사용자가 작성한 recruitment를 Page<SimpleResponse>로 반환한다.")
    public void findMyRecruitments() {
        Member testMember = getCurrentMember();

        Recruitment recruitment = testRecruitmentGenerator.getTestRecruitment();

        List<Recruitment> recruitmentList = Arrays.asList(testRecruitment, recruitment);
        when(recruitmentRepository.findAllByAuthor(testMember, pageRequest))
                .thenReturn(new PageImpl<>(recruitmentList));

        Page<SimpleResponse> responseDtoList =
                recruitmentFindService.findMyRecruitments(pageRequest);

        assertThat(responseDtoList.toList(),
                equalTo(recruitmentList.stream()
                        .map(r -> new SimpleResponse(r))
                        .collect(Collectors.toList())));
    }


    @Test
    @DisplayName("recruitment를 식당이름으로 전체조회한다. Page<SimpleResponse>로 반환된다.")
    void findAllByRestaurantAddress() {
        String restaurantAddress = "restaurantAddress";

        when(recruitmentRepository.findAllByAddress(restaurantAddress, pageRequest))
                .thenReturn(new PageImpl<>(Arrays.asList(testRecruitment)));


        Page<SimpleResponse> restaurantList = recruitmentFindService
                .findAllByRestaurantAddress(restaurantAddress, pageRequest);

        assertThat(restaurantList.toList(),
                equalTo(Arrays.asList(
                        new SimpleResponse(testRecruitment)
                )));
    }

    @Test
    @DisplayName("위도, 경도, 지도의 줌레벨을 입력하면 일정 범위의 recruitment를 " +
            "뭉쳐서 반환한다.")
    void findAllLocationsTest() {
        double lat = 33.4566084914484;
        double lon = 126.56207301534569;
        int zoomLevel = 3;

        List<Recruitment> recruitments = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Recruitment recruitment =
                    testRecruitmentGenerator.getTestRecruitmentWithLocation(lat, lon);
            recruitments.add(recruitment);
        }
        when(recruitmentRepository.findAllByLocation(any(), any(), any()))
                .thenReturn(recruitments);

        Address address = new Address(recruitments.get(0));
        address.setCount(recruitments.size());
        Addresses allLocations = recruitmentFindService.findAllLocations(lat, lon, zoomLevel);

        assertThat(allLocations.getAddresses(), equalTo(List.of(address)));
    }

}

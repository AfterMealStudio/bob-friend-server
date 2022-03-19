package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.Condition;
import com.example.bobfriend.model.dto.recruitment.SimpleResponse;
import com.example.bobfriend.model.entity.*;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecruitmentSearchServiceTest {
    @Mock
    RecruitmentRepository recruitmentRepository;
    @InjectMocks
    RecruitmentSearchService recruitmentSearchService;

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
    @DisplayName("RecruitmentSearchService의 search메소드를 호출하면 결과가 Page객체로 감싸서 반환된다.")
    void search() {
        List<Recruitment> recruitments = Arrays.asList(testRecruitment);
        when(recruitmentRepository.searchByTitle(any(), any()))
                .thenReturn(new PageImpl<>(recruitments));

        List<SimpleResponse> collect = recruitments.stream()
                .map(SimpleResponse::new)
                .collect(Collectors.toList());

        Condition.Search search = new Condition.Search();

        search.setKeyword(testRecruitment.getTitle());
        Page<SimpleResponse> responsePage =
                recruitmentSearchService.searchTitle(search, pageRequest);

        assertThat(responsePage, equalTo(new PageImpl<>(collect)));
    }

    // FIXME: 2022-03-19
}

package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.Condition;
import com.example.bobfriend.model.dto.RecruitmentDto;
import com.example.bobfriend.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecruitmentSearchService {
    private final RecruitmentRepository recruitmentRepository;


    public Page<RecruitmentDto.Response> searchTitle(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByTitle(search, pageable)
                .map(RecruitmentDto.Response::new);
    }


    public Page<RecruitmentDto.Response> searchContent(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByContent(search, pageable)
                .map(RecruitmentDto.Response::new);
    }


    public Page<RecruitmentDto.Response> searchRestaurant(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByRestaurant(search, pageable)
                .map(RecruitmentDto.Response::new);
    }


    public Page<RecruitmentDto.Response> searchByAllCondition(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByAll(search, pageable)
                .map(RecruitmentDto.Response::new);
    }
}

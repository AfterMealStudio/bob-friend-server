package com.example.bobfriend.service;

import com.example.bobfriend.model.dto.Condition;
import com.example.bobfriend.model.dto.recruitment.SimpleResponse;
import com.example.bobfriend.repository.RecruitmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RecruitmentSearchService {
    private final RecruitmentRepository recruitmentRepository;


    public Page<SimpleResponse> searchTitle(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByTitle(search, pageable)
                .map(SimpleResponse::new);
    }


    public Page<SimpleResponse> searchContent(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByContent(search, pageable)
                .map(SimpleResponse::new);
    }


    public Page<SimpleResponse> searchRestaurant(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByRestaurant(search, pageable)
                .map(SimpleResponse::new);
    }


    public Page<SimpleResponse> searchByAllCondition(Condition.Search search, Pageable pageable) {
        return recruitmentRepository
                .searchByAll(search, pageable)
                .map(SimpleResponse::new);
    }
}

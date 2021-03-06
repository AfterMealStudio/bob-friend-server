package com.example.bobfriend.repository;

import com.example.bobfriend.model.dto.Condition;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RecruitmentCustomRepository {
    Page<Recruitment> searchByTitle(Condition.Search search, Pageable pageable);

    Page<Recruitment> searchByContent(Condition.Search search, Pageable pageable);

    Page<Recruitment> searchByRestaurant(Condition.Search search, Pageable pageable);

    Page<Recruitment> searchByAll(Condition.Search search, Pageable pageable);

    Page<Recruitment> findAllAvailable(Member currentMember ,Pageable pageable);

    Page<Recruitment> findAllJoined(Member currentMember, Pageable pageable);

    Page<Recruitment> findAllByAddress(String address, Pageable pageable);

    Page<Recruitment> findAllByAuthor(Member author, Pageable pageable);

    List<Recruitment> findAllByLocation(Double latitude, Double longitude, Double bound);

}

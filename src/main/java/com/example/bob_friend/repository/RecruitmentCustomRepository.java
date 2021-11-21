package com.example.bob_friend.repository;

import com.example.bob_friend.model.dto.Condition;
import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface RecruitmentCustomRepository {
    Page<Recruitment> searchByTitle(Condition.Search search, Pageable pageable);

    Page<Recruitment> searchByContent(Condition.Search search, Pageable pageable);

    Page<Recruitment> searchByRestaurant(Condition.Search search, Pageable pageable);

    Page<Recruitment> searchByAll(Condition.Search search, Pageable pageable);

    Page<Recruitment> findAll(Pageable pageable);

    Page<Recruitment> findAllAvailable(Member currentMember ,Pageable pageable);

    Page<Recruitment> findAllJoined(Member currentMember, Pageable pageable);

    Page<Recruitment> findAllByRestaurant(Condition.Search searchCondition, Pageable pageable);

    Page<Recruitment> findAllByAuthor(Member author, Pageable pageable);

}

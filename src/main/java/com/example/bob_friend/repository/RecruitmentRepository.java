package com.example.bob_friend.repository;

import com.example.bob_friend.model.entity.Member;
import com.example.bob_friend.model.entity.Recruitment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
    Page<Recruitment> findAllByRestaurantNameAndRestaurantAddress(String restaurantName, String restaurantAddress, Pageable pageable);

    Page<Recruitment> findAllByRestaurantAddress(String restaurantAddress, Pageable pageable);

    Page<Recruitment> findAllByAuthor(Member member, Pageable pageable);
    List<Recruitment> findAllByAuthor(Member author);
}
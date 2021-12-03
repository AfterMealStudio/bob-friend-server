package com.example.bobfriend.repository;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Report;
import com.example.bobfriend.model.entity.Writing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WritingReportRepository extends JpaRepository<Report, Long> {
    boolean existsByMemberAndWriting(Member member, Writing writing);

    void deleteAllByWriting(Writing writing);

    void deleteAllByMember(Member member);

}

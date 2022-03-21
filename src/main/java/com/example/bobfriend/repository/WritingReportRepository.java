package com.example.bobfriend.repository;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Report;
import com.example.bobfriend.model.entity.Writing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WritingReportRepository extends JpaRepository<Report, Long> {

    List<Report> findAllByMember(Member member);

    boolean existsByMemberAndWriting(Member member, Writing writing);

    void deleteAllByWriting(Writing writing);

    void deleteAllByMember(Member member);

}

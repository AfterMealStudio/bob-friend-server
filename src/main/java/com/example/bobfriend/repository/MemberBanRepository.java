package com.example.bobfriend.repository;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.MemberBan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberBanRepository extends JpaRepository<MemberBan, Long> {
    Optional<MemberBan> findByMember(Member member);

    List<MemberBan> findAllByMember(Member member);

    Optional<MemberBan> findByMemberAndBannedMember(Member member, Member bannedMember);

    void deleteAllByMember(Member member);

    void deleteAllByBannedMember(Member bannedMember);
}

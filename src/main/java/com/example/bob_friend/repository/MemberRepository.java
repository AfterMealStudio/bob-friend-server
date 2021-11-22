package com.example.bob_friend.repository;

import com.example.bob_friend.model.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByNickname(String nickname);

    Optional<Member> findMemberByEmail(String email);

    @EntityGraph(attributePaths = "authorities")
    Optional<Member> findMemberWithAuthoritiesByEmail(String email);

    boolean existsMemberByEmail(String email);

    boolean existsMemberByNickname(String email);
}

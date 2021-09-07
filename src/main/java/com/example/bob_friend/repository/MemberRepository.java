package com.example.bob_friend.repository;

import com.example.bob_friend.model.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member getMemberByEmail(String email);

    Optional<Member> findMemberByUsername(String username);

    @EntityGraph(attributePaths = "authorities")
    Optional<Member> findMemberWithAuthoritiesByUsername(String username);

    void deleteByUsername(String username);

    boolean existsMemberByUsername(String username);

    boolean existsMemberByEmail(String email);

    boolean existsMemberByNickname(String email);
}

package com.example.bobfriend.util;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Sex;

import java.time.LocalDate;

public class TestMemberGenerator {

    public Member getTestAuthor() {
        Member author = Member.builder()
                .email("testAuthor@test.com")
                .nickname("testAuthor")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.parse("1997-06-04"))
                .build();
        author.setup();
        return author;
    }

    public Member getTestMember() {
        Member member = Member.builder()
                .email("testMember@test.com")
                .nickname("testMember")
                .password("testPassword")
                .sex(Sex.FEMALE)
                .birth(LocalDate.parse("1997-06-04"))
                .build();
        member.setup();
        return member;
    }

}


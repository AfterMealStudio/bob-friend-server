package com.example.bobfriend.ban;

import com.example.bobfriend.model.entity.Authority;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.MemberBan;
import com.example.bobfriend.model.entity.Sex;
import com.example.bobfriend.repository.MemberBanRepository;
import com.example.bobfriend.repository.MemberRepository;
import com.example.bobfriend.service.MemberBanService;
import com.example.bobfriend.service.MemberService;
import com.example.bobfriend.testconfig.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ActiveProfiles("test")
@Import({TestConfig.class, MemberBanService.class, MemberService.class, BCryptPasswordEncoder.class})
public class MemberBanTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberBanRepository memberBanRepository;
    @Autowired
    MemberBanService memberBanService;
    @Autowired
    PasswordEncoder passwordEncoder;

    Member member1, member2;

    @BeforeEach
    void setUp() {
        member1 = Member.builder()
                .id(0L)
                .email("testEmail")
                .nickname("testUser")
                .password(passwordEncoder.encode("1234"))
                .birth(LocalDate.now())
                .sex(Sex.FEMALE)
                .reportCount(0)
                .accumulatedReports(0)
                .rating(0.0)
                .numberOfJoin(0)
                .authorities(Collections.singleton(Authority.ROLE_USER))
                .agree(true)
                .active(true)
                .build();

        member1.setup();

        member2 = Member.builder()
                .id(0L)
                .email("testEmail2")
                .nickname("testUser2")
                .password(passwordEncoder.encode("1234"))
                .birth(LocalDate.now())
                .sex(Sex.FEMALE)
                .reportCount(0)
                .accumulatedReports(0)
                .rating(0.0)
                .numberOfJoin(0)
                .authorities(Collections.singleton(Authority.ROLE_USER))
                .agree(true)
                .active(true)
                .build();

        member2.setup();

        memberRepository.save(member1);
        memberRepository.save(member2);
    }

    @Test
    @DisplayName("ban 메서드를 호출하면 MemberBan 객체를 저장한다.")
    void banTest() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        member1.getEmail(),
                        member1.getPassword(),
                        Collections.singleton(
                                new SimpleGrantedAuthority("ROLE_USER"))
                ));

        memberBanService.ban(member2.getNickname());

        MemberBan byMember = memberBanRepository.findByMember(member1).get();

        assertThat(byMember.getMember()).isEqualTo(member1);
        assertThat(byMember.getBannedMember()).isEqualTo(member2);
    }

    @Test
    @DisplayName("cancel 메서드를 호출하면 MemberBan 객체를 삭제한다.")
    void cancelTest() {
        Member member3 = Member.builder()
                .id(0L)
                .email("testEmail3")
                .nickname("testUser3")
                .password(passwordEncoder.encode("1234"))
                .birth(LocalDate.now())
                .sex(Sex.FEMALE)
                .reportCount(0)
                .accumulatedReports(0)
                .rating(0.0)
                .numberOfJoin(0)
                .authorities(Collections.singleton(Authority.ROLE_USER))
                .agree(true)
                .active(true)
                .build();

        member3.setup();

        memberRepository.save(member3);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        member1.getEmail(),
                        member1.getPassword(),
                        Collections.singleton(
                                new SimpleGrantedAuthority("ROLE_USER"))
                ));

        memberBanService.ban(member2.getNickname());
        memberBanService.ban(member3.getNickname());

        memberBanService.cancel(member2.getNickname());

        List<MemberBan> byMember = memberBanRepository.findAllByMember(member1);

        MemberBan memberBan = byMember.get(0);
        assertThat(memberBan.getMember()).isEqualTo(member1);
        assertThat(memberBan.getBannedMember()).isEqualTo(member3);
    }


}

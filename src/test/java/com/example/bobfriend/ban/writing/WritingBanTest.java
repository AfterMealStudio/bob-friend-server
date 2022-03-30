package com.example.bobfriend.ban.writing;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.RecruitmentBan;
import com.example.bobfriend.repository.MemberRepository;
import com.example.bobfriend.repository.RecruitmentBanRepository;
import com.example.bobfriend.repository.RecruitmentRepository;
import com.example.bobfriend.service.RecruitmentBanService;
import com.example.bobfriend.testconfig.JpaTestConfig;
import com.example.bobfriend.util.TestAuthenticationUtil;
import com.example.bobfriend.util.TestMemberGenerator;
import com.example.bobfriend.util.TestRecruitmentGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import({JpaTestConfig.class, RecruitmentBanService.class})
@DataJpaTest
public class WritingBanTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RecruitmentRepository recruitmentRepository;
    @Autowired
    private RecruitmentBanService recruitmentBanService;
    @Autowired
    private RecruitmentBanRepository recruitmentBanRepository;
    private TestRecruitmentGenerator testRecruitmentGenerator = new TestRecruitmentGenerator();
    private TestMemberGenerator testMemberGenerator = new TestMemberGenerator();
    private Member testAuthor;

    @BeforeEach
    void setup() {
        testAuthor = testMemberGenerator.getTestAuthor();
        memberRepository.save(testAuthor);

        testRecruitmentGenerator.setAuthor(testAuthor);
    }

    @Test
    @DisplayName("ban 메소드를 호출하면 RecruitmentBan 객체가 db에 저장된다.")
    void banRecruitmentTest() {
        Recruitment testRecruitment = saveAndGetTestRecruitment();
        Member testMember = testMemberGenerator.getTestMember();
        memberRepository.save(testMember);

        TestAuthenticationUtil.setAuthentication(testMember);
        recruitmentBanService.ban(testRecruitment.getId());

        Optional<RecruitmentBan> byMemberAndRecruitment =
                recruitmentBanRepository.findByMemberAndRecruitment(testMember, testRecruitment);

        assertTrue(byMemberAndRecruitment.isPresent());
        assertThat(byMemberAndRecruitment.get().getMember()).isEqualTo(testMember);
        assertThat(byMemberAndRecruitment.get().getRecruitment()).isEqualTo(testRecruitment);
    }

    @Test
    @DisplayName("차단된 게시물은 현재사용자에게 조회되지 않는다.")
    void bannedWritingDoesNotDisplayed() {
        Recruitment testRecruitment1 = saveAndGetTestRecruitment();
        Recruitment testRecruitment2 = saveAndGetTestRecruitment();
        TestAuthenticationUtil.setAuthentication(testAuthor);

        recruitmentBanService.ban(testRecruitment1.getId());

        List<Recruitment> all = recruitmentRepository.findAll();

        assertFalse(all.contains(testRecruitment1));
    }

    private Recruitment saveAndGetTestRecruitment() {
        Recruitment testRecruitment = testRecruitmentGenerator.getTestRecruitment();
        recruitmentRepository.save(testRecruitment);
        return testRecruitment;
    }
}

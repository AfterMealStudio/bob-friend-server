package com.example.bobfriend.model.dto.member;

import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Sex;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Response {
    private Long id;
    private String email;
    private String nickname;
    private Integer age;
    private Sex sex;
    private Integer reportCount;
    private Integer accumulatedReports;
    private Double rating;
    private Boolean agree;
    private Boolean active;

    public Response(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.age = member.getAge();
        this.sex = member.getSex();
        this.reportCount = member.getReportCount();
        this.accumulatedReports = member.getAccumulatedReports();
        this.rating = member.getRating();
        this.agree = member.getPrivacyAgreement();
        this.active = member.isActive();
    }

}

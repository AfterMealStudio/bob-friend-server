package com.example.bobfriend.scheduler;

import com.example.bobfriend.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Scheduler {
    private final RecruitmentService recruitmentService;

    @Scheduled(cron = "0 0/1 * * * ?")// 1분 마다 실행
    public void recruitmentExpireSchedule() {
        recruitmentService.expireRecruitment();
    }
}

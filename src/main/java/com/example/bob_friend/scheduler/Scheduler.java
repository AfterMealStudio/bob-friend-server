package com.example.bob_friend.scheduler;

import com.example.bob_friend.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

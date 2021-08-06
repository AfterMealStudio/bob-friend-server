package com.example.bob_friend;

import com.example.bob_friend.service.RecruitmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BobFriendApplicationTests {

    @Autowired
    RecruitmentService recruitmentService;

    @Test
    void contextLoads() {
    }

    public void getAll() {

    }
}

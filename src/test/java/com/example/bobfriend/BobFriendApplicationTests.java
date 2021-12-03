package com.example.bobfriend;

import com.example.bobfriend.service.RecruitmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BobFriendApplicationTests {

    @Autowired
    RecruitmentService recruitmentService;
}

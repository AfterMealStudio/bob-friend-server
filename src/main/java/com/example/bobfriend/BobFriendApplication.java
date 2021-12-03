package com.example.bobfriend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BobFriendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BobFriendApplication.class, args);
    }

}

package com.example.bobfriend.model.dto.member;

import com.example.bobfriend.model.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Request {
    protected String email;
    protected String nickname;
    protected String password;
    protected Sex sex;
    protected LocalDate birth;
    protected Boolean agree;
}
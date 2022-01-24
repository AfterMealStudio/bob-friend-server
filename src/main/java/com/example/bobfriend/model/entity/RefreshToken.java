package com.example.bobfriend.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Table(name = "refresh_token")
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    private String id;
    @Column(name = "token")
    private String refreshToken;

    public void setRefreshToken(String token) {
        this.refreshToken = token;
    }


}

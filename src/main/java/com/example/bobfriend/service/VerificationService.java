package com.example.bobfriend.service;

/**
 * 본인 계정임을 검증하기 위한 서비스
 */
public interface VerificationService {

    void sendVerification(String contact);

    boolean confirm(String contact, String code);
}

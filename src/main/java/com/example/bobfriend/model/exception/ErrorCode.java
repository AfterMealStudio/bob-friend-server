package com.example.bobfriend.model.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    /*
    CONFLICT
     */
    AlreadyJoined(HttpStatus.CONFLICT),
    AlreadyReportedExeption(HttpStatus.CONFLICT),
    RecruitmentIsFullException(HttpStatus.CONFLICT),

    /*
    NOT_FOUND
     */
    CommentNotFoundException(HttpStatus.NOT_FOUND),
    RecruitmentNotFoundException(HttpStatus.NOT_FOUND),
    ReplyNotFoundException(HttpStatus.NOT_FOUND),
    RefreshTokenNotFoundException(HttpStatus.NOT_FOUND),

    /*
    BAD_REQUEST
     */
    JwtInvalidException(HttpStatus.BAD_REQUEST),
    RefreshTokenNotMatchException(HttpStatus.BAD_REQUEST),

    /*
    FORBIDDEN
     */
    MemberNotAllowedException(HttpStatus.FORBIDDEN),
    RecruitmentNotActiveException(HttpStatus.FORBIDDEN),
    MemberNotVerifiedException(HttpStatus.FORBIDDEN);


    private HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }
}

package com.example.bobfriend.util;

import com.example.bobfriend.model.entity.Comment;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;

public class TestCommentGenerator {

    private Member testAuthor;
    private Recruitment testRecruitment;

    public void setTestAuthor(Member testAuthor) {
        this.testAuthor = testAuthor;
    }

    public void setTestRecruitment(Recruitment testRecruitment) {
        this.testRecruitment = testRecruitment;
    }

    public Comment getTestComment() {
        Comment comment = Comment.builder()
                .author(testAuthor)
                .recruitment(testRecruitment)
                .content("testContent")
                .build();
        comment.setup();
        return comment;
    }
}

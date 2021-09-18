package com.example.bob_friend.document;

import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

public class RecruitmentDocument {
    public static RestDocumentationResultHandler getAllRecruitments() {
        return document("recruitments/get-all-recruitments"
//                ,
//                getDocumentRequest(),
//                getDocumentResponse(),
                );
    }
}

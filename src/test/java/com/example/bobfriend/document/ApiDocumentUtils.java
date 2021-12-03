package com.example.bobfriend.document;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;

public interface ApiDocumentUtils {
    static OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(prettyPrint());
    }

    static OperationResponsePreprocessor getDocumentResponse() {
        return preprocessResponse(prettyPrint());
    }


    static MockHttpServletRequestBuilder getRequestBuilder(MockHttpServletRequestBuilder mockHttpServletRequestBuilder) {
        return mockHttpServletRequestBuilder
                .header("Authorization", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJxd3cxNTUyQG5hdmVyLmNvbSIsInJvbGVzIjoiUk9MRV9VU0VSIiwiZXhwIjoxNjM1MzEzNjA5fQ.ljbhPUb2lQQ700-sUftbJUX_taxAnaVR4fVwCJDLi2s")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

}

package com.example.bobfriend.model.dto.recruitment;

import com.example.bobfriend.model.entity.Recruitment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ResponseCollection extends BaseResponse{
    public ResponseCollection(Recruitment recruitment) {
        super(recruitment);
    }
}

package com.example.bobfriend.model.dto.Recruitment;

import com.example.bobfriend.model.entity.Recruitment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ResponseCollection extends BaseResponse{
    public ResponseCollection(Recruitment recruitment) {
        super(recruitment);
    }
}

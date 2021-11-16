package com.example.bob_friend.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchCondition {
    private String restaurantName;
    private String restaurantAddress;
    private String keyword;
    private String start, end;

}

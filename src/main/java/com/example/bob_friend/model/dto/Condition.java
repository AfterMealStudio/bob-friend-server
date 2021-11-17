package com.example.bob_friend.model.dto;

import lombok.Getter;
import lombok.Setter;

public class Condition {
    public enum SearchCategory {
        title, content, place, time, all
    }

    public enum SearchType {
        available, all, joined, owned
    }

    @Setter
    @Getter
    public static class Search {
        private String restaurantName;
        private String restaurantAddress;
        private String keyword;
        private String start, end;
    }
}

package com.example.bob_friend.repository;

import com.querydsl.core.types.Predicate;

public interface StatementStrategy {
    Predicate[] makeBooleanExpression();
}

package com.example.bobfriend.repository;

import com.querydsl.core.types.Predicate;

public interface StatementStrategy {
    Predicate[] makeBooleanExpression();
}

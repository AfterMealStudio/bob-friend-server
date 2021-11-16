package com.example.bob_friend.repository;

import com.example.bob_friend.model.dto.SearchCondition;
import com.example.bob_friend.model.entity.Recruitment;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.bob_friend.model.entity.QRecruitment.recruitment;

@RequiredArgsConstructor
@Repository
public class RecruitmentCustomRepositoryImpl implements RecruitmentCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Recruitment> searchByTitle(String keyword, Pageable pageable) {
        BooleanExpression like = recruitment.title.like(keyword);
        return getRecruitments(pageable, like);
    }

    @Override
    public Page<Recruitment> searchByContent(String keyword, Pageable pageable) {
        BooleanExpression like = recruitment.content.like(keyword);
        return getRecruitments(pageable, like);
    }

    @Override
    public Page<Recruitment> searchByRestaurant(String keyword, Pageable pageable) {
        BooleanExpression like = recruitment.restaurantName.like(keyword);
        return getRecruitments(pageable, like);
    }

    @Override
    public Page<Recruitment> searchByAppointmentTime(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        BooleanExpression between = recruitment.appointmentTime.between(start, end);
        return getRecruitments(pageable, between);
    }

    @Override
    public Page<Recruitment> searchByAll(String keyword, Pageable pageable) {
        BooleanExpression booleanExpression =
                recruitment.title.like(keyword).or(
                        recruitment.content.like(keyword).or(
                                recruitment.restaurantName.like(keyword)
                        )
                );
        return getRecruitments(pageable, booleanExpression);
    }

    public Page<Recruitment> findAllByRestaurant(SearchCondition searchCondition, Pageable pageable) {
        JPAQuery<Recruitment> query = getActiveRecruitments()
                .where(
                        eqRestaurantName(searchCondition.getRestaurantName()),
                        eqRestaurantAddress(searchCondition.getRestaurantAddress())
                );
        return getPage(pageable, query);
    }


    @Override
    public Page<Recruitment> findAll(Pageable pageable) {
        JPAQuery<Recruitment> query = getActiveRecruitments();
        return getPage(pageable, query);
    }


    private BooleanExpression eqRestaurantName(String restaurantName) {
        if (!StringUtils.hasLength(restaurantName)) return null;
        return recruitment.restaurantName.eq(restaurantName);
    }

    private BooleanExpression eqRestaurantAddress(String restaurantAddress) {
        if (!StringUtils.hasLength(restaurantAddress)) return null;
        return recruitment.restaurantAddress.eq(restaurantAddress);
    }

    private Page<Recruitment> getRecruitments(Pageable pageable, BooleanExpression booleanExpression) {
        JPAQuery<Recruitment> query = getActiveRecruitments()
                .where(booleanExpression);
        return getPage(pageable, query);
    }

    private Page getPage(Pageable pageable, JPAQuery<Recruitment> where) {
        List list = where
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return PageableExecutionUtils.getPage(list, pageable, () -> where.fetchCount());
    }

    private JPAQuery<Recruitment> getActiveRecruitments() {
        return jpaQueryFactory.selectFrom(recruitment).where(
                recruitment.active.eq(true)
        );
    }
}

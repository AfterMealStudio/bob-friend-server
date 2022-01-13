package com.example.bobfriend.repository;

import com.example.bobfriend.model.dto.Condition;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.Sex;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
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

import static com.example.bobfriend.model.entity.QRecruitment.recruitment;

@RequiredArgsConstructor
@Repository
public class RecruitmentCustomRepositoryImpl implements RecruitmentCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Recruitment> searchByTitle(Condition.Search search, Pageable pageable) {
        JPAQuery<Recruitment> query = getFilteringQueryFromPredicates(
                recruitment.title.contains(search.getKeyword()),
                betweenTime(search.getStart(), search.getEnd()));
        return getPage(pageable, query);
    }


    @Override
    public Page<Recruitment> searchByContent(Condition.Search search, Pageable pageable) {
        JPAQuery<Recruitment> query = getFilteringQueryFromPredicates(
                recruitment.content.contains(search.getKeyword()),
                betweenTime(search.getStart(), search.getEnd()));
        return getPage(pageable, query);
    }


    @Override
    public Page<Recruitment> searchByRestaurant(Condition.Search search, Pageable pageable) {
        JPAQuery<Recruitment> query = getFilteringQueryFromPredicates(
                recruitment.restaurantName.contains(search.getKeyword()),
                betweenTime(search.getStart(), search.getEnd()));
        return getPage(pageable, query);
    }


    @Override
    public Page<Recruitment> searchByAll(Condition.Search search, Pageable pageable) {
        JPAQuery<Recruitment> query = getFilteringQueryFromPredicates(
                recruitment.title.contains(search.getKeyword()).or(
                        recruitment.content.contains(search.getKeyword()).or(
                                recruitment.restaurantName.contains(search.getKeyword())
                        )),
                betweenTime(search.getStart(), search.getEnd()));
        return getPage(pageable, query);
    }


    @Override
    public Page<Recruitment> findAllByAuthor(Member author, Pageable pageable) {
        JPAQuery<Recruitment> query = getFilteringQueryFromPredicates(
                recruitment.author.eq(author));
        return getPage(pageable, query);
    }


    @Override
    public Page<Recruitment> findAllByAddress(String address, Pageable pageable) {
        JPAQuery<Recruitment> query = getFilteringQueryFromPredicates(
                eqRestaurantAddress(address)
        );
        return getPage(pageable, query);
    }


    @Override
    public Page<Recruitment> findAllAvailable(Member currentMember, Pageable pageable) {
        JPAQuery<Recruitment> query = getFilteringQueryFromPredicates(
                recruitment.totalNumberOfPeople.gt(recruitment.members.size()),
                recruitment.author.ne(currentMember),
                recruitment.members.contains(currentMember).not(),
                recruitment.sexRestriction.eq(currentMember.getSex()).or(
                        recruitment.sexRestriction.eq(Sex.NONE)
                ),
                betweenAgeRestrictionRange(currentMember));
        return getPage(pageable, query);
    }


    private BooleanExpression betweenAgeRestrictionRange(Member currentMember) {
        BooleanExpression restrictExist = recruitment.ageRestrictionStart.loe(currentMember.getAge())// less or equal
                .and(recruitment.ageRestrictionEnd.goe(currentMember.getAge()));
        BooleanExpression restrictNotExist = recruitment.ageRestrictionStart.isNull()
                .and(recruitment.ageRestrictionEnd.isNull());
        return restrictExist.or(restrictNotExist);// greater or equal
    }


    @Override
    public Page<Recruitment> findAllJoined(Member currentMember, Pageable pageable) {
        JPAQuery<Recruitment> query = getFilteringQueryFromPredicates(
                recruitment.members.contains(currentMember));
        return getPage(pageable, query);
    }


    @Override
    public List<Recruitment> findAllByLocation(Double latitude,
                                               Double longitude,
                                               Double bound) {
        return getFilteringQueryFromPredicates(new Predicate[]{
                recruitment.latitude.between(latitude - bound, latitude + bound).and(
                        recruitment.longitude.between(longitude - bound, longitude + bound)
                )
        }).fetch();
    }


    private JPAQuery<Recruitment> getFilteringQueryFromPredicates(Predicate... predicates) {
        return jpaQueryFactory.selectFrom(recruitment)
                .where(recruitment.active.eq(true))
                .where(predicates);
    }


    private BooleanExpression eqRestaurantName(String restaurantName) {
        if (!StringUtils.hasLength(restaurantName)) return null;
        return recruitment.restaurantName.eq(restaurantName);
    }


    private BooleanExpression betweenTime(LocalDateTime start, LocalDateTime end) {
        if (start == null) return null;
        if (end == null) return null;
        return recruitment.appointmentTime.between(start, end);
    }


    private BooleanExpression eqRestaurantAddress(String restaurantAddress) {
        if (!StringUtils.hasLength(restaurantAddress)) return null;
        return recruitment.restaurantAddress.eq(restaurantAddress);
    }


    private Page getPage(Pageable pageable, JPAQuery<Recruitment> where) {
        List list = where
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(pageable.getSort().get()
                        .map(order -> {
                            Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                            Path<Recruitment> fieldPath =
                                    Expressions.path(
                                            Recruitment.class,
                                            recruitment,
                                            order.getProperty());
                            return new OrderSpecifier(direction, fieldPath);
                        }).toArray(OrderSpecifier[]::new))
                .fetch();
        return PageableExecutionUtils.getPage(list, pageable, () -> where.fetchCount());
    }

}

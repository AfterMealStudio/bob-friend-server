package com.example.bobfriend.repository;

import com.example.bobfriend.model.dto.Condition;
import com.example.bobfriend.model.entity.Member;
import com.example.bobfriend.model.entity.Recruitment;
import com.example.bobfriend.model.entity.Sex;
import com.querydsl.core.types.Predicate;
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

import static com.example.bobfriend.model.entity.QRecruitment.recruitment;

@RequiredArgsConstructor
@Repository
public class RecruitmentCustomRepositoryImpl implements RecruitmentCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Recruitment> searchByTitle(Condition.Search search, Pageable pageable) {
        return getPageFromStatement(pageable, () -> new Predicate[]{
                recruitment.title.contains(search.getKeyword()),
                betweenTime(search.getStart(), search.getEnd())
        });
    }

    @Override
    public Page<Recruitment> searchByContent(Condition.Search search, Pageable pageable) {
        return getPageFromStatement(pageable, () -> new Predicate[]{
                recruitment.content.contains(search.getKeyword()),
                betweenTime(search.getStart(), search.getEnd())
        });
    }

    @Override
    public Page<Recruitment> searchByRestaurant(Condition.Search search, Pageable pageable) {
        return getPageFromStatement(pageable, () -> new Predicate[]{
                recruitment.restaurantName.contains(search.getKeyword()),
                betweenTime(search.getStart(), search.getEnd())
        });
    }


//    @Override
//    public Page<Recruitment> searchByAppointmentTime(LocalDateTime start, LocalDateTime end, Pageable pageable) {
//        return getPageFromStatement(pageable, () -> new Predicate[]{
//                betweenTime(start, end)
//        });
//    }

    @Override
    public Page<Recruitment> searchByAll(Condition.Search search, Pageable pageable) {
        return getPageFromStatement(pageable, () -> new Predicate[]{
                        recruitment.title.contains(search.getKeyword()).or(
                                recruitment.content.contains(search.getKeyword()).or(
                                        recruitment.restaurantName.contains(search.getKeyword())
                                )),
                        betweenTime(search.getStart(), search.getEnd())
                }
        );
    }


    @Override
    public Page<Recruitment> findAllByAuthor(Member author, Pageable pageable) {
        return getPageFromStatement(pageable, () -> new Predicate[]{
                recruitment.author.eq(author)
        });
    }

    @Override
    public Page<Recruitment> findAll(Pageable pageable) {
        return getPageFromStatement(pageable, () -> new Predicate[]{});
    }

    @Override
    public Page<Recruitment> findAllByRestaurant(Condition.Search searchCondition, Pageable pageable) {
        return getPageFromStatement(pageable, () -> new Predicate[]{
                eqRestaurantName(searchCondition.getRestaurantName()),
                eqRestaurantAddress(searchCondition.getRestaurantAddress())
        });
    }

    @Override
    public Page<Recruitment> findAllAvailable(Member currentMember, Pageable pageable) {
        return getPageFromStatement(pageable, () -> new Predicate[]{
                recruitment.totalNumberOfPeople.gt(recruitment.members.size()),
                recruitment.author.ne(currentMember),
                recruitment.members.contains(currentMember).not(),
                recruitment.sexRestriction.eq(currentMember.getSex()).or(
                        recruitment.sexRestriction.eq(Sex.NONE)
                )
        });
    }

    @Override
    public Page<Recruitment> findAllJoined(Member currentMember, Pageable pageable) {
        return getPageFromStatement(pageable, () -> new Predicate[]{
                recruitment.members.contains(currentMember)
        });
    }


    private Page<Recruitment> getPageFromStatement(Pageable pageable, StatementStrategy statementStrategy) {
        Predicate[] booleanExpression = statementStrategy.makeBooleanExpression();
        JPAQuery<Recruitment> query = getActiveRecruitments()
                .where(
                        booleanExpression
                );
        return getPage(pageable, query);
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
                .fetch();
        return PageableExecutionUtils.getPage(list, pageable, () -> where.fetchCount());
    }

    private JPAQuery<Recruitment> getActiveRecruitments() {
        return jpaQueryFactory.selectFrom(recruitment).where(
                recruitment.active.eq(true)
        );
    }
}

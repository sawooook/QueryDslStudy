package study.qurtydsl.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.util.StringUtils;
import study.qurtydsl.dto.MemberSerachCondition;
import study.qurtydsl.dto.MemberTeamDto;
import study.qurtydsl.dto.QMemberTeamDto;

import javax.persistence.EntityManager;
import java.awt.print.Pageable;
import java.util.List;

import static study.qurtydsl.entity.QMember.member;
import static study.qurtydsl.entity.QTeam.team;

/*
* impl을 하는건 규칙임
*
* */
public class MemberRepositoryImpl implements  MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public MemberRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<MemberTeamDto> search(MemberSerachCondition condition) {
        return jpaQueryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        userNameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .fetch();
    }

    /*
    *
    * pageble -> offset이나 전체 페이지를 알 수 있음
    * */
    @Override
    public Page<MemberTeamDto> searchPageSimple(MemberSerachCondition condition, Pageable pageable) {
        QueryResults<MemberTeamDto> res = jpaQueryFactory
                .select(new QMemberTeamDto(
                        member.id.as("memberId"),
                        member.username,
                        member.age,
                        team.id.as("teamId"),
                        team.name.as("teamName")
                ))
                .from(member)
                .leftJoin(member.team, team)
                .where(
                        userNameEq(condition.getUsername()),
                        teamNameEq(condition.getTeamName()),
                        ageGoe(condition.getAgeGoe()),
                        ageLoe(condition.getAgeLoe())
                )
                .offset(pageable.getNumberOfPages())
                .limit(pageable.getNumberOfPages())
                .fetchResults();
        List<MemberTeamDto> content = res.getResults();
        long total = res.getTotal();

        return new PageImpl<T>(content, pageable, total);
    }

    @Override
    public Page<MemberTeamDto> searchPageComplex(MemberSerachCondition condition, Pageable pageable) {
        return null;
    }

    private BooleanExpression ageGoe(Integer ageGoe) {
        return ageGoe != null ? member.age.goe(ageGoe) : null;
    }

    private BooleanExpression ageLoe(Integer ageLoe) {
        return ageLoe != null ? member.age.goe(ageLoe) : null;
    }


    private BooleanExpression userNameEq(String username) {
        return StringUtils.hasText(username) ? member.username.eq(username) : null;
    }
    private BooleanExpression teamNameEq(String teamName) {
        return StringUtils.hasText(teamName) ? team.name.eq(teamName) : null;
    }
}

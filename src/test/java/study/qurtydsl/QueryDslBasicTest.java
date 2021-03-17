package study.qurtydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.hibernate.criterion.Projection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import study.qurtydsl.dto.MemberDto;
import study.qurtydsl.dto.QMemberDto;
import study.qurtydsl.dto.UserDto;
import study.qurtydsl.entity.Member;
import study.qurtydsl.entity.QMember;
import study.qurtydsl.entity.QTeam;
import study.qurtydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import java.util.List;

import static study.qurtydsl.entity.QMember.member;
import static study.qurtydsl.entity.QTeam.*;

@SpringBootTest
@Transactional
@Commit
@WebAppConfiguration
public class QueryDslBasicTest {

    @Autowired
    EntityManager em;

    @BeforeEach
    public void before() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }
    @Test
    public void startQueryDsl() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QMember m = new QMember("m");
        Member find = queryFactory.select(m)
                .where(m.username.eq("member1"))
                .fetchOne();

        Assertions.assertThat(find.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        Member find = queryFactory
                .selectFrom(member).where(member.username.eq("member1")
                        .and(member.age.eq(Integer.valueOf("10")))).fetchOne();
        Assertions.assertThat(find.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search2() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        queryFactory
                .selectFrom(member)
                .fetch();
        queryFactory.selectFrom(member)
                .fetchOne();
        queryFactory.selectFrom(member)
                .fetchFirst();
        QueryResults<Member> result = queryFactory.selectFrom(member)
                .fetchResults();

        result.getTotal();
        List<Member> results = result.getResults();

    }

    @Test
    public void sort() {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Member> fetch = queryFactory.selectFrom(member)
                .where(
                        member.age.eq(100))
                .orderBy(
                        member.age.desc(),
                        member.username.asc().nullsLast()).fetch();

        for (Member member : fetch) {
            System.out.println("==========================");
            System.out.println(member);
        }
    }

    @Test
    public void paging1() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        queryFactory.selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();
    }

    @Test
    public void aggregation() {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Tuple> result = queryFactory.select(
                member.count(),
                member.age.sum(),
                member.age.avg(),
                member.age.max(),
                member.age.min()
        )
                .from(member)
                .fetch();

        Tuple tu = result.get(0);

    }

    @Test
    public void group() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Tuple> result = queryFactory.select(
                member.team.name,
                member.age.avg()
        ).from(member).join(member.team, team)
                .groupBy(team.name)
                .fetch();

        //given

        Tuple tuple = result.get(0);
        Tuple tuple1 = result.get(1);
        Assertions.assertThat(tuple.get(team.name)).isEqualTo("teamA");
        Assertions.assertThat(tuple1.get(team.name)).isEqualTo("teamB");

        //when
        
        //then
    }


    @Test
    public void aggregation1() {
        //given
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Integer> fetch = queryFactory.select(
                member.age.sum()
        ).from(member).fetch();


        //when

        fetch.get(0);
        //then

    }

    @Test
    public void groupBy() throws Exception {
        //given
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Tuple> result = queryFactory.select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team).fetch();

        //when

        //then

    }

    @Test
    public void join() throws Exception {
        //given
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Member> result = queryFactory.selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        Assertions.assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");


        //when

        //then

    }

    @Test
    public void thetaJoin() throws Exception {
        //given
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        Assertions.assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");




        //when

        //then

    }

    @Test
    public void joinOn() throws Exception {
        //given
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<Tuple> result = queryFactory.select(member, team)
                .from(member)
                .leftJoin(member.team, team)
                .on(team.name.eq("teamA")).fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
        //when

        //then

    }


    @Test
    public void thetaJoin2() throws Exception {
        //given
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        /*
        * 막조인할때는 id 값으로 하는게 아니라 username으로 하는것이기 떄문에
        * username으로 함
        *
        * */

        List<Tuple> result = queryFactory.select(member, team)
                .from(member)
                .leftJoin(team)
                .on(member.username.eq(team.name))
                .fetch();
        
    }
    /*
    * 성능 최적화 fetchJoin
    * 쿼리를 한방에 가져오는것을 말함
    * 많이사용함
    *
    * */

    @PersistenceUnit
    EntityManagerFactory entityManagerFactory;
    @Test
    public void jetchJoin() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        em.flush();
        em.clear();
        //given
        Member member = queryFactory.selectFrom(QMember.member)
                .where(QMember.member.username.eq("member1"))
                .fetchOne();

        boolean loaded = entityManagerFactory.getPersistenceUnitUtil()
                .isLoaded(member.getTeam());

        Assertions.assertThat(loaded).as("패치조인 미적용").isFalse();

        //when
        
        //then
    
    }

    @Test
    public void fetch2() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        em.flush();
        em.clear();
        //given
        Member member = queryFactory
                .selectFrom(QMember.member)
                .join(QMember.member.team, team).fetchJoin()
                .where(QMember.member.username.eq("member1"))
                .fetchOne();

        boolean loaded = entityManagerFactory.getPersistenceUnitUtil()
                .isLoaded(member.getTeam());

        Assertions.assertThat(loaded).as("패치조인 미적용").isFalse();
    }


    /*
    *
    *
    * */
    @Test
    public void subQuery() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        //given
        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(
                        member.age.eq(
                                JPAExpressions
                                        .select(memberSub.age.max())
                                        .from(memberSub))).fetch();


        //when

        //then

    }

    @Test
    public void subQueryGoe() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        //given
        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(
                        member.age.goe(
                                JPAExpressions
                                        .select(memberSub.age.max())
                                        .from(memberSub))).fetch();


        //when

        //then

    }

    @Test
    public void subQueryIn() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        //given
        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(
                        member.age.in(
                                JPAExpressions
                                        .select(memberSub.age.max())
                                        .from(memberSub)
                                        .where(memberSub.age.gt(10))
                        )).fetch();


        //when

        //then

    }

    @Test
    public void caseTest() throws Exception {
        //given
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<String> result = queryFactory.select(
                new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }

        //when

        //then

    }

    @Test
    public void test() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        //given
        List<Tuple> result = queryFactory.select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();


        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }


        //when

        //then

    }

    @Test
    public void concat() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        //given
        List<Tuple> result = queryFactory.select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();
        //when
        queryFactory.select(member.username.concat("-").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1")).fetch();
        //then

    }

    /*
    * tuple은
    *
    * */

    @Test
    public void simpleProjection() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        //given
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();

        //when

        //then

    }

    @Test
    public void tupleProjection() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        //given
        List<Tuple> result = queryFactory.select(member.username, member.age)
                .from(member)
                .fetch();


        //when

        for (Tuple tuple : result) {
            tuple.get(member.username);
        }
        //then

    }
    @Test
    public void findDtoByJPQL() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        //given


        //when

        //then

    }

    @Test
    public void findDtoBySetter() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        //given

        queryFactory.select(
                Projections.bean(MemberDto.class),
                member.username,
                member.age
        )
        .from(member)
        .fetch();


        //when

        //then

    }

    @Test
    public void filelds() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        //given
        queryFactory.select(
                Projections.fields(MemberDto.class),
                member.username,
                member.age
        )
                .from(member)
                .fetch();

    }
    @Test
    public void fileldsConstruct() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        //given
        queryFactory.select(
                Projections.constructor(MemberDto.class),
                member.username,
                member.age
        )
                .from(member)
                .fetch();

    }
    @Test
    public void findUserDto() throws Exception {
        QMember memberSub = new QMember("memberSub");
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        //given
        queryFactory.select(
                Projections.constructor(UserDto.class),
                member.username.as("name"),
                member.age,
                /*
                * subQuery
                * 필드가 다르면 Expression을 쓰면 됨
                * */
                ExpressionUtils.as(JPAExpressions
                    .select(memberSub.age.max())
                        .from(memberSub), "age")
        )
                .from(member)
                .fetch();

    }

    @Test
    public void findDtoByProjection() throws Exception {
        //given
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<MemberDto> result = queryFactory.select(new QMemberDto(
                member.username, member.age
        )).from(member)
                .fetch();

        //when

        //then

    }

    @Test
    public void booleanBuilder() throws Exception {
        //given
        String usernameParama = "member1";
        Integer ageParam= 10;

        List<Member> result = searchMember(usernameParama, ageParam);


        //when

        //then

    }

    private List<Member> searchMember(String usernameParama, Integer ageParam) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        BooleanBuilder builder = new BooleanBuilder();

        if (usernameParama != null) {
            builder.and(member.username.eq(usernameParama));
        }

        if (ageParam != null) {
            builder.and(member.age.eq(ageParam));
        }

        return queryFactory.selectFrom(member)
                .where(builder)
                .fetch();


    }

    @Test
    public void whereParam() throws Exception {
        String usernameParama = "member1";
        Integer ageParam= 10;

        List<Member> result = searchMember2(usernameParama, ageParam);

        //given


        //when

        //then

    }


    /*
    * queryDsl 동적 쿼리를 날릴때
    * boolean builder방식말고 where방식으로하자
    * */
    private List<Member> searchMember2(String usernameParama, Integer ageParam) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        return queryFactory
                .selectFrom(member)
                .where(allEq(usernameParama, ageParam)).fetch();
    }

    private BooleanExpression allEq(String usernameParama, Integer ageParam) {
        return usernameEq(usernameParama).and(ageEq(ageParam));
    }

    private BooleanExpression ageEq(Integer ageParam) {
        return ageParam != null ? member.age.eq(ageParam) : null;
    }

    private BooleanExpression usernameEq(String usernameParama) {
        if (usernameParama != null) {
            return member.username.eq(usernameParama);
        } else {
            return null;
        }
    }


    /*
    * 벌크 연산은 조심해야함
    * 영속성 컨텍스트에 올려놓은건데
    * 벌크 연산은 바로 DB에 떄림 -> 영속성 컨텍스트는 바뀌징낳음
    *
    * */
    @Test
    public void BulkUpdate() throws Exception {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        //given
        /*
        * lt 면 미만이란뜻
        * */
        long count = queryFactory.update(member)
                .set(member.username, "비회원")
                .where(member.age.lt(28)).execute();


        //when

        //then

    }

}

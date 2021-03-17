package study.qurtydsl.entity;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static study.qurtydsl.entity.QMember.member;

@SpringBootTest
class MemberTest {
    @Autowired
    EntityManager entityManager;

    JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);

    @Test
    public void testEntity() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        entityManager.persist(teamA);
        entityManager.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        List<Member> list = entityManager.createQuery("select m from Member m", Member.class).getResultList();

    }

    @Test
    public void sqlFunction() {
        jpaQueryFactory
                .select(
                        Expressions.stringTemplate(
                                "function('replace', {0}, {1}, {2})"
                                ,member.username, "member", "M"
                        )).from(member)
                .fetch();
    }

    @Test
    public void sqlFunction2() throws Exception {
        //given
        jpaQueryFactory.select(member.username)
                .from(member)
//                .where(member.username.eq(Expressions.stringTemplate(
//                        "function('lower', {0})", member.username
//                )))
                .where(member.username.eq(member.username.lower()))
                .fetch();


        //when

        //then

    }
}
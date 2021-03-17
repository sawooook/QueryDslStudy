package study.qurtydsl.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import study.qurtydsl.dto.MemberSerachCondition;
import study.qurtydsl.dto.MemberTeamDto;
import study.qurtydsl.entity.Member;
import study.qurtydsl.entity.Team;

import javax.persistence.EntityManager;

import java.awt.print.Pageable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager entityManager;
    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void basicTest() {
        Member member1 = new Member("member1", 10);
        memberJpaRepository.save(member1);
        Member find = memberJpaRepository.findById(member1.getId()).get();

        org.assertj.core.api.Assertions.assertThat(member1).isEqualTo(find);

        List<Member> result1 = memberJpaRepository.findAll_QueryDsl();
        org.assertj.core.api.Assertions.assertThat(find).isEqualTo(result1);

        List<Member> result2 = memberJpaRepository.findByUserName_QueryDsl("member1");
    }

    @Test
    public void seachTest() throws Exception {
        //given

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        entityManager.persist(teamA);
        entityManager.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        MemberSerachCondition memberSerachCondition = new MemberSerachCondition();
        memberSerachCondition.setAgeGoe(35);
        memberSerachCondition.setAgeGoe(40);
        memberSerachCondition.setTeamName("teamB");


        List<MemberTeamDto> teams = memberJpaRepository.serachByBuilder(memberSerachCondition);
        PageRequest pag = PageRequest.of(0, 3);


        memberRepository.searchPageSimple(memberSerachCondition, (Pageable) pag);
        //when

        //then

    }

}
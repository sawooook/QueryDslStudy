package study.qurtydsl.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import study.qurtydsl.entity.Member;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@Repository
@Transactional
class MemberRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void basicTest() {
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);

        memberRepository.findByUserName(member1.getUsername());

    }
}
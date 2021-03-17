package study.qurtydsl.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import study.qurtydsl.entity.Member;

import java.util.List;

/*
* 인터페이스는 여러개를 상속받을 수 있음
* 두개를 상속시킴
* */
public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom{
    //select M from Member m where m.username = ?
    List<Member> findByUserName(String username);

}

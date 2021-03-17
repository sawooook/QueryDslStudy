package study.qurtydsl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import study.qurtydsl.dto.MemberSerachCondition;
import study.qurtydsl.dto.MemberTeamDto;
import study.qurtydsl.repository.MemberJpaRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberCotroller {

    private final MemberJpaRepository memberJpaRepository;

    @GetMapping("/v1/members")
    public List<MemberTeamDto> searchMemberV1(MemberSerachCondition memberSerachCondition) {
        return memberJpaRepository.search(memberSerachCondition);
    }


}

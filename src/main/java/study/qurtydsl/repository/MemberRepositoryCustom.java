package study.qurtydsl.repository;

import com.querydsl.core.QueryResults;
import org.springframework.data.domain.Page;
import study.qurtydsl.dto.MemberSerachCondition;
import study.qurtydsl.dto.MemberTeamDto;

import java.awt.print.Pageable;
import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberTeamDto> search(MemberSerachCondition condition);
    Page<MemberTeamDto> searchPageSimple(MemberSerachCondition condition, Pageable pageable);
    Page<MemberTeamDto> searchPageComplex(MemberSerachCondition condition, Pageable pageable);
}

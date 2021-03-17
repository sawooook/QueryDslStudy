package study.qurtydsl.dto;

import lombok.Data;

@Data
public class MemberSerachCondition {
    private String username;
    private String teamName;
    private Integer ageGoe;
    private Integer ageLoe;
}

package com.nextClass.repository;

import com.nextClass.dto.MemberRequestDto;
import com.nextClass.entity.Member;
import com.nextClass.enums.GradeType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

import static com.nextClass.entity.QMember.member;

@Repository
@Transactional
public class LoginRepository {
    private final MemberRepository memberRepository;
    private final JPAQueryFactory queryFactory;

    LoginRepository(MemberRepository memberRepository, JPAQueryFactory queryFactory) {
        this.memberRepository = memberRepository;
        this.queryFactory = queryFactory;
    }
    /**
     * DB INSERT : MEMBER
     * @param MemberRequestDto
     **/
    public void saveMember(MemberRequestDto MemberRequestDto){
        Member member = Member.builder()
                .id(MemberRequestDto.getId())
                .name(MemberRequestDto.getName())
                .password(MemberRequestDto.getPassword())
                .member_grade(GradeType.getInstance(MemberRequestDto.getMember_grade()))
                .member_school(MemberRequestDto.getMember_school())
                .reg_date(LocalDateTime.now())
                .build();
        memberRepository.save(member);
    }


    /**
     * DB SELECT : MEMBER
     * @param key
     * @param value
     **/
    public Member getMemberByKeyValue(String key, String value){
        return queryFactory.selectFrom(member)
                .where(propertyEqByKeyValue(key, value))
                .fetchOne();
    }

    /**
     * DB SELECT : MEMBER
     * @param id
     **/
    public Member getMemberById(String id){
        return queryFactory.selectFrom(member)
                .where(member.id.eq(id))
                .fetchOne();
    }

    private BooleanExpression propertyEqByKeyValue(String key, String value) {
        if (key.equals("id")) {
            return member.id.eq(value);
        } else if (key.equals("email")) {
            return member.email.eq(value);
        }
        return null;
    }

}

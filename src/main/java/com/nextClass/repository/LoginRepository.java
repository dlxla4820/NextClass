package com.nextClass.repository;

import com.nextClass.dto.*;
import com.nextClass.entity.Member;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

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
     **/
    public void saveMember(MemberRequestDto MemberRequestDto, String encodePassword){
        Member member = Member.builder()
                .id(MemberRequestDto.getId())
                .name(MemberRequestDto.getName())
                .password(encodePassword)
                .email(MemberRequestDto.getEmail())
                .memberGrade(MemberRequestDto.getMemberGrade())
                .memberSchool(MemberRequestDto.getMemberSchool())
                .regDate(LocalDateTime.now())
                .build();
        memberRepository.save(member);
    }


    /**
     * DB SELECT : MEMBER
     **/
    public Member getMemberByKeyValue(String key, String value){
        return queryFactory.selectFrom(member)
                .where(propertyEqByKeyValue(key, value))
                .fetchOne();
    }

    /**
     * DB SELECT : MEMBER
     **/
    public Member getMemberById(String id){
        return queryFactory.selectFrom(member)
                .where(member.id.eq(id))
                .fetchOne();
    }
    /**
     * DB SELECT : MEMBER
     **/
    public Member getMemberByUuid(String uuid){
        return queryFactory.selectFrom(member)
                .where(Expressions.stringTemplate("HEX({0})", member.uuid).eq(uuid.replace("-","")))
                .fetchOne();
    }

    /**
     * DB SELECT : MEMBER
     **/
    public Member getMemberByEmail(String email){
        return queryFactory.selectFrom(member)
                .where(member.email.eq(email))
                .fetchOne();
    }
    /**
     * DB SELECT : MEMBER
     **/
    public String getMemberPasswordByUuid(String uuid){
        return queryFactory.select(member.password)
                .from(member)
                .where(Expressions.stringTemplate("HEX({0})", member.uuid).eq(uuid.replace("-","")))
                .fetchOne();
    }
    /**
     * DB SELECT : MEMBER
     **/
    public MemberInfoResponseDto getMyInfoByUuid(String uuid){
        return queryFactory.select(Projections.fields(MemberInfoResponseDto.class, member.id, member.name, member.email, member.memberGrade, member.memberSchool))
                .from(member)
                .where(Expressions.stringTemplate("HEX({0})", member.uuid).eq(uuid.replace("-","")))
                .fetchOne();
    }

    /**
     * DB UPDATE : MEMBER
     **/
    public void updateMember(MemberRequestDto memberRequestDto){
        queryFactory.update(member)
                .set(member.email, memberRequestDto.getEmail())
                .set(member.name, memberRequestDto.getName())
                .set(member.password, memberRequestDto.getPassword())
                .set(member.memberGrade, memberRequestDto.getMemberGrade())
                .set(member.memberSchool, memberRequestDto.getMemberSchool())
                .set(member.modDate, LocalDateTime.now())
                .where(member.id.eq(memberRequestDto.getId()))
                .execute();
    }
    /**
     * DB UPDATE : MEMBER
     **/
    public void updateMemberNormalInfo(String uuid, MemberChangeNormalInfoRequestDto requestDto){
        queryFactory.update(member)
                .set(member.name, requestDto.getName())
                .set(member.memberSchool, requestDto.getMemberSchool())
                .set(member.memberGrade, requestDto.getMemberGrade())
                .where(Expressions.stringTemplate("HEX({0})", member.uuid).eq(uuid.replace("-","")))
                .execute();
    }
    /**
     * DB UPDATE : MEMBER
     **/
    public void updateMemberPassword(String uuid, String encodePassword){
        queryFactory.update(member)
                .set(member.password, encodePassword)
                .where(Expressions.stringTemplate("HEX({0})", member.uuid).eq(uuid.replace("-","")))
                .execute();
    }
    /**
     * DB UPDATE : MEMBER
     **/
    public void updateMemberEmail(String uuid, String email){
        queryFactory.update(member)
                .set(member.email, email)
                .where(Expressions.stringTemplate("HEX({0})", member.uuid).eq(uuid.replace("-","")))
                .execute();
    }

    /**
     * DB UPDATE : MEMBER
     **/
    public void updateMemberAppToken(String uuid, String appToken){
        queryFactory.update(member)
                .set(member.appToken, appToken)
                .where(Expressions.stringTemplate("HEX({0})", member.uuid).eq(uuid.replace("-","")))
                .execute();
    }

    /**
     * DB DELETE : MEMBER
     **/

    public void deleteMember(Member member){
        memberRepository.delete(member);
    }

    private BooleanExpression propertyEqByKeyValue(String key, String value) {
        if ("id".equals(key))
            return member.id.eq(value);
        if ("email".equals(key))
            return member.email.eq(value);
        return null;
    }



}

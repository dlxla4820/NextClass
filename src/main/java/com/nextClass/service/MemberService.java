package com.nextClass.service;

import com.nextClass.auth.TokenProvider;
import com.nextClass.dto.LoginRequestDto;
import com.nextClass.dto.MemberRequestDto;
import com.nextClass.dto.ResponseDto;
import com.nextClass.entity.Member;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.enums.GradeType;
import com.nextClass.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.nextClass.utils.CommonUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.nextClass.enums.ErrorCode.MEMBER_NOT_EXIST;
import static com.nextClass.enums.ErrorCode.TOKEN_UNAUTHORIZED;

@Service
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final LoginRepository loginRepository;
    private final String[] duplicatedKey= {"id","email"};

    @Autowired
    public MemberService(PasswordEncoder passwordEncoder, TokenProvider tokenProvider, LoginRepository loginRepository) {
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.loginRepository = loginRepository;
    }

    public ResponseDto<?> saveMember(MemberRequestDto requestBody){
        //유효성 체크
        String errorDescription = checkMemberData(requestBody);
        if(errorDescription != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);

        // 중복체크
        if(loginRepository.getMemberByKeyValue("id",requestBody.getId()) != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.MEMBER_DUPLICATED.getErrorCode(), String.format(ErrorCode.MEMBER_DUPLICATED.getErrorDescription(),"id"));
        if(loginRepository.getMemberByKeyValue("email",requestBody.getEmail()) != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.MEMBER_DUPLICATED.getErrorCode(), String.format(ErrorCode.MEMBER_DUPLICATED.getErrorDescription(),"email"));

        // 비밀번호 hashing + 저장
        loginRepository.saveMember(requestBody, passwordEncoder.encode(requestBody.getPassword()));

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }
    public ResponseDto<?> loginMember(LoginRequestDto requestBody) {
        Member member = loginRepository.getMemberById(requestBody.getId());
        if(member == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(),Description.FAIL, MEMBER_NOT_EXIST.getErrorCode(), MEMBER_NOT_EXIST.getErrorDescription());
        if(!passwordEncoder.matches(requestBody.getPassword(), member.getPassword()))
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(),Description.FAIL, MEMBER_NOT_EXIST.getErrorCode(), MEMBER_NOT_EXIST.getErrorDescription());
        String tokenSubject = String.format("%s:%s", member.getUuid(), member.getRoleType());
        Map<String, String> token = new HashMap<>();
        token.put("accessToken",tokenProvider.createAccessToken(tokenSubject));
        token.put("refreshToken", tokenProvider.createRefreshToken(tokenSubject));
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, token);
    }



    public ResponseDto<?> checkDuplicatedMemberData(Map<String, String> data){

        for (String key : duplicatedKey){
            if(data.containsKey(key)){
                if(loginRepository.getMemberByKeyValue(key, data.get(key)) == null)
                    return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
                else
                    return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.MEMBER_DUPLICATED.getErrorCode(), String.format(ErrorCode.MEMBER_DUPLICATED.getErrorDescription(),key));
            }
        }
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_GENERAL.getErrorCode(), ErrorCode.PARAMETER_INVALID_GENERAL.getErrorDescription());
    }


    private String checkMemberData(MemberRequestDto memberRequestDto){
        // id check
        if(memberRequestDto.getId() == null || memberRequestDto.getId().isBlank())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"id");
        if(!Pattern.compile("^[a-zA-Z0-9]{5,20}$").matcher(memberRequestDto.getId()).find())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"id");
        // name check
        if(memberRequestDto.getName() == null || memberRequestDto.getName().isBlank())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"name");
        if(!Pattern.compile("^[가-힣]{2,11}$").matcher(memberRequestDto.getName()).find())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"name");
        // password check
        if(memberRequestDto.getPassword()== null || memberRequestDto.getPassword().isBlank())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"password");
        if(!Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[~'!@#$%^()\\-={\\[}\\];:<>,.?/])[a-zA-Z0-9~'!@#$%^()\\-={\\[}\\];:<>,.?/]{9,18}$").matcher(memberRequestDto.getPassword()).find())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"password");
        // email check
        if(memberRequestDto.getEmail() == null || memberRequestDto.getEmail().isBlank())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"email");
        if(!Pattern.compile("[0-9a-zA-Z]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w{1,320}$").matcher(memberRequestDto.getEmail()).find())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"email");
        // member_grade check
        if(memberRequestDto.getMember_grade() == null)
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"member_grade");
        try {
            GradeType.getInstance(memberRequestDto.getMember_grade());
        } catch (IllegalArgumentException e){
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"member_grade");
        }
        //member_school check
        if(memberRequestDto.getMember_school() == null || memberRequestDto.getMember_school().isBlank())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"member_school");
        if(!Pattern.compile("^[가-힣]{2,11}$").matcher(memberRequestDto.getMember_school()).find())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),"member_school");
        return null;
    }



    public ResponseDto<?> changeInfo(MemberRequestDto requestBody) {
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();

        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(),Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        Member member = loginRepository.getMemberByUuid(memberUuid);

        if(member == null) //회원 삭제
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.SYSTEM_ERROR.getErrorCode(), ErrorCode.SYSTEM_ERROR.getErrorDescription());

        //유효성 체크
        MemberRequestDto memberRequestDto = MemberRequestDto.builder()
                .id(member.getId())
                .name(requestBody.getName())
                .email(requestBody.getEmail())
                .password(requestBody.getPassword())
                .member_grade(requestBody.getMember_grade())
                .member_school(requestBody.getMember_school())
                .build();

        String errorDescription = checkMemberData(memberRequestDto);

        if(errorDescription != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);

        // 중복체크
        if(loginRepository.getMemberByKeyValue("email",memberRequestDto.getEmail()) != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.MEMBER_DUPLICATED.getErrorCode(), String.format(ErrorCode.MEMBER_DUPLICATED.getErrorDescription(),"email"));

        loginRepository.updateMember(memberRequestDto);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }



}

package com.nextClass.service;

import com.nextClass.dto.MemberRequestDto;
import com.nextClass.dto.ResponseDto;
import com.nextClass.entity.Member;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.enums.GradeType;
import com.nextClass.repository.LoginRepository;
import com.nextClass.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;

import static com.nextClass.enums.ErrorCode.MEMBER_NOT_EXIST;

@Service
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final LoginRepository loginRepository;
    private final String[] duplicatedKey= {"id","email"};

    @Autowired
    public MemberService(PasswordEncoder passwordEncoder, LoginRepository loginRepository) {
        this.passwordEncoder = passwordEncoder;
        this.loginRepository = loginRepository;
    }

    public ResponseDto<?> saveMember(MemberRequestDto requestBody){
        //유효성 체크
        String errorDescription = checkMemberData(requestBody);
        System.out.println("requestBody.getId() = " + requestBody.getId());
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
    public ResponseDto<?> loginMember(MemberRequestDto requestBody) {
        Member member = loginRepository.getMemberById(requestBody.getId());
        if(member == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(),Description.FAIL, MEMBER_NOT_EXIST.getErrorCode(), MEMBER_NOT_EXIST.getErrorDescription());
        if(!passwordEncoder.matches(requestBody.getPassword(), member.getPassword()))
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(),Description.FAIL, MEMBER_NOT_EXIST.getErrorCode(), MEMBER_NOT_EXIST.getErrorDescription());

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }



    public ResponseDto<?> checkDuplicatedMemberData(Map<String, String> data){

        for (String key : duplicatedKey){
            if(data.containsKey(key)){
                if(loginRepository.getMemberByKeyValue(key, data.get(key)) != null)
                    return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
                else
                    return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.MEMBER_DUPLICATED.getErrorCode(), String.format(ErrorCode.MEMBER_DUPLICATED.getErrorDescription(),key));
            }
        }
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_GENERAL.getErrorCode(), ErrorCode.PARAMETER_INVALID_GENERAL.getErrorDescription());
    }


    private String checkMemberData(MemberRequestDto memberRequestDto){
        // id check
        HashSet<ErrorCode> errorCodeHashSet;
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

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        return loginRepository.getMemberById(userId);
    }


}

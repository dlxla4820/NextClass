package com.nextClass.service;

import com.nextClass.auth.TokenProvider;
import com.nextClass.dto.*;
import com.nextClass.entity.MailValidation;
import com.nextClass.entity.Member;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.enums.GradeType;
import com.nextClass.repository.LoginRepository;
import com.nextClass.repository.MailRepository;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final LoginRepository loginRepository;
    private final MailRepository mailRepository;
    private final String[] duplicatedKey= {"id","email"};

    private HashMap<String, Boolean> isMemberEmailChangeCodeCheck; // true : 코드 입력 성공, false : 코드 입력 실패

    @Autowired
    public MemberService(PasswordEncoder passwordEncoder, TokenProvider tokenProvider, LoginRepository loginRepository, MailRepository mailRepository) {
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.loginRepository = loginRepository;
        this.mailRepository = mailRepository;
    }

    public ResponseDto<?> saveMember(MemberRequestDto requestBody){
        //유효성 체크
        String errorDescription = checkMemberData(requestBody);
        if(errorDescription != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);
        //메일 인증 TODO: 에러코드 추가
        MailValidation mailValidation = mailRepository.getMailValidationByEmail(requestBody.getEmail());
        if(mailValidation == null || !mailValidation.getChecked())
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL);
        // 중복체크
        if(loginRepository.getMemberByKeyValue("id",requestBody.getId()) != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.INPUT_DUPLICATED.getErrorCode(), String.format(ErrorCode.INPUT_DUPLICATED.getErrorDescription(),"id"));
        if(loginRepository.getMemberByKeyValue("email",requestBody.getEmail()) != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.INPUT_DUPLICATED.getErrorCode(), String.format(ErrorCode.INPUT_DUPLICATED.getErrorDescription(),"email"));



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
                    return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.INPUT_DUPLICATED.getErrorCode(), String.format(ErrorCode.INPUT_DUPLICATED.getErrorDescription(),key));
            }
        }
        return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(), Description.FAIL, ErrorCode.PARAMETER_INVALID_GENERAL.getErrorCode(), ErrorCode.PARAMETER_INVALID_GENERAL.getErrorDescription());
    }






    public ResponseDto<?> changeNormalInfo(MemberChangeNormalInfoRequestDto requestBody) {
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(),Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        //유효성 검사
        String errorDescription = checkMemberNormalInfoData(requestBody);
        if(errorDescription != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);

        loginRepository.updateMemberNormalInfo(memberUuid, requestBody);

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> changePassword(MemberChangePasswordRequestDto requestBody){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(),Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        //유효성 검사
        String errorDescription = checkMemberPassword(requestBody.getExistingPassword(), "existingPassword");
        if(errorDescription != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);
        errorDescription = checkMemberPassword(requestBody.getNewPassword(), "newPassword");
        if(errorDescription != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);
        // 현재 비밀번호 검사
        String memberPassword = loginRepository.getMemberPasswordByUuid(memberUuid);
        if(memberPassword == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.TOKEN_MEMBER_NOT_EXIST.getErrorCode(), ErrorCode.TOKEN_MEMBER_NOT_EXIST.getErrorDescription());
        if(memberPassword.equals(passwordEncoder.encode(requestBody.getExistingPassword())))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.EXISTING_PASSWORD_NOT_MATCH.getErrorCode(), ErrorCode.EXISTING_PASSWORD_NOT_MATCH.getErrorDescription());

        // 비밀번호 저장
        loginRepository.updateMemberPassword(memberUuid,passwordEncoder.encode(requestBody.getNewPassword()));

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }

    public ResponseDto<?> changeEmail(MemberChangeEmailRequestDto requestBody){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(),Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());
        //유효성 검사
        String errorDescription = checkMemberPassword(requestBody.getPassword(), "password");
        if(errorDescription != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);
        errorDescription = checkMemberEmail(requestBody.getEmail());
        if(errorDescription != null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), errorDescription);
        // 현재 비밀번호 검사
        String memberPassword = loginRepository.getMemberPasswordByUuid(memberUuid);
        if(memberPassword == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.TOKEN_MEMBER_NOT_EXIST.getErrorCode(), ErrorCode.TOKEN_MEMBER_NOT_EXIST.getErrorDescription());
        if(memberPassword.equals(passwordEncoder.encode(requestBody.getPassword())))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.EXISTING_PASSWORD_NOT_MATCH.getErrorCode(), ErrorCode.EXISTING_PASSWORD_NOT_MATCH.getErrorDescription());
        // 이메일 체크 검사
        if(!isMemberEmailChangeCodeCheck.containsKey(memberUuid) || !isMemberEmailChangeCodeCheck.get(memberUuid))
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL,ErrorCode.EMAIL_NOT_CHECK.getErrorCode(), ErrorCode.EMAIL_NOT_CHECK.getErrorDescription());

        loginRepository.updateMemberEmail(memberUuid, requestBody.getEmail());

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);

    }

    public ResponseDto<?> getMyInfo(){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(),Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());

        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, loginRepository.getMyInfoByUuid(memberUuid));

    }

    private String checkMemberData(MemberRequestDto requestDto){
        String errorDescription;
        // id check
        errorDescription = checkMemberId(requestDto.getId());
        if(errorDescription != null) return errorDescription;
        // name check
        errorDescription = checkMemberName(requestDto.getName());
        if(errorDescription != null) return errorDescription;
        // password check
        errorDescription = checkMemberPassword(requestDto.getPassword(), "password");
        if(errorDescription != null) return errorDescription;
        // email check
        errorDescription = checkMemberEmail(requestDto.getEmail());
        if(errorDescription != null) return errorDescription;
        // member_grade check
        errorDescription = checkMemberGrade(requestDto.getMember_grade());
        if(errorDescription != null) return errorDescription;
        //member_school check
        errorDescription = checkMemberSchool(requestDto.getMember_school());
        if(errorDescription != null) return errorDescription;

        return null;
    }

    private String checkMemberNormalInfoData(MemberChangeNormalInfoRequestDto requestDto){
        String errorDescription;
        // name check
        errorDescription = checkMemberName(requestDto.getName());
        if(errorDescription != null) return errorDescription;
        // email check
        errorDescription = checkMemberGrade(requestDto.getMember_grade());
        if(errorDescription != null) return errorDescription;
        //member_school check
        errorDescription = checkMemberSchool(requestDto.getMember_school());
        if(errorDescription != null) return errorDescription;

        return null;
    }

    private String checkMemberId(String id){
        // id check
        if(id == null || id.isBlank())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "id");
        if(!Pattern.compile("^[a-zA-Z0-9]{5,20}$").matcher(id).find())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "id");
        return null;
    }
    private String checkMemberName(String name){
        if(name == null || name.isBlank())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "name");
        if(!Pattern.compile("^[가-힣]{2,11}$").matcher(name).find())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "name");
        return null;
    }

    private String checkMemberPassword(String password, String paramName){
        if(password== null || password.isBlank())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),paramName);
        if(!Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[~'!@#$%^()\\-={\\[}\\];:<>,.?/])[a-zA-Z0-9~'!@#$%^()\\-={\\[}\\];:<>,.?/]{9,18}$").matcher(password).find())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(),paramName);
        return null;
    }

    private String checkMemberEmail(String email){
        if(email == null || email.isBlank())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "email");
        if(!Pattern.compile("[0-9a-zA-Z]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w{1,320}$").matcher(email).find())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "email");
        return null;
    }
    private String checkMemberGrade(Integer memberGrade){
        if(memberGrade == null)
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "member_grade");
        try {
            GradeType.getInstance(memberGrade);
        } catch (IllegalArgumentException e){
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "member_grade");
        }
        return null;
    }

    private String checkMemberSchool(String memberSchool){
        if(memberSchool == null || memberSchool.isBlank())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "member_school");
        if(!Pattern.compile("^[가-힣]{2,11}$").matcher(memberSchool).find())
            return String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "member_school");
        return null;
    }
}

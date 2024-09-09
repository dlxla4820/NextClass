package com.nextClass.service;

import com.nextClass.dto.NotificationConfigRequestDto;
import com.nextClass.dto.NotificationConfigResponseDto;
import com.nextClass.dto.ResponseDto;
import com.nextClass.enums.Description;
import com.nextClass.enums.ErrorCode;
import com.nextClass.repository.NotificationDetailRepository;
import com.nextClass.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.nextClass.enums.ErrorCode.TOKEN_UNAUTHORIZED;

@Service
@Slf4j
@Transactional
public class NotificationService {

    private final NotificationDetailRepository notificationDetailRepository;

    public NotificationService(
            NotificationDetailRepository notificationDetailRepository
    ){
        this.notificationDetailRepository = notificationDetailRepository;
    }

    public ResponseDto<?> getNotificationConfig(){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("NotificationService << getNotificationConfig >> | memberUuid : {}",memberUuid);
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());

        List<NotificationConfigResponseDto> responseList = notificationDetailRepository.getNotificationConfigByMemberUuid(memberUuid);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS, responseList);
    }


    public ResponseDto<?> updateNotificationConfig(NotificationConfigRequestDto requestBody){
        String memberUuid = CommonUtils.getMemberUuidIfAdminOrUser();
        log.info("NotificationService << updateNotificationConfig >> | memberUuid : {}, requestBody : {}",memberUuid, requestBody);
        if(memberUuid == null)
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), Description.FAIL, TOKEN_UNAUTHORIZED.getErrorCode(), TOKEN_UNAUTHORIZED.getErrorDescription());

        //유효성 검사
        if(requestBody.getCategory() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "category"));
        if(requestBody.getIsNotificationActivated() == null)
            return new ResponseDto<>(HttpStatus.BAD_REQUEST.value(),Description.FAIL, ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorCode(), String.format(ErrorCode.PARAMETER_INVALID_SPECIFIC.getErrorDescription(), "is_notification_activated"));

        notificationDetailRepository.updateNotificationConfig(requestBody,memberUuid);
        return new ResponseDto<>(HttpStatus.OK.value(), Description.SUCCESS);
    }
}

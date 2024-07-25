package com.nextClass.service;

import com.nextClass.dto.ResponseDto;
import com.nextClass.dto.ToDoListRequsetDto;
import org.springframework.stereotype.Service;

@Service
public class ToDoListService {


    public ResponseDto<?> createToDoList(ToDoListRequsetDto toDoListDto){
        //request 전부 왔는지 검증(content, createdTime, alarmTIme, doneTime)
        //현재 로그인 한 사람 데이터 가져오기
        //동일한 데이터가 있는지 확인(내용, 작성자, 알람 설정 시간, 목표 시간)
        //있으면 이미 해당 목표가 설정되어 있습니다라는 에러 발생 (E-004)
        //없으면 해당 to do list 생성
        //firebase에 연결해서 알람도 설정 (alarmTime 시간)
        // 성공 return
    }

    public ResponseDto<?> updateToDoList(ToDoListRequsetDto toDoListDto){
        //request검증(uuid, updateTime, content, alarmTime,doneTime)
        //현재 로그인 한 사람 가져오기
        //해당 uuid를 가진 todoList의 생성자가 해당 멤버인지 검증
        //아닐 경우 UnAuthorized 에러
        //맞을 경우 해당 데이터 업데이트 후 firebase의 알람도 update
        //성공 return
    }

    public ResponseDto<?> deleteToDoList(ToDoListRequsetDto toDoListRequsetDto){
        //해당 uuid가 존재하는지 확인
        //존재하지 않으면 존재하지 않는다 에러
        //현재 접속 유저와 해당 to_do_list의 생성자가 일치하는지 확인
        //일치하지 않으면 권한 없음
        //일치 하면 firebase 알람 삭제 후 해당 ToDoList 삭제 후 return
    }

    public ResponseDto<?> readAllToDoList(){
        //로그인 한 유저 확인
        //해당 유저가 생성한 ToDoList전부 읽어온 뒤에 return
    }

    
}

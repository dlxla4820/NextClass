package com.nextClass.dto;

import com.nextClass.entity.ClassDetail;
import com.nextClass.entity.Member;
import com.nextClass.entity.TimeTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimeTableDto {
    private String memberUUID;
    private String classDetailUUID;
    private TimeTableRequestDto timeTableRequestDto;
    private String semester;

    public TimeTableDto(String memberUUID, TimeTableRequestDto timeTableRequestDto){
        this.memberUUID = memberUUID;
        this.timeTableRequestDto = timeTableRequestDto;
    }

    public TimeTableDto(String memberUUID, String semester){
        this.memberUUID = memberUUID;
        this.semester = semester;
    }

    public TimeTableDto(String memberUUID, String classDetailUUID, TimeTableRequestDto timeTableRequestDto){
        this.memberUUID = memberUUID;
        this.classDetailUUID = classDetailUUID;
        this.timeTableRequestDto = timeTableRequestDto;
    }

    public void addClassDetailUuid(String classDetailUUID){
        this.classDetailUUID = classDetailUUID;
    }
}

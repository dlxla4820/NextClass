package com.nextClass.dto;

import com.nextClass.entity.ClassDetail;
import com.nextClass.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeTableDto {
    private Member member;
    private ClassDetail classDetail;
    private TimeTableRequestDto timeTableRequestDto;
}

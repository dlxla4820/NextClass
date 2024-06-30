package com.nextClass.dto;

import com.nextClass.entity.ClassDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeTableDto {
    private ClassDetail classDetail;
    private String week;
    private int classStartTime;
    private int classEndTime;
    private String semester;
}

package com.nextClass.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GradeType {
    FRESHMAN(1), SOPHOMORE(2), SENIOR(3), ETC(0);

    private final int grade;

    public static GradeType getInstance(int grade) {
        for (GradeType gradeType : GradeType.values()) {
            if (gradeType.grade == grade) {
                return gradeType;
            }
        }
        throw new IllegalArgumentException("Invalid num: " + grade);
    }
}

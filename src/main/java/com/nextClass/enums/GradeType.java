package com.nextClass.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GradeType {
    freshman(1), sophomore(2), senior(3), etc(0);

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

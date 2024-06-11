package com.nextClass.utils;

import com.nextClass.enums.GradeType;
import jakarta.persistence.AttributeConverter;

public class GradeTypeConverter implements AttributeConverter<GradeType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(GradeType attr) {
        if(attr == null)
            return 0;
        return attr.getGrade();
    }

    @Override
    public GradeType convertToEntityAttribute(Integer num) {
        return GradeType.getInstance(num);
    }

}

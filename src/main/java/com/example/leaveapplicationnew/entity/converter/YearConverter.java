package com.example.leaveapplicationnew.entity.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Year;

@Converter
public class YearConverter implements AttributeConverter<Year, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Year year) {
        if(year == null) return null;
        return year.getValue();
    }

    @Override
    public Year convertToEntityAttribute(Integer integer) {
        return Year.of(integer);
    }
}

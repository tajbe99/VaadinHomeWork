package com.classes;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;

import java.time.Duration;
import java.time.LocalDate;

public class DateConverter implements Converter<LocalDate, Long> {
    @Override
    public Result<Long> convertToModel(LocalDate value, ValueContext context) {
        if (value != null) {
            long days = Duration.between(value.atTime(0, 0), LocalDate.now().atTime(0, 0)).toDays();
            return Result.ok(days);
        }
        return Result.error("Please, choose date!");
    }

    @Override
    public LocalDate convertToPresentation(Long value, ValueContext context) {
        if (value == null) {
            return LocalDate.now();
        }
        return LocalDate.now().minusDays(value);
    }
}

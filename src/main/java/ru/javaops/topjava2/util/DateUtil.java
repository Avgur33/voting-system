package ru.javaops.topjava2.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class DateUtil {

    public static final LocalDate DATE_MIN = LocalDate.of(2000,1,1);
    public static final LocalDate DATE_MAX = LocalDate.of(3000,1,1);

    public static LocalDate startDateUtil(LocalDate startDate){
        if ((startDate == null) || (startDate.isBefore(DATE_MIN))){
            return DATE_MIN;
        }else {
            return startDate;
        }
    }
    public static LocalDate endDateUtil(LocalDate endDate){
        if ((endDate == null) || (endDate.isAfter(DATE_MAX))){
            return DATE_MAX;
        }else {
            return endDate;
        }
    }
}

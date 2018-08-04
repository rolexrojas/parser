package com.wallethub.parser.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class Utility {

    private static final Logger log = LoggerFactory.getLogger(Utility.class);

    //Validate startDate param is in the correct input
    public static boolean isValidDateFormat(String dateFormatPattern, String value, Locale locale) {
        LocalDateTime ldt = null;
        DateTimeFormatter fomatter = DateTimeFormatter.ofPattern(dateFormatPattern, locale);

        try {
            ldt = LocalDateTime.parse(value, fomatter);
            String result = ldt.format(fomatter);
            return result.equals(value);
        } catch (DateTimeParseException e) {
            try {
                LocalDate ld = LocalDate.parse(value, fomatter);
                String result = ld.format(fomatter);
                return result.equals(value);
            } catch (DateTimeParseException exp) {
                try {
                    LocalTime lt = LocalTime.parse(value, fomatter);
                    String result = lt.format(fomatter);
                    return result.equals(value);
                } catch (DateTimeParseException e2) {
                    // Debugging purposes
                    //e2.printStackTrace();
                }
            }
        }

        return false;
    }

    //Transform date in order to add the new hours to create endDate param, then added it through validator to jobParameterBuilder
    public static String nextTimeBasedOnDurationParam(String dateFormatPattern, String value, Locale locale, String param){
        DateTimeFormatter fomatter = DateTimeFormatter.ofPattern(dateFormatPattern, locale);
        LocalDateTime localdt = LocalDateTime.parse(value, fomatter);
        LocalDateTime nextTime = null;
        //at this point the param containing the value has been validating, even so, method could use further decoupling
        if(param.equalsIgnoreCase("hourly")){
             nextTime = localdt.plusHours(1);

           // System.out.println("After 1 Hours = " + nextTime);

        }else if(param.equalsIgnoreCase("daily")){
             nextTime = localdt.plusHours(24);
           // System.out.println("After 24 Hours = " + nextTime);
        }

        return nextTime.format(fomatter);

    }
}

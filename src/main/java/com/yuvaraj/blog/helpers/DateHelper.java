package com.yuvaraj.blog.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {


    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String TIME_ZONE = "Asia/Kuala_Lumpur";

    public static String convertDateForEndResult(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));
        return simpleDateFormat.format(date);
    }
}

package com.parser;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CalendarUtils {
    public static String getCurrentDate(int increment) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cl = Calendar.getInstance();
        cl.add(Calendar.DATE, increment);
        return dateFormat.format(cl.getTime());
    }
}

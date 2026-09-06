package com.mar.ds.utils;

import lombok.experimental.UtilityClass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@UtilityClass
public class Utils {

    public static String formatUsingSimpleDateTimeFormat(Date utilDate) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(utilDate);
    }

    public static long getDateWithoutTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return new Date(
                c.get(Calendar.YEAR) - 1900,
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).getTime();
    }

}

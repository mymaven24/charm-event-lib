package com.swwx.charm.event.lib.util;

import com.swwx.charm.commons.lang.utils.LogPortal;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {


    public static Integer dateToInteger(Date generateTime) {
        if (null == generateTime) {
            return null;
        }
        try {
            return Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(generateTime));
        } catch (Exception e) {
            LogPortal.error("dateToIntegerError, {}", e, generateTime);
            return null;
        }
    }

    public static Date getNowDate(){
        return new Date();
    }

    public static Date getDayBeforeDay(int i) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -i);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
}

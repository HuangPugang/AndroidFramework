package org.hpdroid.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by paul on 16/1/13.
 */
public class DateHelper {
    public static final String FORMAT_Y      = "yyyy";
    public static final String FORMAT_YMD    = "yyyy-MM-dd";
    public static final String FORMAT_YMDHM  = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyy_MM_dd_T_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyy_MM_dd = "yyyy-MM-dd";
    public static final String yyyydotMMdotdd = "yyyy.MM.dd";
    public static final String MMstr_ddstr = "M月d日";
    public static final String yyyy_MM = "yyyy-MM";

    private DateHelper() {
    }

    public static String convert(long times, String pattern) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.format(new Date(times));
        } catch (Exception e) {
            return "";
        }
    }

    public static String convertToNormal(String serverTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmm");
        Date date = null;
        try {
            date = simpleDateFormat.parse(serverTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HH:mm");
        String string = simpleDateFormat2.format(date);
        return string;
    }

    public static String convertToServer(String serverTime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        Date date = null;
        try {
            date = simpleDateFormat.parse(serverTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("HHmm");
        String string = simpleDateFormat2.format(date);
        return string;
    }

    public static String getFormatDateSecond(long longDate, String formatStr) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(longDate * 1000);
        return new SimpleDateFormat(formatStr, Locale.getDefault()).format(gc.getTime());
    }

    public static int calcEnterSchoolYears(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_Y);
        String strCurYears = dateFormat.format(date);
        Integer curYears = Integer.valueOf(strCurYears);
        return curYears;
    }

    public static String getFormatDate(long dateLong, String formatString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(formatString);
            Date date = new Date(dateLong);
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 返回 xx月xx日
     * @param dateLong
     * @return
     */
    public static String getFormatDateByMStrDStr(long dateLong) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(MMstr_ddstr);
            Date date = new Date(dateLong);
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}

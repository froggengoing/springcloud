package com.awesomeJdk.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author froggengo@qq.com
 * @date 2021/4/13 20:42.
 * 主要时java.util.Date的封装。
 * Date对象包含的唯一东西是从1970年1月1日00:00:00 UTC开始的“纪元”以来的毫秒数。
 * DateFormat对象上设置时区，以显示相应时区日期和时间
 */
public class FgDateUtils {

    public static final long MILLIS_PER_SECOND = 1000;
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;


    public static String YYYY = "yyyy";
    public static String YYYY_MM = "yyyy-MM";
    public static String YYYY_MM_DD = "yyyy-MM-dd";
    public static String HH_MM_SS = "HH:mm:ss";
    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static String YYYY_MM_DD_HH_MM_SS_FFF = "yyyy-MM-dd HH:mm:ss.SSS";
    public static String SLASH_YYYY_MM_DD_HH_MM_SS = "yyyy/MM/dd HH:mm:ss";
    public static String SLASH_YYYY_MM_DD_HH_MM_SS_FFF = "yyyy/MM/dd HH:mm:ss.SSS";

    /**
     * 返回当前系统时间（秒）
     */
    public static int getCurSec() {
        return Math.toIntExact(getCurMills());
    }

    /**
     * 返回当前系统时间（毫秒）
     */
    public static long getCurMills() {
        return System.currentTimeMillis();
    }

    /**
     * 毫秒数转字符串,格式：yyyy-MM-dd HH:mm:ss
     */
    public static String millsToDateTime(long mills) {
        return dateTime(new Date(mills));
    }

    /**
     * 秒数转字符串，格式：yyyy-MM-dd HH:mm:ss
     */
    public static String secToDateTime(int sec) {
        return dateTime(new Date(sec * MILLIS_PER_SECOND));
    }

    /**
     * 将日期转yyyy-MM-dd格式
     */
    public static String date(final Date date) {
        return formatDateTime(date, YYYY_MM_DD);
    }

    /**
     * 将日期转yyyy-MM-dd HH:mm:ss格式
     */
    public static String dateTime(final Date date) {
        return formatDateTime(date, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss格式的字符串转日期
     */
    public static Date parseDateTime(final String str) {
        return parseToDate(str, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 根据字符串格式格式化日期
     */
    public static String formatDateTime(final Date date, final String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 将字符串按给定格式解析为日期
     */
    public static Date parseToDate(final String str, final String format) {
        if (str == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(format).parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 返回相对当前月份的偏移月数，所在月份第一天0点整日期，offset表示偏移量，如上个月为-1
     */
    public static Date truncateMonByOffset(int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, offset);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return truncateDate(calendar);
    }

    /**
     * 指定时间搓（毫秒）当月第一天的0点整
     */
    public static Date truncateMon(long millsTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millsTime);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return truncateDate(calendar);
    }

    /**
     * 指定时间搓（毫秒）当天的0点整
     */
    public static Date truncateDate(long millsTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millsTime);
        return truncateDate(calendar);
    }

    /**
     * 返回相对今天的偏移天数，当天的0点日期
     */
    public static Date truncateDateByOffset(int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.DAY_OF_MONTH, offset);
        return truncateDate(calendar);
    }

    /**
     * 更好的是apache DateUtils 的实现
     */
    public static Date truncateDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return truncateDate(calendar);
    }

    /**
     * 更好的是apache DateUtils 的实现
     */
    public static Date truncateDate(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 两个时间搓的时间差
     */
    public static String duration(long max, long min) {
        long duration = max - min;
        long dayPart = duration / MILLIS_PER_DAY;
        long hourPart = duration % MILLIS_PER_DAY / MILLIS_PER_HOUR;
        long miniPart = duration % MILLIS_PER_DAY % MILLIS_PER_HOUR / MILLIS_PER_MINUTE;
        long secPart = duration % MILLIS_PER_DAY % MILLIS_PER_HOUR % MILLIS_PER_MINUTE / MILLIS_PER_SECOND;
        return dayPart + "天" + hourPart + "小时" + miniPart + "分" + secPart + "秒";
    }

}

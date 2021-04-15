package com.awesomeJdk.date;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

/**
 * @author froggengo@qq.com
 * @date 2021/4/14 9:30.
 */
public class FgTimeUtils {

    //时间搓转日期,mills为utc时间搓
    public static LocalDateTime millsToDateTime(long mills) {
        Instant instant = Instant.ofEpochMilli(mills);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    //时间搓转日期
    public static LocalDateTime secToDateTime(int sec) {
        Instant instant = Instant.ofEpochSecond(sec);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * 字符串转时间搓,时间格式
     * yyyy-MM-dd HH:mm:ss
     * yyyy-MM-dd
     * yyyy-MM
     */
    public static LocalDateTime strToDateTime(String str) {
        Objects.requireNonNull(str);
        String dateStr;
        switch (str.length()) {
            case 19:
                dateStr = str;
                break;
            case 10:
                dateStr = str + " 00:00:00";
                break;
            case 7:
                dateStr = str + "-01 00:00:00";
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(FgDateUtils.YYYY_MM_DD_HH_MM_SS));
    }

    /**
     * localDatetime转时间搓(utc),时间搓均指utc+0时间
     */
    public static long dateTimeToMills(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 字符串转时间搓,时间格式
     * yyyy-MM-dd HH:mm:ss
     * yyyy-MM-dd
     * yyyy-MM
     */
    public static long strToMills(String str) {
        Objects.requireNonNull(str);
        return dateTimeToMills(strToDateTime(str));
    }

    /**
     * 计算偏移量后当天时间0点ZoneDateTime
     * LocalDate的另一个方法会转成
     * LocalDate.now().atStartOfDay(ZoneId.systemDefault()
     */
    public static LocalDateTime startOfDayByOffset(int offset) {
        LocalDate now = LocalDate.now().plusDays(offset);
        return LocalDateTime.of(now, LocalTime.MIN);
    }

    /**
     * 计算偏移后当月第一天时间0点
     */
    public static LocalDateTime startOfMonthByOffset(int offset) {
        LocalDate now = LocalDate.now().plusMonths(offset);
        return LocalDateTime.of(now.with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN);
    }

    /**
     * 将time包下的时间对象转成相应的字符串
     */
    public static <T extends Temporal> String formatTime(T time) {
        if (time instanceof LocalDateTime) {
            return ((LocalDateTime) time).format(DateTimeFormatter.ofPattern(FgDateUtils.YYYY_MM_DD_HH_MM_SS));
        } else if (time instanceof LocalDate) {
            return ((LocalDate) time).format(DateTimeFormatter.ofPattern(FgDateUtils.YYYY_MM_DD));
        } else if ((time instanceof LocalTime)) {
            return ((LocalTime) time).format(DateTimeFormatter.ofPattern(FgDateUtils.HH_MM_SS));
        } else if ((time instanceof ZonedDateTime)) {
            return ((ZonedDateTime) time).format(DateTimeFormatter.ofPattern(FgDateUtils.YYYY_MM_DD_HH_MM_SS));
        }
        throw new UnsupportedOperationException("only support LocalDateTime、LocalDate、LocalTime、ZonedDateTime");
    }

    public static String formatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern(FgDateUtils.YYYY_MM_DD));
    }

    //    两个时间搓的时间差
    public static String duration(long max, long min) {
        Duration duration = Duration.ofMillis(max - min);
        return duration.toDaysPart() + "天" + duration.toHoursPart() + "小时" + duration.toMinutesPart() + "分" + duration
            .toSecondsPart() + "秒";
    }
}

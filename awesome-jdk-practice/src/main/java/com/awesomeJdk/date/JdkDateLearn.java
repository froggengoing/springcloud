package com.awesomeJdk.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.MinguoDate;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Stream;
import org.junit.Test;

/**
 * @author froggengo@qq.com
 * @date 2021/4/13 10:22.
 */
public class JdkDateLearn {

    @Test
    public void testDateToString() {
        System.out.println(FgDateUtils.formatDateTime(new Date(), FgDateUtils.YYYY_MM_DD_HH_MM_SS));
        System.out.println(FgDateUtils.formatDateTime(new Date(), FgDateUtils.SLASH_YYYY_MM_DD_HH_MM_SS));
        System.out.println(FgDateUtils.formatDateTime(new Date(), FgDateUtils.SLASH_YYYY_MM_DD_HH_MM_SS_FFF));
    }

    @Test
    public void testStrToDate() {
        System.out.println(FgDateUtils.dateTime(FgDateUtils.parseDateTime("2019-11-12 12:20:31")));
    }

    @Test
    public void testTruncate() {
        Date date = FgDateUtils.truncateDateByOffset(-16);
        System.out.println(date.getTime());
        System.out.println(FgDateUtils.dateTime(date));
    }

    @Test
    public void testDayofMonth() {
        System.out.println(FgDateUtils.truncateMon(System.currentTimeMillis()).getTime());
        System.out.println(FgDateUtils.dateTime(FgDateUtils.truncateMonByOffset(9)));
    }

    @Test
    public void testFormat() {
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        dateFormat.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
        System.out.println(dateFormat.format(System.currentTimeMillis()));
        System.out.println(System.currentTimeMillis());

    }

    @Test
    public void testZone() {
        System.out.println(ZoneId.systemDefault());
        System.out.println(ZoneId.of("Asia/Shanghai"));
        System.out.println(ZoneId.of("Asia/Hong_Kong"));
        System.out.println(ZoneOffset.UTC);
        System.out.println(ZoneId.of("Z"));
        System.out.println(ZoneId.of("+8"));
        System.out.println(ZoneId.of("UTC+8"));
        System.out.println(ZoneId.ofOffset("UTC", ZoneOffset.ofHours(8)));
        //具体见方法注释
        System.out.println(ZoneOffset.of("+08:30:20"));
        System.out.println(LocalDateTime.ofInstant(new Date().toInstant(), ZoneOffset.UTC));
        System.out.println(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.of("+7")));
    }

    @Test
    public void testJdk8Date() {
        System.out.println(LocalDate.now());
        System.out.println(LocalTime.now());
        System.out.println(ZonedDateTime.now());
        System.out.println(LocalTime.now());
        System.out.println(LocalDateTime.now());
        System.out.println(Instant.now());
        System.out.println(MinguoDate.now());
        System.out.println("-----------------------");
        System.out.println(FgDateUtils.dateTime(java.sql.Date.valueOf(LocalDate.now().plus(1, ChronoUnit.DAYS))));
        System.out.println(LocalTime.now());
        System.out.println(
            FgDateUtils.secToDateTime((int) LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()));
        System.out.println(LocalDateTime.now().atZone(ZoneId.systemDefault()).toString());
        //获取毫秒数
    }

    @Test
    public void testLocalDtToIne() {
        long defaultZoneDt = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long utcZoneDt = LocalDateTime.now().atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
        System.out.println(defaultZoneDt);
        System.out.println(utcZoneDt);
        System.out.println(FgDateUtils.millsToDateTime(defaultZoneDt));
        System.out.println(FgDateUtils.millsToDateTime(utcZoneDt));
    }

    @Test
    public void testDateStr() {
        LocalDateTime leaving = LocalDateTime.of(2020, 6, 29, 19, 30);
        //这里也是2020/06/29 19:30:00，但是有了时区的概念，是上海时区的2020/06/29 19:30:00
        //而上面的LocalDateTime可以理解为只是一个时间字符串
        ZonedDateTime shanghaiTime = leaving.atZone(ZoneId.of("Asia/Shanghai"));
        System.out.println(shanghaiTime);
        //这里把上海时间加上半小时后,转化为了东京时间,东京时间与上海时间相差1个小时
        //所以这里显示的字符串是2020-06-29T21:00+09:00[Asia/Tokyo]
        ZonedDateTime tokyoTime = shanghaiTime.plusMinutes(30).withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
        System.out.println(tokyoTime);
    }

    @Test
    public void testStrToLocalDateTime() {
        System.out.println(FgTimeUtils.strToMills("2020-11-12"));
        System.out.println(FgTimeUtils.strToMills("2020-11"));
    }

    @Test
    public void testStart() {
        LocalDateTime dateTime = FgTimeUtils.startOfDayByOffset(-15);
        LocalDateTime dateMon = FgTimeUtils.startOfMonthByOffset(0);
        System.out.println(dateTime);
        System.out.println(dateMon);
        System.out.println(FgTimeUtils.dateTimeToMills(dateMon));
        System.out.println(LocalTime.MIN.toString());
        System.out.println(LocalDate.now().atStartOfDay(ZoneId.systemDefault()));
        Duration between = Duration.between(dateMon, dateTime);
        System.out.println(between.getUnits());
        System.out.println(between.get(ChronoUnit.SECONDS));
        System.out.println(between.get(ChronoUnit.NANOS));
        System.out.println(between.toDaysPart());
    }

    @Test
    public void testNumToLocalTime() {
        System.out.println(FgTimeUtils.millsToDateTime(System.currentTimeMillis()));
        int sec = (int) (System.currentTimeMillis() / 1000);
        System.out.println(sec);
        System.out.println(FgTimeUtils.secToDateTime(sec));
        System.out.println(FgTimeUtils.formatTime(LocalTime.now()));
        System.out.println(FgTimeUtils.formatTime(LocalDateTime.now()));
        System.out.println(FgTimeUtils.formatTime(LocalDate.now()));
        System.out.println(FgTimeUtils.formatTime(LocalDateTime.now().atZone(ZoneId.systemDefault())));
        System.out.println(FgTimeUtils.formatDate(LocalDateTime.now()));
    }

    @Test
    public void testFormatter() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        formatter.setLenient(false);
        Date date = formatter.parse("2020-03-60");  //抛出转换异常
        System.out.println(date);
    }

    @Test
    public void test() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        System.out.println(calendar.get(Calendar.YEAR));
        System.out.println(calendar.get(Calendar.MONTH) + 1);
        System.out.println(calendar.get(Calendar.DATE));
        System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
        System.out.println(calendar.get(Calendar.MINUTE));
        System.out.println(calendar.get(Calendar.SECOND));
        //月最大值，返回31
        System.out.println(calendar.getMaximum(Calendar.DATE));
        //返回当月最大值
        System.out.println(calendar.getActualMaximum(Calendar.DATE));

    }

    @Test
    public void testDuration() {
        Duration d8 = Duration.of(10, ChronoUnit.DAYS);
        System.out.println(d8.getSeconds());
        LocalDateTime sec = LocalDateTime.of(2020, 4, 14, 17, 15, 30);
        LocalDateTime first = LocalDateTime.of(2020, 3, 13, 16, 14, 20);
        System.out.println(FgTimeUtils.duration(FgTimeUtils.dateTimeToMills(sec), FgTimeUtils.dateTimeToMills(first)));
        System.out.println(FgDateUtils.duration(FgTimeUtils.dateTimeToMills(sec), FgTimeUtils.dateTimeToMills(first)));

        Duration d1 = Duration.ofHours(2);
        //返回具有指定秒数的此时间量对象的副本。副本具有指定秒数的时间量，并保留原时间量对象的纳秒部分。
        Duration d2 = d1.withSeconds(2000);
        //返回具有指定纳秒数的此时间量对象的副本。副本具有指定纳秒数的时间量，并保留原时间量对象的秒的部分。
        Duration d3 = d1.withNanos(1000);
        System.out.println(d1);
        System.out.println(d2);
        System.out.println(d3);
    }

    /**
     * 每月有几天
     */
    @Test
    public void testMonth() {
        int year = 2018;
        Stream.of(Month.values())
            // 注意这里：可以使用 YearMonth.of(year, month) 类
            .map(month -> LocalDate.of(year, month.getValue(), 1))
            .forEach(date -> {
                Month month = date.getMonth();
                String displayNameMonth = month.getDisplayName(TextStyle.FULL, Locale.getDefault());
                System.out.println(displayNameMonth+"--" +month.maxLength() +"--"+date.lengthOfMonth());
            });
    }
    //在当年的某个特定月份，列出当月的所有星期一。
    @Test
    public void testFirst (){
        int m = 5;
        // LocalDate date = LocalDate.of(2018, month, 1);
        Month month = Month.of(m);
        // 注意这里的at用法，挺不错
        LocalDate date = Year.now().atMonth(month).atDay(1)
            .with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));  // 找到该月的第一个星期并作为起始日期
        Month mi = date.getMonth();
        System.out.printf("%s的星期一有以下几天:%n", month.getDisplayName(TextStyle.FULL, Locale.getDefault()));
        while (mi == month) {
            System.out.printf("%s%n", date);
            date = date.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
            mi = date.getMonth();
        }
    }
    @Test
    public void testDateHappen (){
        // 假设给定5月13号，判断该日期是否是13号并且还是星期五
        int m = 5;
        int day = 13;

        LocalDate date = Year.now().atMonth(m).atDay(day);
        // 使用查询方式来处理是最方便的
        Boolean query = date.query(temporal -> {
            int dom = temporal.get(ChronoField.DAY_OF_MONTH);
            int dow = temporal.get(ChronoField.DAY_OF_WEEK);
            return dom == 13 && dow == 5;
        });
        System.out.println(query);
    }
    //找一年中是13号又是星期5的日期
    @Test
    public void testDay (){
        int year = 2018;
        Year y = Year.of(year);
        LocalDate date = y.atMonth(1).atDay(1)
            .with(TemporalAdjusters.firstInMonth(DayOfWeek.FRIDAY));
        int targetY = date.getYear();
        while (year == targetY) {
            Boolean query = date.query(temporal -> {
                int dom = temporal.get(ChronoField.DAY_OF_MONTH);
//                int dow = temporal.get(ChronoField.DAY_OF_WEEK);
                return dom == 13 ;//&& dow == 5;
            });
            if (query) {
                System.out.println(date);
            }
            date = date.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
            targetY = date.getYear();
        }
    }
}

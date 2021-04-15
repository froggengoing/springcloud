package com.awesomeJdk.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;

/**
 * @author froggengo@qq.com
 * @date 2021/4/13 9:50.
 */
public class apacheDate {



    public static void main(String[] args) {
        //下月第一天
        Date nextMonthFirstDay = DateUtils.ceiling(new Date(), Calendar.MONTH);
        System.out.println(dateSecStr(nextMonthFirstDay));
        //当月第一天
        Date curMonthFirstDay = DateUtils.truncate(new Date(), Calendar.MONTH);
        System.out.println(dateSecStr(curMonthFirstDay));

        Date curMonthFirstDay2 = DateUtils.setDays(new Date(), 1);
        System.out.println(dateSecStr(DateUtils.truncate(curMonthFirstDay2,Calendar.DATE)));
    }
    public static String dateSecStr(Date date){
        return FgDateUtils.formatDateTime(date,FgDateUtils.YYYY_MM_DD_HH_MM_SS);
    }

}

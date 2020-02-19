package com.ling.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * 获取过去 任意天内的日期数组
     */
    public static ArrayList<String> getPastDaysList(int intervals) {
        ArrayList<String> pastDaysList = new ArrayList<>();
        for (int i = 0; i < intervals; i++) {
            pastDaysList.add(getPastDate(i));
        }
        return pastDaysList;
    }

    /**
     * 获取未来 任意天内的日期数组
     */
    public static ArrayList<String> getFutureDaysList(int intervals) {
        ArrayList<String> fetureDaysList = new ArrayList<>();
        for (int i = 0; i < intervals; i++) {
            fetureDaysList.add(getFutureDate(i));
        }
        return fetureDaysList;
    }

    /**
     * 获取过去第几天的日期
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }

    /**
     * 获取未来 第 past 天的日期
     */
    public static String getFutureDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }

    //返回yyyy-MM 格式的日期字符串
    public static String getYearMonthString() {
        return new SimpleDateFormat("yyyy-MM").format(new Date()).toString();
    }

    //返回yyyy/MM/dd 格式的日期字符串
    public static String getDateString() {
        return new SimpleDateFormat("yyyy/MM/dd").format(new Date()).toString();
    }

    public static void main(String[] args) {
        System.out.println(getFutureDate(-1));
        System.out.println(getPastDate(1));
    }
}

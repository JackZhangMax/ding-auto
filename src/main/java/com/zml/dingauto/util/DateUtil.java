package com.zml.dingauto.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author zhangmingliang
 * @version 1.0
 * @date 2021/8/6 15:21
 */
public class DateUtil {

    public static final DateTimeFormatter HM = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter YMD = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter YMD_HMS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter MD_HM = DateTimeFormatter.ofPattern("MM-dd HH:mm:ss");
    public static final DateTimeFormatter HMS = DateTimeFormatter.ofPattern("HH:mm:ss");


    /**
     * 获取时间
     * @return
     */
    public static String getTime(){
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(HMS);
    }

    /**
     * 获取日期
     * @return
     */
    public static String getDate(){
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(YMD);
    }

    /**
     * 获取时间
     * @param format
     * @return
     */
    public static String getNowTime(String format){
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }
}

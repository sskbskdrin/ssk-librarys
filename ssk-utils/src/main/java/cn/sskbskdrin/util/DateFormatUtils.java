package cn.sskbskdrin.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum DateFormatUtils {
    /**
     * yyyy-MM-dd
     */
    YYYY_MM_DD("yyyy-MM-dd"),
    /**
     * yyyyMMdd
     */
    YYYYMMDD("yyyyMMdd"),
    /**
     * yyyyMM
     */
    YYYYMM("yyyyMM"),
    /**
     * yyyy-MM
     */
    YYYY_MM("yyyy-MM"),
    /**
     * yyyyMMddHHmmss
     */
    YYMDHMS("yyyyMMddHHmmss"),
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    YY_M_D_H_M_S("yyyy-MM-dd HH:mm:ss"),
    /**
     * yyyy-MM-dd HH:mm
     */
    YY_M_D_H_M("yyyy-MM-dd HH:mm"),
    /**
     * yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     */
    TZ("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
    /**
     * MM-dd
     */
    MM_DD("MM-dd"),
    /**
     * MM月dd日
     */
    MM_DD2("MM月dd日"),
    /**
     * MM月dd日HH:mm
     */
    MM_DD2_HH_MM("MM月dd日HH:mm"),
    /**
     * yyyy年MM月dd日
     */
    YYYY_MM_DD2("yyyy年MM月dd日"),
    /**
     * yyyy年MM月dd日HH:mm
     */
    YY_M_D_H_M2("yyyy年MM月dd日HH:mm"),
    /**
     * yyyy年MM月dd日 HH:mm:ss
     */
    YY_M_D_H_M_S2("yyyy年MM月dd日 HH:mm:ss"),
    /**
     * KK:mm
     */
    HH_MM_12("KK:mm"),
    /**
     * HH:mm
     */
    HH_MM_24("HH:mm"),
    /**
     * HH:mm:ss
     */
    HH_MM_SS("HH:mm:ss"),

    CUSTOM("");

    private String format;
    private SimpleDateFormat simpleDateFormat;
    private Date date;

    DateFormatUtils(String format) {
        this.format = format;
    }

    public DateFormatUtils setFormat(String format) {
        if (this != CUSTOM) {
            throw new IllegalAccessError("只有CUSTOM才可以自定格式");
        }
        this.format = format;
        return this;
    }

    public String format(long time) {
        if (date == null) {
            date = new Date(time);
        }
        date.setTime(time);
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(format);
        }
        return simpleDateFormat.format(date);
    }

    public long parse(String date) throws ParseException {
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(format);
        }
        Date date1 = simpleDateFormat.parse(date);
        return date1 == null ? 0 : date1.getTime();
    }
}

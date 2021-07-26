package cn.sskbskdrin.widget.calendar;

import java.util.Calendar;

/**
 * Created by keayuan on 2021/7/23.
 *
 * @author keayuan
 */
public class CalendarUtils {
    public static final long DAY = 24 * 60 * 60 * 1000;
    private static final Calendar calendar = Calendar.getInstance();

    public static int timeToDay(long time) {
        if (time < 0) {
            time -= DAY;
        }
        return Long.valueOf(time / DAY).intValue();
    }

    public static long dayToTime(int day) {
        if (day < 0) {
            return day * DAY + 1;
        }
        return day * DAY;
    }

    public static long dateToTime(int year, int month, int date) {
        Calendar c = calendar;
        c.set(year, month, date);
        return c.getTimeInMillis();
    }

    public static boolean isEqualDayOfMonth(long first, long second) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(first);
        int day = c.get(Calendar.DAY_OF_MONTH);
        c.setTimeInMillis(second);
        return day == c.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isEqualDayOfYear(long first, long second) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(first);
        int day = c.get(Calendar.DAY_OF_YEAR);
        c.setTimeInMillis(second);
        return day == c.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isDayInMonth(long dayTime, long monthTime) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(dayTime);
        int month = c.get(Calendar.MONTH);
        c.setTimeInMillis(monthTime);
        return month == c.get(Calendar.MONTH);
    }

    public static boolean isDayInMonth(int dayTime, int monthTime) {
        return isDayInMonth(dayToTime(dayTime), dayToTime(monthTime));
    }

    public static int getWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (week < 0) {
            week = 6;
        }
        return week;
    }

    public static long getMonthFirstDay(long time, int weekStart) {
        Calendar c = calendar;
        c.setTimeInMillis(time);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
        return c.getTimeInMillis() - ((getWeek(c.getTimeInMillis()) + 7 - weekStart) % 7) * DAY;
    }

    public static long getWeekFirstDay(long time, int weekStart) {
        Calendar c = calendar;
        c.setTimeInMillis(time);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        return c.getTimeInMillis() - DAY * (((getWeek(c.getTimeInMillis()) + 7) - weekStart) % 7);
    }

}

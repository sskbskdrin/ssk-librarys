package cn.sskbskdrin.widget.calendar;

import java.util.Calendar;

/**
 * Created by keayuan on 2021/7/23.
 *
 * @author keayuan
 */
class Utils {
    public static final long DAY = 24 * 60 * 60 * 1000;
    private static final Calendar calendar = Calendar.getInstance();

    public static int timeToDay(long time) {
        return Long.valueOf(time / DAY).intValue();
    }

    public static long dayToTime(int day) {
        return day * DAY + 1;
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

    public static boolean isDayInMonth(int dayTime, long monthTime) {
        return isDayInMonth(dayTime * DAY, monthTime);
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
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1, 0, 0, 1);
        return c.getTimeInMillis() - CalendarView.DAY * (((getWeek(c.getTimeInMillis()) + 7) - weekStart) % 7);
    }

    public static long getWeekFirstDay(long time, int weekStart) {
        Calendar c = calendar;
        c.setTimeInMillis(time);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        return c.getTimeInMillis() - CalendarView.DAY * (((getWeek(c.getTimeInMillis()) + 7) - weekStart) % 7);
    }

}

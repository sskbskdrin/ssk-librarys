package cn.sskbskdrin.widget.calendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Calendar;

/**
 * Created by keayuan on 2021/7/23.
 *
 * @author keayuan
 */
public abstract class Decoration {
    final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    static float scaledDensity = 3;
    static float density = 3;

    private long time;
    private int showMode;
    private int date;

    protected static float sp2px(float sp) {
        return sp * scaledDensity;
    }

    protected static float dp2px(float dp) {
        return density * dp;
    }

    void setTime(long time) {
        this.time = time;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        date = calendar.get(Calendar.YEAR) << 16;
        date |= calendar.get(Calendar.MONTH) << 12;
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (week < 0) {
            week = 6;
        }
        date |= week << 8;
        date |= calendar.get(Calendar.DAY_OF_MONTH);
    }

    long getTime() {
        return time;
    }

    protected int getYear() {
        return date >> 16;
    }

    protected int getMonth() {
        return (date >> 12) & 0x0f;
    }

    protected int getWeek() {
        return (date >> 8) & 0x0f;
    }

    protected int getDay() {
        return date & 0xff;
    }

    protected abstract void onDraw(Canvas canvas, float width, float height);

    void onClick(CalendarView view) {

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

    public static int getWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (week < 0) {
            week = 6;
        }
        return week;
    }

    public enum AlignMode {
        LEFT_TOP, LEFT_CENTER, LEFT_BOTTOM, TOP_CENTER, RIGHT_TOP, RIGHT_CENTER, RIGHT_BOTTOM, BOTTOM_CENTER, CENTER
    }

    public static void drawText(Canvas canvas, String text, float x, float y, AlignMode mode, Paint paint) {
        Rect r = new Rect();
        paint.getTextBounds(text, 0, text.length(), r);
        int height = r.height();
        y -= r.bottom;
        switch (mode) {
            case LEFT_TOP:
                paint.setTextAlign(Paint.Align.LEFT);
                y += height;
                break;
            case LEFT_CENTER:
                paint.setTextAlign(Paint.Align.LEFT);
                y += height >> 1;
                break;
            case LEFT_BOTTOM:
                paint.setTextAlign(Paint.Align.LEFT);
                break;
            case TOP_CENTER:
                paint.setTextAlign(Paint.Align.CENTER);
                y += height;
                break;
            case RIGHT_TOP:
                paint.setTextAlign(Paint.Align.RIGHT);
                y += height;
                break;
            case RIGHT_CENTER:
                paint.setTextAlign(Paint.Align.RIGHT);
                y += height >> 1;
                break;
            case RIGHT_BOTTOM:
                paint.setTextAlign(Paint.Align.RIGHT);
                break;
            case BOTTOM_CENTER:
                paint.setTextAlign(Paint.Align.CENTER);
                break;
            case CENTER:
                paint.setTextAlign(Paint.Align.CENTER);
                y += height >> 1;
                break;
        }
        canvas.drawText(text, x, y, paint);
    }
}

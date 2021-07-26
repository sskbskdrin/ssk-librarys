package cn.sskbskdrin.widget.calendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by keayuan on 2021/7/23.
 *
 * @author keayuan
 */
public abstract class Decoration {
    protected final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    static float scaledDensity = 3;
    static float density = 3;

    private int startDate;
    private int endDate;

    public Decoration(long startTime) {
        this(startTime, startTime);
    }

    public Decoration(long startTime, long endTime) {
        updateDate(startTime, endTime);
    }

    public Decoration(int year, int month, int day) {
        this(year, month, day, year, month, day);
    }

    public Decoration(int year, int month, int day, int toYear, int toMonth, int toDay) {
        this(CalendarUtils.dateToTime(year, month, day), CalendarUtils.dateToTime(toYear, toMonth, toDay));
    }

    public void updateDate(long startTime, long endTime) {
        startDate = CalendarUtils.timeToDay(startTime);
        endDate = CalendarUtils.timeToDay(endTime);
        if (startDate > endDate) {
            int t = startDate;
            startDate = endDate;
            endDate = t;
        }
    }

    public void updateDate(int year, int month, int day, int toYear, int toMonth, int toDay) {
        updateDate(CalendarUtils.dateToTime(year, month, day), CalendarUtils.dateToTime(toYear, toMonth, toDay));
    }

    public int getStartDate() {
        return startDate;
    }

    public int getEndDate() {
        return endDate;
    }

    public boolean isValid(int day) {
        return startDate <= day && day <= endDate;
    }

    public boolean isValid(long time) {
        return isValid(CalendarUtils.timeToDay(time));
    }

    protected static float sp2px(float sp) {
        return sp * scaledDensity;
    }

    protected static float dp2px(float dp) {
        return density * dp;
    }

    protected abstract void onDraw(Canvas canvas, float width, float height, long time);

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

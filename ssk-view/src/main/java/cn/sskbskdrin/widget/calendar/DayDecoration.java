package cn.sskbskdrin.widget.calendar;

import android.graphics.Canvas;

import java.util.Calendar;

/**
 * Created by keayuan on 2021/7/23.
 *
 * @author keayuan
 */
public final class DayDecoration extends Decoration {

    private long currentMonth;
    private boolean enable;
    private int date;
    private long time;

    DayDecoration() {
        super(0);
        mPaint.setColor(0xff303030);
        mPaint.setTextSize(sp2px(16));
    }

    @Override
    public final boolean isValid(int day) {
        return true;
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

    protected int getDayOfMonth() {
        return date & 0xff;
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
        setEnable(CalendarUtils.isDayInMonth(time, currentMonth));
    }

    private void setEnable(boolean enable) {
        this.enable = enable;
        if (getWeek() >= 5) {
            mPaint.setColor(0xf3704b);
        } else {
            mPaint.setColor(0x303030);
        }
        mPaint.setAlpha(enable ? 0xff : 0x80);
    }

    void setCurrentMonth(long monthTime) {
        this.currentMonth = monthTime;
    }

    @Override
    protected void onDraw(Canvas canvas, float width, float height, long time) {
        if (CalendarUtils.isEqualDayOfYear(System.currentTimeMillis(), this.time)) {
            mPaint.setColor(0xff7bbfea);
            canvas.drawCircle(width / 2, height / 2, width / 3, mPaint);
            setEnable(enable);
        }
        drawText(canvas, getDayOfMonth() + "", width / 2, height / 2, AlignMode.CENTER, mPaint);
    }
}

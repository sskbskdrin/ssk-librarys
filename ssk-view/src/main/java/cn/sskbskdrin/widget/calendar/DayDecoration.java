package cn.sskbskdrin.widget.calendar;

import android.graphics.Canvas;
import android.graphics.Color;

import java.util.Calendar;

/**
 * Created by keayuan on 2021/7/23.
 *
 * @author keayuan
 */
public final class DayDecoration extends Decoration {

    private int enableColor = 0xffe03030;
    private int disableColor = 0x80e03030;
    private static long currentMonth;
    private boolean enable;

    public DayDecoration(long time) {
        mPaint.setColor(0xffd03030);
        mPaint.setTextSize(sp2px(16));
        setTime(time);
    }

    void setEnable(boolean enable) {
        this.enable = enable;
        if (enable) {
            mPaint.setColor(enableColor);
        } else {
            mPaint.setColor(disableColor);
        }
    }

    @Override
    protected void onDraw(Canvas canvas, float width, float height) {
        if (isEqualDayOfYear(System.currentTimeMillis(), getTime())) {
            mPaint.setColor(Color.BLUE);
            canvas.drawCircle(width / 2, height / 2, width / 3, mPaint);
            mPaint.setColor(Color.RED);
        }
        drawText(canvas, getDay() + "", width / 2, height / 2, AlignMode.CENTER, mPaint);
    }

    static DayDecoration[][] getDayDecoration(long time, int weekStart) {
        currentMonth = time;
        DayDecoration[][] days = new DayDecoration[6][7];
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
        long firstDay = c.getTimeInMillis() - CalendarView.DAY * (((getWeek(c.getTimeInMillis()) + 7) - weekStart) % 7);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                days[i][j] = new DayDecoration(firstDay);
                firstDay += CalendarView.DAY;
            }
        }
        return days;
    }
}

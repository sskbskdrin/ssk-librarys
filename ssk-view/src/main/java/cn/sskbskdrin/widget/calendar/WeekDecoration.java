package cn.sskbskdrin.widget.calendar;

import android.graphics.Canvas;
import android.graphics.Color;

/**
 * Created by keayuan on 2021/7/23.
 *
 * @author keayuan
 */
public class WeekDecoration extends Decoration {
    private static final String[] WEEKS = {"一", "二", "三", "四", "五", "六", "日"};

    private int startWeek;

    private static WeekDecoration mInstance;

    public WeekDecoration() {
        super(0);
        mPaint.setColor(Color.GRAY);
        mPaint.setTextSize(sp2px(18));
    }

    @Override
    public final boolean isValid(int day) {
        return true;
    }

    void setStartWeek(int week) {
        startWeek = week % 7;
    }

    @Override
    protected void onDraw(Canvas canvas, float width, float height, long time) {
        float w = width / 7;
        for (int i = 0; i < 7; i++) {
            int week = (i + startWeek) % 7;
            mPaint.setColor(week > 4 ? 0xfff3704b : Color.GRAY);
            drawText(canvas, WEEKS[week], w / 2 + w * i, height / 2, AlignMode.CENTER, mPaint);
        }
    }
}

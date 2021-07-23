package cn.sskbskdrin.widget.calendar;

import android.graphics.Canvas;
import android.graphics.Color;

/**
 * Created by keayuan on 2021/7/23.
 *
 * @author keayuan
 */
public class WeekDecoration extends Decoration {
    private static final String[] WEEK = {"一", "二", "三", "四", "五", "六", "日"};

    private int startWeek;

    private static WeekDecoration mInstance;

    private WeekDecoration() {
        mPaint.setColor(Color.GRAY);
        mPaint.setTextSize(sp2px(18));
    }

    static WeekDecoration getInstance() {
        if (mInstance == null) {
            synchronized (WeekDecoration.class) {
                if (mInstance == null) {
                    mInstance = new WeekDecoration();
                }
            }
        }
        return mInstance;
    }

    void setStartWeek(int week) {
        startWeek = week % 7;
    }

    @Override
    protected void onDraw(Canvas canvas, float width, float height) {
        float w = width / 7;
        for (int i = 0; i < 7; i++) {
            drawText(canvas, WEEK[(i + startWeek) % 7], w / 2 + w * i, height / 2, AlignMode.CENTER, mPaint);
        }
    }
}

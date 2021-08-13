package cn.sskbskdrin.lib.demo.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cn.sskbskdrin.base.IFragment;
import cn.sskbskdrin.lib.demo.R;
import cn.sskbskdrin.widget.calendar.CalendarUtils;
import cn.sskbskdrin.widget.calendar.CalendarView;
import cn.sskbskdrin.widget.calendar.Decoration;

/**
 * Created by keayuan on 2021/2/10.
 *
 * @author keayuan
 */
public class CalendarFragment extends IFragment {
    Decoration click;
    Decoration select;
    int step = 0;
    long startTime;

    List<Decoration> list = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.f_calendar_layout;
    }

    @Override
    protected void onInitView(View rootView, Bundle savedInstanceState) {
        CalendarView view = getView(R.id.calendar);
        view.setStartWeek(5);
        view.setOnDateClickListener(time -> {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time);
            if (step == 0) {
                startTime = time;
                select.updateDate(time, time);
                step = 1;
            } else if (step == 1) {
                select.updateDate(startTime, time);
                step = 0;
            }
            view.updateDate();
            showToast(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH));
        });
        view.setOnDateLongClickListener(time -> {
            if (click != null) list.remove(click);
            click = new Decoration(time) {
                @Override
                protected void onDraw(Canvas canvas, float width, float height, long time) {
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setStrokeWidth(dp2px(1));
                    mPaint.setColor(0xffebbfea);
                    canvas.drawCircle(width / 2, height / 2, width / 3, mPaint);
                }
            };
            list.add(click);
            view.updateDate();
        });

        view.setPreDecorationProvider((startDate, endDate) -> list);
        select = new Decoration(0, 0) {
            @Override
            protected void onDraw(Canvas canvas, float width, float height, long time) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(dp2px(1));
                mPaint.setColor(0xffebbfea);
                if (CalendarUtils.getWeek(time) == view.getStartWeek() || CalendarUtils.timeToDay(time) == getStartDate()) {
                    canvas.drawCircle(width / 2, height / 2, (height - dp2px(8)) / 2, mPaint);
                    canvas.drawRect(width / 2, dp2px(4), width, height - dp2px(4), mPaint);
                } else if (CalendarUtils.getWeek(time) == (view.getStartWeek() + 6) % 7 || CalendarUtils.timeToDay(time) == getEndDate()) {
                    canvas.drawCircle(width / 2, height / 2, (height - dp2px(8)) / 2, mPaint);
                    canvas.drawRect(0, dp2px(4), width / 2, height - dp2px(4), mPaint);
                } else {
                    canvas.drawRect(0, dp2px(4), width, height - dp2px(4), mPaint);
                }
            }
        };
        list.add(select);
    }
}

package cn.sskbskdrin.widget.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Collections;
import java.util.List;

/**
 * Created by keayuan on 2021/7/23.
 *
 * @author keayuan
 */
public class CalendarView extends View {

    private boolean showWeek = true;
    private final WeekDecoration weeks;
    private boolean showDay = true;
    private final DayDecoration days;
    private float itemWidth = 0;
    private float itemHeight = 0;
    private long currentTime;
    private int firstDay;
    private boolean isMonthMode = true;
    private int startWeek = 0;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Decoration.scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        Decoration.density = context.getResources().getDisplayMetrics().density;
        weeks = new WeekDecoration();
        weeks.setStartWeek(startWeek);
        days = new DayDecoration();
        setCurrentDateTime(System.currentTimeMillis());
    }

    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
        weeks.setStartWeek(startWeek);
        setCurrentDateTime(currentTime);
    }

    public int getStartWeek() {
        return startWeek;
    }

    public void setCurrentDateTime(long time) {
        if (isMonthMode) {
            firstDay = CalendarUtils.timeToDay(CalendarUtils.getMonthFirstDay(time, startWeek));
        } else {
            firstDay = CalendarUtils.timeToDay(CalendarUtils.getWeekFirstDay(time, startWeek));
        }
        currentTime = time;
        days.setCurrentMonth(time);
        postInvalidate();
    }

    public void setCurrentDateTime(int year, int month, int day) {
        setCurrentDateTime(CalendarUtils.dateToTime(year, month, day));
    }

    public void setShowWeek(boolean show) {
        if (showWeek != show) itemWidth = 0;
        this.showWeek = show;
        postInvalidate();
    }

    public void setShowDay(boolean show) {
        showDay = show;
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        itemWidth = 0;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (itemWidth == 0) {
            int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            itemWidth = width / 7f;
            int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
            itemHeight = height / 7f;
        }
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());
        if (showWeek && weeks != null) {
            canvas.save();
            weeks.onDraw(canvas, getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), itemHeight, 0);
            canvas.restore();
            canvas.translate(0, itemHeight);
        }

        List<Decoration> preDecorationList = getMonthDecorationList(true);
        List<Decoration> postDecorationList = getMonthDecorationList(false);
        int day = firstDay;
        for (int i = 0; i < (isMonthMode ? 6 : 1); i++) {
            for (int j = 0; j < 7; j++) {
                canvas.save();
                canvas.clipRect(0, 0, itemWidth, itemHeight);
                long time = CalendarUtils.dayToTime(day++);
                for (Decoration decoration : preDecorationList) {
                    if (decoration != null && decoration.isValid(time)) {
                        decoration.onDraw(canvas, itemWidth, itemHeight, time);
                    }
                }
                if (showDay && days != null) {
                    days.setTime(time);
                    days.onDraw(canvas, itemWidth, itemHeight, time);
                }
                for (Decoration decoration : postDecorationList) {
                    if (decoration != null && decoration.isValid(time)) {
                        decoration.onDraw(canvas, itemWidth, itemHeight, time);
                    }
                }
                canvas.restore();
                canvas.translate(itemWidth, 0);
            }
            canvas.translate(-itemWidth * 7, itemHeight);
        }
        canvas.restore();
    }

    private List<Decoration> getMonthDecorationList(boolean pre) {
        List<Decoration> list = null;
        DecorationProvider provider = pre ? preDecorationProvider : postDecorationProvider;
        if (provider != null) {
            list = provider.getList(CalendarUtils.dayToTime(firstDay), CalendarUtils.dayToTime(firstDay + 42));
        }
        return list == null ? Collections.<Decoration>emptyList() : list;
    }

    private final GestureDetector detector = new GestureDetector(null, new GestureDetector.SimpleOnGestureListener() {
        int lastDown = -1;

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (getPosition(e) != lastDown) {
                return false;
            } else {
                onClick(CalendarUtils.dayToTime(firstDay + lastDown));
                return true;
            }
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (getPosition(e) == lastDown) {
                onLongClick(CalendarUtils.dayToTime(firstDay + lastDown));
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return (lastDown = getPosition(e)) >= 0;
        }
    });

    private int getPosition(MotionEvent event) {
        float x = event.getX() - getPaddingLeft();
        float y = event.getY();
        int top = getPaddingTop();
        if (showWeek && weeks != null) {
            top += itemHeight;
        }
        if (y <= top) return -1;
        return (int) (x / itemWidth) + (int) ((y - top) / itemHeight) * 7;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    private void onClick(long time) {
        if (onDateClickListener != null) {
            onDateClickListener.onClick(time);
        }
    }

    private void onLongClick(long time) {
        if (onDateLongClickListener != null) {
            onDateLongClickListener.onLongClick(time);
        }
    }

    private OnDateClickListener onDateClickListener;
    private OnDateLongClickListener onDateLongClickListener;

    private DecorationProvider preDecorationProvider;
    private DecorationProvider postDecorationProvider;

    public void setOnDateClickListener(OnDateClickListener onDateClickListener) {
        this.onDateClickListener = onDateClickListener;
    }

    public void setOnDateLongClickListener(OnDateLongClickListener onDateLongClickListener) {
        this.onDateLongClickListener = onDateLongClickListener;
    }

    public void setPreDecorationProvider(DecorationProvider preDecorationProvider) {
        this.preDecorationProvider = preDecorationProvider;
    }

    public void setPostDecorationProvider(DecorationProvider postDecorationProvider) {
        this.postDecorationProvider = postDecorationProvider;
    }

    public interface OnDateClickListener {
        void onClick(long time);
    }

    public interface OnDateLongClickListener {
        void onLongClick(long time);
    }

    public interface DecorationProvider {
        List<Decoration> getList(long startDate, long endDate);
    }
}

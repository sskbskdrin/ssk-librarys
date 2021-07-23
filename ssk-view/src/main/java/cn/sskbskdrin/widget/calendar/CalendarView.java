package cn.sskbskdrin.widget.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

/**
 * Created by keayuan on 2021/7/23.
 *
 * @author keayuan
 */
public class CalendarView extends View {

    public static final long DAY = 24 * 60 * 60 * 1000;

    private boolean showWeek = true;
    private WeekDecoration week;
    private float itemWidth = 0;
    private float itemHeight = 0;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int currentDay;
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
        week = WeekDecoration.getInstance();
        week.setStartWeek(startWeek);
        mPaint.setColor(Color.RED);
        mPaint.setTextSize(60);
        setPadding(40, 40, 40, 40);
        setTime(System.currentTimeMillis());
        int t = firstDay;
        for (int i = 0; i < 42; i++) {
            DayDecoration decoration = new DayDecoration(t * DAY);
            decoration.setEnable(Utils.isDayInMonth(t, currentDay * DAY));
            if (i == 1) {
                addPreDecoration(Utils.dayToTime(t), Utils.dayToTime(t+4), new Decoration() {
                    @Override
                    protected void onDraw(Canvas canvas, float width, float height) {
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(6);
                        paint.setColor(Color.RED);
                        canvas.drawCircle(width / 2, height / 2, width / 3, paint);
                    }
                });
            }
            addPreDecoration(Utils.dayToTime(t++), decoration);
        }
    }

    public void setTime(long time) {
        if (isMonthMode) {
            firstDay = Utils.timeToDay(Utils.getMonthFirstDay(time, startWeek));
        } else {
            firstDay = Utils.timeToDay(Utils.getWeekFirstDay(time, startWeek));
        }
        currentDay = Utils.timeToDay(time);
        postInvalidate();
    }

    public void setTime(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day);
        setTime(c.getTimeInMillis());
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
        if (showWeek && week != null) {
            canvas.save();
            week.onDraw(canvas, getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), itemHeight);
            canvas.restore();
        }
        canvas.translate(0, itemHeight);

        int t = firstDay;
        for (int i = 0; i < (isMonthMode ? 6 : 1); i++) {
            for (int j = 0; j < 7; j++) {
                canvas.save();
                canvas.clipRect(0, 0, itemWidth, itemHeight);
                int temp;
                int lastIndex = 0;
                while (lastIndex < size && (temp = find(list, t, lastIndex, size - 1)) >= 0) {
                    list[temp].decoration.onDraw(canvas, itemWidth, itemHeight);
                    lastIndex = temp + 1;
                }
                t++;
                canvas.restore();
                canvas.translate(itemWidth, 0);
            }
            canvas.translate(-itemWidth * 7, itemHeight);
        }
        canvas.restore();
    }

    private int size = 0;
    private Item[] list = new Item[64];

    public void addPreDecoration(long startTime, Decoration decoration) {
        addPreDecoration(startTime, startTime, decoration);
    }

    public void addPreDecoration(long startTime, long endTime, Decoration decoration) {
        int startDay = Utils.timeToDay(startTime);
        int endDay = Utils.timeToDay(endTime);
        if (startDay > endDay) {
            startDay += endDay;
            endDay = startDay - endDay;
            startDay = startDay - endDay;
        }
        if (size == list.length) {
            list = Arrays.copyOf(list, size + 8);
        }
        list[size++] = new Item(startDay, endDay, decoration);
        sort(list);
    }

    private static void sort(Item[] arr) {
        Arrays.sort(arr, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                if (o1 == null) return 1;
                if (o2 == null) return -1;
                return o1.startTime - o2.startTime;
            }
        });
    }

    private static int find(Item[] list, int day, int left, int right) {
        if (left > right) return -1;
        Item item;
        int mid;
        while (left <= right) {
            mid = (left + right) / 2;
            item = list[mid];
            if (item.startTime <= day && item.endTime >= day) {
                return mid;
            } else if (item.startTime > day) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return -1;
    }

    private static class Item {
        int startTime;
        int endTime;
        Decoration decoration;

        Item(int startTime, int endTime, Decoration decoration) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.decoration = decoration;
        }
    }
}

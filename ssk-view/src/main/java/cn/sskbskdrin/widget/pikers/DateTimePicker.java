package cn.sskbskdrin.widget.pikers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Calendar;

/**
 * Created by sskbskdrin on 2017/十月/5.
 */
public class DateTimePicker extends LinearLayout implements PickerView.onSelectListener {
    PickerNumberView mPickerYearView;
    PickerNumberView mPickerMonthView;
    PickerNumberView mPickerDayView;
    PickerNumberView mPickerHourView;
    PickerNumberView mPickerMinuteView;

    private int minYear;
    private int maxYear;
    private int minMonth;//0-11
    private int maxMonth;
    private int minDay;
    private int maxDay;//1-31

    private int minHour;
    private int maxHour;
    private int minMinute;
    private int maxMinute;

    public DateTimePicker(Context context) {
        this(context, null);
    }

    public DateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
        setTimeRange(getTime(1900, 0, 1, 0, 0), getTime(2100, 11, 31, 23, 59));
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL);
        mPickerYearView = new PickerNumberView(context);
        mPickerMonthView = new PickerNumberView(context);
        mPickerDayView = new PickerNumberView(context);
        mPickerHourView = new PickerNumberView(context);
        mPickerMinuteView = new PickerNumberView(context);
        addView(mPickerYearView, new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        addView(mPickerMonthView, new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        addView(mPickerDayView, new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        addView(mPickerHourView, new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        addView(mPickerMinuteView, new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1));
        initDate(System.currentTimeMillis());
    }

    private void initDate(long time) {
        Calendar mCurrentTime = Calendar.getInstance();
        mCurrentTime.setTimeInMillis(time);
        mPickerYearView.setValue(minYear, maxYear);
        mPickerYearView.setUnitText("年");
        mPickerYearView.setCurrentValue(mCurrentTime.get(Calendar.YEAR));
        mPickerYearView.setOnSelectListener(this);

        mPickerMonthView.setValue(1, 12);
        mPickerMonthView.setUnitText("月");
        mPickerMonthView.setCycle(false);
        mPickerMonthView.setCurrentSelect(mCurrentTime.get(Calendar.MONTH), false);
        mPickerMonthView.setOnSelectListener(this);

        mPickerDayView.setValue(1, mCurrentTime.getActualMaximum(Calendar.DAY_OF_MONTH));
        mPickerDayView.setUnitText("日");
        mPickerDayView.setCurrentSelect(mCurrentTime.get(Calendar.DAY_OF_MONTH) - 1, false);
        mPickerDayView.setOnSelectListener(this);

        mPickerHourView.setValue(0, 23);
        mPickerHourView.setUnitText("时");
        mPickerHourView.setCycle(false);
        mPickerHourView.setCurrentSelect(mCurrentTime.get(Calendar.HOUR_OF_DAY), false);
        mPickerHourView.setOnSelectListener(this);

        mPickerMinuteView.setValue(0, 59);
        mPickerMinuteView.setUnitText("分");
        mPickerMinuteView.setCycle(false);
        mPickerMinuteView.setCurrentSelect(mCurrentTime.get(Calendar.MINUTE), false);
    }

    public void setEnable(boolean date, boolean time) {
        if (date) {
            mPickerYearView.setVisibility(VISIBLE);
            mPickerMonthView.setVisibility(VISIBLE);
            mPickerDayView.setVisibility(VISIBLE);
        } else {
            mPickerYearView.setVisibility(GONE);
            mPickerMonthView.setVisibility(GONE);
            mPickerDayView.setVisibility(GONE);
        }
        if (time) {
            mPickerHourView.setVisibility(VISIBLE);
            mPickerMinuteView.setVisibility(VISIBLE);
        } else {
            mPickerHourView.setVisibility(GONE);
            mPickerMinuteView.setVisibility(GONE);
        }
    }

    public long getSelectTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(mPickerYearView.getCurrentValue(), mPickerMonthView.getCurrentValue() - 1,
	        mPickerDayView.getCurrentValue(), mPickerHourView.getCurrentValue(), mPickerMinuteView.getCurrentValue());
        return calendar.getTimeInMillis();
    }

    public void setTimeRange(long minTimeMillis, long maxTimeMillis) {
        Calendar calendar = Calendar.getInstance();
        minTimeMillis = minTimeMillis < maxTimeMillis ? minTimeMillis : maxTimeMillis;
        maxTimeMillis = minTimeMillis < maxTimeMillis ? maxTimeMillis : minTimeMillis;
        calendar.clear();
        calendar.setTimeInMillis(minTimeMillis);
        minYear = calendar.get(Calendar.YEAR);
        minMonth = calendar.get(Calendar.MONTH);
        minDay = calendar.get(Calendar.DAY_OF_MONTH);
        minHour = calendar.get(Calendar.HOUR_OF_DAY);
        minMinute = calendar.get(Calendar.MINUTE);

        calendar.clear();
        calendar.setTimeInMillis(maxTimeMillis);
        maxYear = calendar.get(Calendar.YEAR);
        maxMonth = calendar.get(Calendar.MONTH);
        maxDay = calendar.get(Calendar.DAY_OF_MONTH);
        maxHour = calendar.get(Calendar.HOUR_OF_DAY);
        maxMinute = calendar.get(Calendar.MINUTE);
        updateYear();
    }

    public long getMaxTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(maxYear, maxMonth, maxDay, maxHour, maxMinute, 0);
        return calendar.getTimeInMillis();
    }

    public long getMinTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(minYear, minMonth, minDay, minHour, minMinute, 0);
        return calendar.getTimeInMillis();
    }

    public void setCurrentTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(minYear, minMonth, minDay, minHour, minMinute);
        long min = calendar.getTimeInMillis();
        if (time < min) {
            time = min;
        }
        calendar.clear();
        calendar.set(maxYear, maxMonth, maxDay, maxHour, maxMinute);
        long max = calendar.getTimeInMillis();
        if (time > max) {
            time = max;
        }
        initDate(time);
        updateYear();
    }

    private void updateYear() {
        int current = Calendar.getInstance().get(Calendar.YEAR);
        mPickerYearView.setValue(minYear, maxYear);
        mPickerYearView.setCurrentValue(current);
        updateMonth(mPickerYearView.getCurrentValue());
    }

    private void updateMonth(int year) {
        int min = minMonth + 1;
        int max = maxMonth + 1;
        int current = mPickerMonthView.getCurrentValue();
        if (year != minYear) {
            min = 1;
        }
        if (year != maxYear) {
            max = 12;
        }
        mPickerMonthView.setValue(min, max);
        mPickerMonthView.setCurrentValue(current);
        updateDay(year, mPickerMonthView.getCurrentValue());
    }

    private void updateDay(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month - 1, 1);
        int min = 1;
        int max = calendar.getActualMaximum(Calendar.DATE);
        int current = mPickerDayView.getCurrentValue();

        if (year == minYear && month == minMonth + 1) {
            min = minDay;
        }
        if (year == maxYear && month == maxMonth + 1) {
            max = maxDay;
        }
        mPickerDayView.setValue(min, max);
        mPickerDayView.setCurrentValue(current);
        if (mPickerHourView.getVisibility() == VISIBLE) {
            updateHour(year, month, mPickerDayView.getCurrentValue());
        }
    }

    private void updateHour(int year, int month, int day) {
        int min = 0;
        int max = 23;
        int current = mPickerHourView.getCurrentValue();

        if (year == minYear && month == minMonth + 1 && day == minDay) {
            min = minHour;
        }
        if (year == maxYear && month == maxMonth + 1 && day == maxDay) {
            max = maxHour;
        }
        mPickerHourView.setValue(min, max);
        mPickerHourView.setCurrentValue(current);
        updateMinute(year, month, day, mPickerHourView.getCurrentValue());
    }

    private void updateMinute(int year, int month, int day, int hour) {
        int min = 0;
        int max = 59;
        int current = mPickerMinuteView.getCurrentValue();

        if (year == minYear && month == minMonth + 1 && day == minDay && hour == minHour) {
            min = minMinute;
        }
        if (year == maxYear && month == maxMonth + 1 && day == maxDay && hour == maxHour) {
            max = maxMinute;
        }
        mPickerMinuteView.setValue(min, max);
        mPickerMinuteView.setCurrentValue(current);
    }

    @Override
    public void onSelect(PickerView view, int position) {

    }

    @Override
    public void onTicker(PickerView view, int oldPosition, int newPosition) {
        if (view == mPickerYearView) {
            updateMonth(mPickerYearView.getCurrentValue());
        } else if (view == mPickerMonthView) {
            updateDay(mPickerYearView.getCurrentValue(), mPickerMonthView.getCurrentValue());
        } else if (view == mPickerDayView) {
            updateHour(mPickerYearView.getCurrentValue(), mPickerMonthView.getCurrentValue(),
	            mPickerDayView.getCurrentValue());
        } else if (view == mPickerHourView) {
            updateMinute(mPickerYearView.getCurrentValue(), mPickerMonthView.getCurrentValue(),
	            mPickerDayView.getCurrentValue(), mPickerHourView.getCurrentValue());
        } else if (view == mPickerMinuteView) {

        }
    }

    public static long getTime(int year, int month, int day, int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month % 12, day, hour, min, 0);
        return calendar.getTimeInMillis();
    }
}

package cn.sskbskdrin.widget.pikers;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Created by ayke on 2016/10/14 0014.
 */
public class PickerNumberView extends PickerView {

	private int mMax = 0;
	private int mMin = 0;
	private StringBuilder builder;
	private static final String NONE = "";

	public PickerNumberView(Context context) {
		this(context, null);
	}

	public PickerNumberView(Context context, AttributeSet attrs) {
		super(context, attrs);
		builder = new StringBuilder();
	}

	public void setValue(int max, int min) {
		mMax = max > min ? max : min;
		mMin = min < max ? min : max;
		notifyDataSetChange();
	}

	@Override
	protected String getItem(int position) {
		if (position < 0) {
			return NONE;
		}
		builder.setLength(0);
		builder.append(position + mMin);
		return builder.toString();
	}

	@Override
	protected int getItemCount() {
		return mMax - mMin + 1;
	}

	public int getCurrentValue() {
		String value = getItem(getCurrentSelect());
		if (TextUtils.isEmpty(value)) {
			return -1;
		} else {
			return Integer.parseInt(value);
		}
	}

	public void setCurrentValue(int value) {
		if (value > mMax) {
			value = mMax;
		} else if (value < mMin) {
			value = mMin;
		}
		setCurrentSelect(value - mMin, false);
	}
}

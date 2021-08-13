package cn.sskbskdrin.base;

import android.view.View;

public interface DebounceOnClickListener extends View.OnClickListener {
    int LAST_TIME_KEY = 0x32875656;

    @Override
    default void onClick(View v) {
        Long lastTime = (Long) v.getTag(LAST_TIME_KEY);
        if (lastTime == null || System.currentTimeMillis() - lastTime > 200) {
            doClick(v);
        }
        v.setTag(LAST_TIME_KEY, System.currentTimeMillis());
    }

    void doClick(View v);
}

package cn.sskbskdrin.base;

import android.view.View;
import android.view.ViewGroup;

import cn.sskbskdrin.base.adapter.IHolder;
import cn.sskbskdrin.util.function.Consumer;

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

    public static void main(String[] args) {
        new B(null).clickItem(new Consumer<HO>() {
            @Override
            public void accept(HO h) {

            }
        });
    }

    class HO extends IHolder<String> {

        public HO(View view) {
            super(view);
        }
    }

    class B extends IHolder.IBuilder<String, HO> {

        protected B(ViewGroup parent) {
            super(parent);
        }

        @Override
        public HO build() {
            return null;
        }
    }
}

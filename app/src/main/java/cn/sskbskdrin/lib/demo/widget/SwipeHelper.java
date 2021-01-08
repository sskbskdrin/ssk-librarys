package cn.sskbskdrin.lib.demo.widget;

import android.util.Log;

/**
 * Created by keayuan on 2021/1/8.
 *
 * @author keayuan
 */
public class SwipeHelper {

    public static final int STATUS_RESET = 0;
    public static final int STATUS_SWIPE = 1;
    public static final int STATUS_PREPARE = 2;
    public static final int STATUS_LOAD = 3;
    public static final int STATUS_COMPLETE = 4;

    int status = 0;

    public void onSwitchChange(int dx, int dy, int offsetX, int offsetY, boolean isTouch) {
        int status = -1;
        if (offsetY == 0) {
            status = STATUS_RESET;
        } else if (offsetY > 0) {
            if (offsetY < handler.getSwipeLoad()) {
                status = STATUS_SWIPE;
            } else if (offsetY < handler.getSwipeMax()) {
                status = STATUS_PREPARE;
            }
        }
        changeStatus(status);
    }

    public void onTouchUp() {

    }

    void setViewOffset(int offsetX, int offsetY) {

    }

    private void changeStatus(int status) {
        changeStatus(status, true);
    }

    private boolean validity(int oldStatus, int status) {
        if (oldStatus == status) return false;
        if (oldStatus == STATUS_LOAD && status != STATUS_COMPLETE) return false;
        if (oldStatus == STATUS_COMPLETE && status != STATUS_RESET) return false;
        return true;
    }

    private void changeStatus(int status, boolean success) {
        if (!validity(this.status, status)) return;
        this.status = status;
        switch (status) {
            case STATUS_RESET:
                handler.onReset();
                break;
            case STATUS_SWIPE:
                handler.onSwipe();
                break;
            case STATUS_PREPARE:
                handler.onPrepare();
                break;
            case STATUS_LOAD:
                handler.onLoad();
                break;
            case STATUS_COMPLETE:
                handler.onComplete(success);
                break;
        }
    }

    float getResistance() {
        int resistance = handler.getResistance();
        if (resistance < 1) resistance = 1;
        if (resistance > 100) resistance = 100;
        return resistance / 100f;
    }

    int getSwipeMax() {
        return handler.getSwipeMax();
    }

    int getSwipeLoad() {
        return handler.getSwipeLoad();
    }

    void startLoad() {
        changeStatus(STATUS_LOAD);
    }

    void complete(boolean success) {
        changeStatus(STATUS_COMPLETE, success);
    }

    boolean isLoading() {
        return status == STATUS_LOAD;
    }

    boolean isComplete() {
        return status == STATUS_COMPLETE;
    }

    private SwipeHandler handler = new SwipeHandler() {
        private static final String TAG = "SwipeHandler";

        @Override
        public void onReset() {
            Log.i(TAG, "onReset: ");
        }

        @Override
        public void onSwipe() {
            Log.i(TAG, "onSwipe: ");
        }

        @Override
        public void onPrepare() {
            Log.i(TAG, "onPrepare: ");
        }

        @Override
        public void onLoad() {
            Log.i(TAG, "onLoad: ");
        }

        @Override
        public void onComplete(boolean success) {
            Log.i(TAG, "onComplete: " + success);
        }

        @Override
        public int getSwipeMax() {
            return 400;
        }

        @Override
        public int getSwipeLoad() {
            return 300;
        }

        @Override
        public int getResistance() {
            return 40;
        }
    };

}

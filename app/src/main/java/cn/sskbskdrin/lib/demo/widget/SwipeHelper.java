package cn.sskbskdrin.lib.demo.widget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by keayuan on 2021/1/8.
 *
 * @author keayuan
 */
public class SwipeHelper {

    public static final int STATUS_RESET = 0;
    public static final int STATUS_SWIPE = 1;
    public static final int STATUS_PREPARE = 2;
    public static final int STATUS_LOADING = 3;
    public static final int STATUS_COMPLETE = 4;

    private HashMap<SwipePosition, ViewHandler> viewHelperHashMap = new HashMap<>(5);

    interface SwipeAble {
        void swipeToTarget(int targetPos);
    }

    private final SwipeAble swipeAble;

    SwipeHelper(SwipeAble swipeAble) {
        this.swipeAble = swipeAble;
        viewHelperHashMap.put(SwipePosition.NONE, new ViewHandler(SwipePosition.NONE, null));
    }

    public void moveSpinner(int dx, int dy, int offsetX, int offsetY, boolean isTouch) {
        if (offsetX == 0 && offsetY == 0) {
            if (getHandler() != null) {
                getHandler().updatePosition(dx, dy, offsetX, offsetY, isTouch);
            }
            handler = null;
        } else {
            updateHandler(offsetX, offsetY);
            if (getHandler() != null) {
                getHandler().updatePosition(dx, dy, offsetX, offsetY, isTouch);
            }
        }
    }

    public void finishSpinner(int offsetX, int offsetY) {
        ViewHandler handler = getHandler();
        handler.onTouchUp();
        if (handler.isLoading()) {
            if (Math.abs(offsetY) > handler.getSwipeLoad()) {
                boolean sign = handler.mDirection == SwipePosition.BOTTOM || handler.mDirection == SwipePosition.RIGHT;
                swipeAble.swipeToTarget((sign ? -1 : 1) * handler.getSwipeLoad());
            }
            return;
        }
        if (handler.isComplete()) {
            swipeAble.swipeToTarget(0);
        } else {
            if (Math.abs(offsetY) > handler.getSwipeLoad()) {
                boolean sign = handler.mDirection == SwipePosition.BOTTOM || handler.mDirection == SwipePosition.RIGHT;
                swipeAble.swipeToTarget((sign ? -1 : 1) * handler.getSwipeLoad());
            } else {
                swipeAble.swipeToTarget(0);
            }
        }
    }

    private void updateHandler(int offsetX, int offsetY) {
        if (offsetY > 0) {
            handler = viewHelperHashMap.get(SwipePosition.TOP);
        } else if (offsetY < 0) {
            handler = viewHelperHashMap.get(SwipePosition.BOTTOM);
        } else if (offsetX > 0) {
            handler = viewHelperHashMap.get(SwipePosition.LEFT);
        } else if (offsetX < 0) {
            handler = viewHelperHashMap.get(SwipePosition.RIGHT);
        } else {
            handler = viewHelperHashMap.get(SwipePosition.NONE);
        }
    }

    float getResistance() {
        return getHandler().getResistance();
    }

    int getSwipeMax() {
        return getHandler().getSwipeMax();
    }

    int getSwipeLoad() {
        return getHandler().getSwipeLoad();
    }

    void complete(SwipePosition position, boolean success) {
        ViewHandler handler = viewHelperHashMap.get(position);
        if (handler != null) {
            handler.refreshComplete(success);
        }
        if (getCurrentDirection() == position) {
            swipeAble.swipeToTarget(0);
        } else {
            if (handler != null) {
                handler.updatePosition(0, 0, 0, 0, false);
            }
        }
    }

    private ViewHandler handler;

    private ViewHandler getHandler() {
        if (handler == null) {
            return viewHelperHashMap.get(SwipePosition.NONE);
        }
        return handler;
    }

    private ViewHandler getHandler(SwipePosition position) {
        ViewHandler handler = viewHelperHashMap.get(position);
        if (handler == null) {
            handler = new ViewHandler(position, null);
            viewHelperHashMap.put(position, handler);
        }
        return handler;
    }

    public void addSwipeHandler(SwipePosition position, SwipeHandler handler) {
        getHandler(position).setHandler(handler);
    }

    public void addSwipeRefreshListener(SwipePosition position, SwipeRefreshListener listener) {
        getHandler(position).addCallback(listener);
    }

    SwipePosition getCurrentDirection() {
        return getHandler().mDirection;
    }

    public void addSwipeChangeListener(SwipeChangeListener listener) {}

    private static class ViewHandler {
        private long mStartTime;
        private int mStatus = STATUS_RESET;
        private SwipeHandler handler;
        private final Set<SwipeHandler> mUIHandlers = new HashSet<>();
        private Set<SwipeRefreshListener> mCallbacks;
        private boolean isReleaseRefresh = true;
        private boolean isRefreshShowView = true;

        private SwipePosition mDirection;

        ViewHandler(SwipePosition direction, SwipeHandler handler) {
            mDirection = direction;
            setHandler(handler);
        }

        private void setHandler(SwipeHandler handler) {
            if (handler != null) {
                mUIHandlers.add(handler);
            }
            this.handler = handler;
        }

        void onTouchUp() {
            if (mStatus == STATUS_PREPARE) {
                mStartTime = System.currentTimeMillis();
                for (SwipeHandler handler : mUIHandlers) {
                    updateStatus(handler, STATUS_LOADING, true);
                }
                if (mCallbacks != null) {
                    for (SwipeRefreshListener callback : mCallbacks) {
                        callback.onLoad(mDirection);
                    }
                }
            }
        }

        boolean isLoading() {
            return mStatus == STATUS_LOADING;
        }

        boolean isComplete() {
            return mStatus == STATUS_COMPLETE;
        }

        void refreshComplete(boolean success) {
            if (mStatus == STATUS_LOADING) {
                for (SwipeHandler handler : mUIHandlers) {
                    updateStatus(handler, STATUS_COMPLETE, success);
                }
            }
        }

        void setReleaseRefresh(boolean releaseRefresh) {
            isReleaseRefresh = releaseRefresh;
        }

        void setRefreshShowView(boolean show) {
            isRefreshShowView = show;
        }

        void addCallback(SwipeRefreshListener callback) {
            if (mCallbacks == null) mCallbacks = new HashSet<>();
            if (!mCallbacks.contains(callback)) mCallbacks.add(callback);
        }

        void updatePosition(int dx, int dy, int offsetX, int offsetY, boolean touch) {
            checkStatus(offsetX, offsetY, touch);
            for (SwipeHandler handler : mUIHandlers) {
                handler.onSwitchChange(dx, dy, offsetX, offsetY, touch);
            }
        }

        private int getSwipeLoad() {
            if (handler != null) {
                return handler.getSwipeLoad();
            }
            return Integer.MAX_VALUE;
        }

        private int getSwipeMax() {
            if (handler != null) {
                return handler.getSwipeMax();
            }
            return 300;
        }

        private float getResistance() {
            float resistance = 0.5f;
            if (handler != null) {
                resistance = handler.getResistance();
            }
            if (resistance < 0) resistance = 0;
            if (resistance > 1) resistance = 1;
            return 1 - resistance;
        }

        private boolean isVertical() {
            return true;
        }

        private void checkStatus(int offsetX, int offsetY, boolean touch) {
            if (STATUS_LOADING != mStatus) {
                int status = mStatus;
                if (offsetX == 0 && offsetY == 0) {
                    mStatus = STATUS_RESET;
                } else if (mStatus == STATUS_COMPLETE) {
                    mStatus = STATUS_COMPLETE;
                } else if (Math.abs(offsetX) >= getSwipeLoad() || Math.abs(offsetY) >= getSwipeLoad()) {
                    if (isReleaseRefresh) mStatus = STATUS_PREPARE;
                    else mStatus = STATUS_LOADING;
                } else {
                    mStatus = STATUS_SWIPE;
                }
                if (status != mStatus) {
                    for (SwipeHandler handler : mUIHandlers) {
                        updateStatus(handler, mStatus, true);
                    }
                    if (mStatus == STATUS_LOADING) {
                        mStartTime = System.currentTimeMillis();
                        if (mCallbacks != null) {
                            for (SwipeRefreshListener callback : mCallbacks) {
                                callback.onLoad(mDirection);
                            }
                        }
                    }
                }
            }
        }

        void updateStatus(SwipeHandler handler, int status, boolean success) {
            mStatus = status;
            switch (mStatus) {
                case STATUS_RESET:
                    handler.onReset();
                    break;
                case STATUS_SWIPE:
                    handler.onSwipe();
                    break;
                case STATUS_PREPARE:
                    handler.onPrepare();
                    break;
                case STATUS_LOADING:
                    handler.onLoad();
                    break;
                case STATUS_COMPLETE:
                    handler.onComplete(success);
                    break;
            }
        }
    }
}

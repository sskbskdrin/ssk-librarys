package cn.sskbskdrin.widget.swipe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by keayuan on 2021/1/8.
 *
 * @author keayuan
 */
final class SwipeHelper {

    private static final int STATUS_RESET = 0;
    private static final int STATUS_SWIPE = 1;
    private static final int STATUS_PREPARE = 2;
    private static final int STATUS_LOADING = 3;
    private static final int STATUS_COMPLETE = 4;

    private HashMap<SwipePosition, SwipeController> viewHelperHashMap = new HashMap<>(5);

    interface SwipeAble {
        void swipeToTarget(int targetPos);
    }

    private final SwipeAble swipeAble;

    SwipeHelper(SwipeAble swipeAble) {
        this.swipeAble = swipeAble;
        viewHelperHashMap.put(SwipePosition.NONE, new SwipeController(SwipePosition.NONE, null));
    }

    void moveSpinner(int dx, int dy, int offsetX, int offsetY, boolean isTouch) {
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

    void finishSpinner(int offsetX, int offsetY) {
        SwipeController handler = getHandler();
        handler.onTouchUp();
        if (handler.isLoading()) {
            if (Math.abs(offsetY) > handler.getSwipeLoad()) {
                boolean sign = handler.mPosition == SwipePosition.BOTTOM || handler.mPosition == SwipePosition.RIGHT;
                swipeAble.swipeToTarget((sign ? -1 : 1) * handler.getSwipeLoad());
            }
            return;
        }
        if (handler.isComplete()) {
            swipeAble.swipeToTarget(0);
        } else {
            if (Math.abs(offsetY) > handler.getSwipeLoad() && handler.isEnable()) {
                boolean sign = handler.mPosition == SwipePosition.BOTTOM || handler.mPosition == SwipePosition.RIGHT;
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
        SwipeController handler = viewHelperHashMap.get(position);
        if (handler != null) {
            handler.refreshComplete(success);
        }
        if (getCurrentPosition() == position) {
            swipeAble.swipeToTarget(0);
        } else {
            if (handler != null) {
                handler.updatePosition(0, 0, 0, 0, false);
            }
        }
    }

    private SwipeController handler;

    private SwipeController getHandler() {
        if (handler == null) {
            return viewHelperHashMap.get(SwipePosition.NONE);
        }
        return handler;
    }

    private SwipeController getHandler(SwipePosition position) {
        SwipeController handler = viewHelperHashMap.get(position);
        if (handler == null) {
            handler = new SwipeController(position, null);
            viewHelperHashMap.put(position, handler);
        }
        return handler;
    }

    void addSwipeHandler(SwipePosition position, SwipeHandler handler) {
        getHandler(position).setHandler(handler);
    }

    void addSwipeRefreshListener(SwipePosition position, SwipeRefreshListener listener) {
        getHandler(position).addRefreshListener(listener);
    }

    SwipePosition getCurrentPosition() {
        return getHandler().mPosition;
    }

    SwipeController getController(SwipePosition position) {
        return viewHelperHashMap.get(position);
    }

    void setEnable(SwipePosition position, boolean enable) {
        SwipeController controller = viewHelperHashMap.get(position);
        if (controller == null && enable) {
            controller = new SwipeController(position, null);
            viewHelperHashMap.put(position, controller);
        }
        if (controller != null) {
            controller.setEnable(enable);
        }
    }

    boolean isEnable(SwipePosition position) {
        SwipeController controller = viewHelperHashMap.get(position);
        return controller != null && controller.isEnable();
    }

    public void addSwipeChangeListener(SwipePositionChangeListener listener) {}

    static class SwipeController {
        private static final String TAG = "SwipeController";
        private int mStatus = STATUS_RESET;
        private SwipeHandler handler;
        private final Set<SwipeStatusChangeListener> mStatusChangeListeners = new HashSet<>();
        private final Set<SwipeRefreshListener> mRefreshListeners = new HashSet<>();
        private final Set<SwipePositionChangeListener> mPositionChangeListeners = new HashSet<>();
        private boolean mEnable = true;

        private final SwipePosition mPosition;

        SwipeController(SwipePosition direction, SwipeHandler handler) {
            mPosition = direction;
            setHandler(handler);
        }

        private void setHandler(SwipeHandler handler) {
            this.handler = handler;
        }

        void onTouchUp() {
            if (mStatus == STATUS_PREPARE) {
                notifyStatusChange(STATUS_LOADING, true);
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
                notifyStatusChange(STATUS_COMPLETE, success);
            }
        }

        void addRefreshListener(SwipeRefreshListener listener) {
            if (listener != null) {
                mRefreshListeners.add(listener);
            }
        }

        void addPositionChangeListener(SwipePositionChangeListener listener) {
            if (listener != null) {
                mPositionChangeListeners.add(listener);
            }
        }

        void addStatusChangeListener(SwipeStatusChangeListener listener) {
            if (listener != null) {
                mStatusChangeListeners.add(listener);
            }
        }

        void updatePosition(int dx, int dy, int offsetX, int offsetY, boolean touch) {
            if (!isEnable()) return;
            checkStatus(offsetX, offsetY, touch);
            for (SwipePositionChangeListener listener : mPositionChangeListeners) {
                if (listener != null) {
                    listener.onSwitchChange(dx, dy, offsetX, offsetY, touch);
                }
            }
        }

        void setEnable(boolean enable) {
            if (mEnable && !enable) {
                updatePosition(0, 0, 0, 0, false);
            }
            this.mEnable = enable;
        }

        boolean isEnable() {
            return mEnable;
        }

        int getSwipeLoad() {
            if (handler != null) {
                return handler.getSwipeLoad();
            }
            return Integer.MAX_VALUE - 2;
        }

        int getSwipeMax() {
            if (handler != null) {
                return handler.getSwipeMax();
            }
            return 0;
        }

        float getResistance() {
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
                int status;
                if (offsetX == 0 && offsetY == 0) {
                    status = STATUS_RESET;
                } else if (mStatus == STATUS_COMPLETE) {
                    status = STATUS_COMPLETE;
                } else if (Math.abs(offsetX) >= getSwipeLoad() || Math.abs(offsetY) >= getSwipeLoad()) {
                    if (handler.isReleaseRefresh()) status = STATUS_PREPARE;
                    else status = STATUS_LOADING;
                } else {
                    status = STATUS_SWIPE;
                }
                notifyStatusChange(status, true);
            }
        }

        private void notifyStatusChange(int status, boolean success) {
            if (mStatus == status) return;
            mStatus = status;
            updateStatus(handler, mStatus, success);

            for (SwipeStatusChangeListener handler : mStatusChangeListeners) {
                updateStatus(handler, mStatus, success);
            }

            if (mStatus == STATUS_LOADING) {
                for (SwipeRefreshListener listener : mRefreshListeners) {
                    if (listener != null) {
                        listener.onLoading(mPosition);
                    }
                }
            }
        }

        private void updateStatus(SwipeStatusChangeListener handler, int status, boolean success) {
            if (handler == null) return;
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
                case STATUS_LOADING:
                    handler.onLoading(mPosition);
                    break;
                case STATUS_COMPLETE:
                    handler.onComplete(success);
                    break;
                default:
            }
        }
    }
}

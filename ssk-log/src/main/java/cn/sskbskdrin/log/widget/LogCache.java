package cn.sskbskdrin.log.widget;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import cn.sskbskdrin.log.Printer;

/**
 * @author sskbskdrin
 * @date 2019-07-07
 */
abstract class LogCache extends Printer implements ViewFilter {

    private static int maxSize = 256 * 1024;

    private static final int WHAT_ADD = 2001;
    private static final int WHAT_CLEAR = 2002;
    private static final int WHAT_FILTER = 2003;

    private ConcurrentLinkedQueue<Log> mCache = new ConcurrentLinkedQueue<>();
    private AtomicInteger cacheSize = new AtomicInteger();
    private List<Log> mList = new ArrayList<>();

    private int mLevel = 0;
    private String filterContent;

    private HandlerThread mThreadHandler;
    private WorkHandler workHandler;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private ReentrantLock lock = new ReentrantLock(true);

    private class WorkHandler extends Handler {
        public WorkHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_ADD:
                    add((Log) msg.obj);
                    break;
                case WHAT_FILTER:
                    filter();
                    break;
                case WHAT_CLEAR:
                    mCache.clear();
                    cacheSize.set(0);
                    filter();
                    break;
            }
        }
    }

    void setCacheMax(int size) {
        maxSize = size;
    }

    @Override
    public void onFilter(boolean clear, int level, String content) {
        mLevel = level;
        filterContent = content;
        if (clear) {
            getWorkHandler().sendEmptyMessage(WHAT_CLEAR);
            return;
        }
        getWorkHandler().sendEmptyMessage(WHAT_FILTER);
    }

    private Handler getWorkHandler() {
        if (mThreadHandler == null) {
            mThreadHandler = new HandlerThread("LogCache");
            mThreadHandler.start();
            workHandler = new WorkHandler(mThreadHandler.getLooper());
        }
        return workHandler;
    }

    @Override
    public void print(int priority, String tag, String message) {
        lock.lock();
        Message.obtain(getWorkHandler(), WHAT_ADD, new Log(priority, tag, message)).sendToTarget();
        lock.unlock();
    }

    private void add(Log log) {
        while (cacheSize.get() + log.size > maxSize) {
            Log temp = mCache.poll();
            if (temp != null) {
                temp.markRemove();
                cacheSize.getAndAdd(-temp.size);
            }
        }
        mCache.offer(log);

        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).isRemove) {
                mList.remove(0);
                i--;
            } else {
                break;
            }
        }
        if (log.priority >= mLevel) {
            if (log.checkSpan()) {
                //            if (TextUtils.isEmpty(filterContent) || log.content.contains(filterContent)) {
                mList.add(log);
            }
        }
        post();
    }

    private void filter() {
        mList.clear();
        Log.filter(filterContent);
        for (Log log : mCache) {
            if (log.priority >= mLevel) {
                if (log.checkSpan()) {
                    //                if (TextUtils.isEmpty(filterContent) || log.content.contains(filterContent)) {
                    mList.add(log);
                }
            }
        }
        post();
    }

    private void post() {
        final Log[] list = new Log[mList.size()];
        mList.toArray(list);
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                onRefresh(list);
            }
        });
    }

    protected abstract void onRefresh(Log[] list);
}

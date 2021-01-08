package cn.sskbskdrin.base;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by keayuan on 2020/11/19.
 *
 * @author keayuan
 */
public interface IPost {
    class InPost {
        private Handler mH = null;
        private Executor executor = new ThreadPoolExecutor(5, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        private InPost() {
        }
    }

    InPost inPost = new InPost();

    static void setIOExecutor(Executor executor) {
        if (executor == null) throw new NullPointerException("executor is null");
        inPost.executor = executor;
    }

    default boolean post(Runnable runnable) {
        if (inPost.mH == null) {
            inPost.mH = new Handler(Looper.getMainLooper());
        }
        if (isMainThread()) {
            runnable.run();
        } else {
            inPost.mH.post(runnable);
        }
        return true;
    }

    default boolean postDelayed(Runnable runnable, long delay) {
        return postDelayed(runnable, false, delay);
    }

    default boolean postDelayed(Runnable runnable, boolean hasRemove, long delay) {
        if (inPost.mH == null) {
            inPost.mH = new Handler(Looper.getMainLooper());
        }
        if (hasRemove) {
            removeCallbacks(runnable);
        }
        inPost.mH.postDelayed(runnable, delay);
        return true;
    }

    default boolean removeCallbacks(Runnable runnable) {
        if (inPost.mH != null) {
            inPost.mH.removeCallbacks(runnable);
        }
        return true;
    }

    default void postIO(Runnable runnable) {
        inPost.executor.execute(runnable);
    }

    default boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}

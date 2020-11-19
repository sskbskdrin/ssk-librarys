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
        private Executor executor = new ThreadPoolExecutor(0, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        private InPost() {
        }
    }

    InPost inPost = new InPost();

    static void setIOExecutor(Executor executor) {
        if (executor == null) throw new NullPointerException("executor is null");
        inPost.executor = executor;
    }

    default void post(Runnable runnable) {
        if (inPost.mH == null) {
            inPost.mH = new Handler(Looper.getMainLooper());
        }
        if (isMainThread()) {
            runnable.run();
        } else {
            inPost.mH.post(runnable);
        }
    }

    default void postDelayed(Runnable runnable, long delay) {
        if (inPost.mH == null) {
            inPost.mH = new Handler(Looper.getMainLooper());
        }
        inPost.mH.postDelayed(runnable, delay);
    }

    default void postIO(Runnable runnable) {
        inPost.executor.execute(runnable);
    }

    default boolean isMainThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }
}

package cn.sskbskdrin.base;

import android.os.Handler;
import android.os.Looper;

import java.util.WeakHashMap;
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
    WeakHashMap<IPost, Handler> postMap = new WeakHashMap<>();
    EXE exe = new EXE();

    class EXE {
        private Executor executor;
    }

    static void setIOExecutor(Executor executor) {
        if (executor == null) throw new NullPointerException("executor is null");
        exe.executor = executor;
    }

    static Handler getMainHandler(IPost post) {
        Handler handler = postMap.get(post);
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
            postMap.put(post, handler);
        }
        return handler;
    }

    default boolean post(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            return getMainHandler(this).post(runnable);
        }
        return true;
    }

    default boolean postDelayed(Runnable runnable, long delay) {
        return postDelayed(runnable, false, delay);
    }

    default boolean postDelayed(Runnable runnable, boolean hasRemove, long delay) {
        if (hasRemove) {
            removeCallbacks(runnable);
        }
        return getMainHandler(this).postDelayed(runnable, delay);
    }

    default boolean removeCallbacks(Runnable runnable) {
        Handler handler = postMap.get(this);
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        return true;
    }

    default void postIO(Runnable runnable) {
        if (exe.executor == null) {
            exe.executor = new ThreadPoolExecutor(4, 4, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        }
        exe.executor.execute(runnable);
    }

    default boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    abstract class Run<T> implements Runnable {
        protected T target;

        public Run(T t) {
            target = t;
        }
    }
}

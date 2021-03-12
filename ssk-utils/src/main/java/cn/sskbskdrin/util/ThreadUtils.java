package cn.sskbskdrin.util;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池
 * Created by keayuan on 2021/3/9.
 *
 * @author keayuan
 */
public final class ThreadUtils {
    private static final String TAG = "ThreadUtils";

    private static final SparseArray<SparseArray<ExecutorService>> TYPE_PRIORITY_POOLS = new SparseArray<>();


    private static final int PRIORITY_SINGLE = 8;
    private static final int PRIORITY_CACHED = 4;
    private static final int PRIORITY_IO = Thread.NORM_PRIORITY;
    private static final int PRIORITY_CPU = 7;

    private static final byte TYPE_SINGLE = -1;
    private static final byte TYPE_CACHED = -2;
    private static final byte TYPE_IO = -4;
    private static final byte TYPE_CPU = -8;

    private static Executor sDeliver;
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * Return whether the thread is the main thread.
     *
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static Executor getMainExecutor() {
        if (sDeliver == null) {
            sDeliver = command -> {
                if (command != null) {
                    mainHandler.post(command);
                }
            };
        }
        return sDeliver;
    }

    /**
     * Return a thread pool that uses a single worker thread operating
     * off an unbounded queue, and uses the provided ThreadFactory to
     * create a new thread when needed.
     *
     * @return a single thread pool
     */
    public static ExecutorService getSinglePool() {
        return getSinglePool(PRIORITY_SINGLE);
    }

    /**
     * Return a thread pool that uses a single worker thread operating
     * off an unbounded queue, and uses the provided ThreadFactory to
     * create a new thread when needed.
     *
     * @param priority The priority of thread in the poll.
     * @return a single thread pool
     */
    public static ExecutorService getSinglePool(final int priority) {
        return getPoolByTypeAndPriority(TYPE_SINGLE, priority);
    }

    /**
     * Return a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available.
     *
     * @return a cached thread pool
     */
    public static ExecutorService getCachedPool() {
        return getCachedPool(PRIORITY_CACHED);
    }

    /**
     * Return a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available.
     *
     * @param priority The priority of thread in the poll.
     * @return a cached thread pool
     */
    public static ExecutorService getCachedPool(final int priority) {
        return getPoolByTypeAndPriority(TYPE_CACHED, priority);
    }

    /**
     * Return a thread pool that creates (2 * CPU_COUNT + 1) threads
     * operating off a queue which size is 128.
     *
     * @return a IO thread pool
     */
    public static ExecutorService getIOPool() {
        return getIOPool(PRIORITY_IO);
    }

    /**
     * Return a thread pool that creates (2 * CPU_COUNT + 1) threads
     * operating off a queue which size is 128.
     *
     * @param priority The priority of thread in the poll.
     * @return a IO thread pool
     */
    public static ExecutorService getIOPool(final int priority) {
        return getPoolByTypeAndPriority(TYPE_IO, priority);
    }

    /**
     * Return a thread pool that creates (CPU_COUNT + 1) threads
     * operating off a queue which size is 128 and the maximum
     * number of threads equals (2 * CPU_COUNT + 1).
     *
     * @return a cpu thread pool for
     */
    public static ExecutorService getCPUPool() {
        return getCPUPool(PRIORITY_CPU);
    }

    /**
     * Return a thread pool that creates (CPU_COUNT + 1) threads
     * operating off a queue which size is 128 and the maximum
     * number of threads equals (2 * CPU_COUNT + 1).
     *
     * @param priority The priority of thread in the poll.
     * @return a cpu thread pool for
     */
    public static ExecutorService getCPUPool(final int priority) {
        return getPoolByTypeAndPriority(TYPE_CPU, priority);
    }

    private synchronized static ExecutorService getPoolByTypeAndPriority(final int type, final int priority) {
        SparseArray<ExecutorService> priorityPools = TYPE_PRIORITY_POOLS.get(type);
        if (priorityPools == null) {
            priorityPools = new SparseArray<>();
            TYPE_PRIORITY_POOLS.put(type, priorityPools);
        }
        ExecutorService pool = priorityPools.get(priority);
        if (pool == null) {
            pool = createPoolByTypeAndPriority(type, priority);
            priorityPools.put(priority, pool);
        }
        return pool;
    }

    private static ExecutorService createPoolByTypeAndPriority(final int type, final int priority) {
        switch (type) {
            case TYPE_SINGLE:
                return Executors.newSingleThreadExecutor(new UtilsThreadFactory("single", priority));
            case TYPE_CACHED:
                return new ThreadPoolExecutor(1, 2 * CPU_COUNT + 1, 30, TimeUnit.SECONDS, new SynchronousQueue<>(),
                    new UtilsThreadFactory("cached", priority));
            case TYPE_IO:
                return new ThreadPoolExecutor(2 * CPU_COUNT + 1, 2 * CPU_COUNT + 1, 30, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(128), new UtilsThreadFactory("io", priority));
            case TYPE_CPU:
                return new ThreadPoolExecutor(CPU_COUNT + 1, 2 * CPU_COUNT + 1, 30, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(128), new UtilsThreadFactory("cpu", priority));
            default:
                return Executors.newFixedThreadPool(type, new UtilsThreadFactory("fixed(" + type + ")", priority));
        }
    }

    private static final class UtilsThreadFactory implements ThreadFactory {
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final AtomicInteger threadId = new AtomicInteger(1);
        private final String namePrefix;
        private final int priority;

        UtilsThreadFactory(String prefix, int priority) {
            namePrefix = prefix + "-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-";
            this.priority = priority;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + threadId.getAndIncrement()) {
                @Override
                public void run() {
                    try {
                        super.run();
                    } catch (Throwable t) {
                        Log.e(TAG, "Request threw uncaught throwable", t);
                    }
                }
            };
            Log.d(TAG, "newThread: " + t.getName());
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            t.setPriority(priority);
            return t;
        }
    }
}

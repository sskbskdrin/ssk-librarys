package cn.sskbskdrin.http;

import android.util.Log;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import cn.sskbskdrin.http.url.DefaultRealRequest;

/**
 * Created by keayuan on 2020/4/17.
 *
 * @author keayuan
 */
public final class Config {
    private final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    private static String BASE_URL;

    private static Executor executor;
    private IMap<String, String> iHeader;

    private IRealRequestFactory iRealRequestFactory;

    long readTimeout = 15000;
    long connectedTimeout = 15000;
    private boolean openLog = false;

    final static Config INSTANCE = new Config();

    private Config() {}

    /**
     * 设置base url
     *
     * @param url baseUrl
     * @return Config
     */
    public Config setBaseUrl(String url) {
        BASE_URL = url;
        return this;
    }

    /**
     * 设置全局header
     *
     * @param header header添加器
     * @return Config
     */
    public Config setHeader(IMap<String, String> header) {
        iHeader = header;
        return this;
    }

    /**
     * 设置是否打开log
     *
     * @param log 是否打开
     * @return Config
     */
    public Config setOpenLog(boolean log) {
        openLog = log;
        return this;
    }

    /**
     * 设置实际请求工厂
     *
     * @param factory 请求构建工厂
     * @return Config
     */
    public Config setRealRequestFactory(IRealRequestFactory factory) {
        this.iRealRequestFactory = factory;
        return this;
    }

    /**
     * 连接超时时间
     *
     * @param time 超时时间，单位ms
     * @return Config
     */
    public Config connectedTimeout(long time) {
        connectedTimeout = time;
        return this;
    }

    /**
     * 读取超时时间
     *
     * @param time 超时时间，单位ms
     * @return Config
     */
    public Config readTimeout(long time) {
        readTimeout = time;
        return this;
    }

    /**
     * 设置请求执行器
     *
     * @param executor 执行者
     * @return Config
     */
    public Config setExecuteService(Executor executor) {
        if (executor != null) Config.executor = executor;
        return this;
    }

    void applyHeader(HashMap<String, String> map) {
        if (iHeader != null) {
            iHeader.apply(map);
        }
    }

    IRealRequest getRealRequest() {
        if (iRealRequestFactory != null) {
            return iRealRequestFactory.generateRealRequest();
        }
        return new DefaultRealRequest();
    }

    void execute(Runnable runnable) {
        if (executor == null) {
            synchronized (Config.class) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(CPU_COUNT + 1, CPU_COUNT * 2, 20, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(128), new DefaultThreadFactory(),
                        new ThreadPoolExecutor.DiscardOldestPolicy());
                }
            }
        }
        executor.execute(runnable);
    }

    public boolean isOpenLog() {
        return openLog;
    }

    /**
     * 完善url
     *
     * @param url url
     */
    static String fixUrl(String url) {
        return isFullUrl(url) ? url : BASE_URL + url;
    }

    private static boolean isFullUrl(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = poolNumber.getAndIncrement() + "-HttpThreadPool-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement());
            Log.d("HTTP-Config", "newThread: " + t.getName());
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}

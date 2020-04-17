package cn.sskbskdrin.http;

import java.lang.reflect.Type;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by keayuan on 2020/4/17.
 *
 * @author keayuan
 */
public final class Config {

    private static String BASE_URL;

    static Executor executor = new ThreadPoolExecutor(0, 10, 10, TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory());
    IMap<String, String> iHeader;

    IRealRequestFactory iRealRequestFactory;
    IParseResponse<?> iParseResponse;

    long readTimeout = 15000;
    long connectedTimeout = 15000;

    public final static Config INSTANCE = new Config();

    private Config() {}

    public static void setBaseUrl(String url) {
        BASE_URL = url;
    }

    public Config setHeader(IMap<String, String> header) {
        iHeader = header;
        return this;
    }

    <T> IParseResult<T> parse(IResponse<T> response, Type type) {
        return null;
    }

    /**
     * 连接超时时间
     *
     * @param time 超时时间，单位ms
     */
    public Config connectedTimeout(long time) {
        connectedTimeout = time;
        return this;
    }

    public Config setParseResponse(IParseResponse<?> parseResponse) {
        this.iParseResponse = parseResponse;
        return this;
    }

    public Config setRealRequestFactory(IRealRequestFactory factory) {
        this.iRealRequestFactory = factory;
        return this;
    }

    /**
     * 读取超时时间
     *
     * @param time 超时时间，单位ms
     */
    public Config readTimeout(long time) {
        readTimeout = time;
        return this;
    }

    public Config setExecuteService(Executor executor) {
        if (executor != null) Config.executor = executor;
        return this;
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
            namePrefix = "HttpThreadPool-" + poolNumber.getAndIncrement();
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
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

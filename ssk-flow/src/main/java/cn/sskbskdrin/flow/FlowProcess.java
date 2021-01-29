package cn.sskbskdrin.flow;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by keayuan on 2020/8/20.
 *
 * @author keayuan
 */
public class FlowProcess {

    private static Executor executor = new ThreadPoolExecutor(0, 10, 10, TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>(), new DefaultThreadFactory());

    private Node head;
    private Node tail;
    private Object lastResult;
    private volatile boolean isStart;

    public FlowProcess() {
    }

    /**
     * 开始处理
     */
    public void start() {
        if (isStart) return;
        isStart = true;
        if (head == null) return;
        if (head.isMain) {
            Platform.get().callback(new Flow(this));
        } else {
            executor.execute(new Flow(this));
        }
    }

    /**
     * 设置io线程池
     *
     * @param executor 执行器
     */
    public static void setExecutor(Executor executor) {
        FlowProcess.executor = executor;
    }

    static void execute(Runnable runnable) {
        executor.execute(runnable);
    }

    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = poolNumber.getAndIncrement() + "-FlowThreadPool-";
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

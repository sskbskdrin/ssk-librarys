package cn.sskbskdrin.flow;

import android.util.Log;

import java.io.Closeable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by keayuan on 2021/1/29.
 *
 * @author keayuan
 */

public class FlowProcess<P> implements IFlow<P>, Runnable {
    private static final String TAG = "Flow";

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static ThreadPoolExecutor executor;

    private Node<P, ?> head;
    private Node<?, ?> tail;
    private Object lastResult;
    private volatile boolean isClose;

    private final ReentrantLock mLock = new ReentrantLock();

    public static <T> IFlow<T> create(T t) {
        return new FlowProcess<>(t);
    }

    /**
     * 设置io线程池
     *
     * @param executor 执行器
     */
    public static void setExecutor(ThreadPoolExecutor executor) {
        FlowProcess.executor = executor;
    }

    private final static LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(128);

    private static Executor getExecutor() {
        if (executor == null) {
            synchronized (FlowProcess.class) {
                if (executor == null) {
                    executor = new ThreadPoolExecutor(2, CPU_COUNT, 60, TimeUnit.SECONDS,
                        new ArrayBlockingQueue<Runnable>(CPU_COUNT), new DefaultThreadFactory(),
                        new RejectedExecutionHandler() {
                        @Override
                        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                            while (!queue.offer(r)) {
                                queue.poll();
                            }
                        }
                    });
                }
            }
        }
        return executor;
    }

    private static void execute(Runnable runnable, boolean main) {
        if (main) {
            Platform.get().callback(runnable);
        } else {
            getExecutor().execute(runnable);
        }
    }

    private FlowProcess(P first) {
        lastResult = first;
    }

    @Override
    public void run() {
        if (isClose || head == null) return;
        Node<P, ?> node = head;
        lastResult = node.process(this, (P) lastResult);
        if (!node.isMain) {
            while (executor.getQueue().size() < CPU_COUNT && !queue.isEmpty()) {
                Runnable runnable = queue.poll();
                if (runnable != null) {
                    execute(runnable, false);
                }
            }
        }
        mLock.lock();
        if (head != null) {
            head = (Node<P, ?>) head.next;
            if (head != null) {
                execute(this, head.isMain);
            } else {
                tail = null;
            }
        }
        mLock.unlock();
    }

    private <T> IFlow<T> add(String tag, IProcess<P, T> p, boolean main, Object... args) {
        if (p != null) {
            Node<?, ?> node = new Node<>(tag, p, main, args);
            mLock.lock();
            if (head == null) {
                head = (Node<P, ?>) node;
            } else {
                tail.next = node;
            }
            tail = node;
            mLock.unlock();
        }
        return (IFlow<T>) this;
    }

    @Override
    public <T> IFlow<T> main(IProcess<P, T> p, Object... args) {
        return main(null, p, args);
    }

    @Override
    public <T> IFlow<T> main(String tag, IProcess<P, T> p, Object... args) {
        return add(tag, p, true, args);
    }

    @Override
    public <T> IFlow<T> io(IProcess<P, T> p, Object... args) {
        return io(null, p, args);
    }

    @Override
    public <T> IFlow<T> io(String tag, IProcess<P, T> p, Object... args) {
        return add(tag, p, false, args);
    }

    @Override
    public void remove(String tag) {
        Node pre = head;
        Node next = head.next;
        while (next != null) {
            while (next != null && (tag == next.tag || (tag != null && tag.equals(next.tag)))) {
                next = next.next;
            }
            pre.next = next;
            tail = pre;
            pre = next;
            if (pre != null) {
                tail = pre;
                next = pre.next;
            }
        }

    }

    @Override
    public Closeable start() {
        execute(this, head.isMain);
        return this;
    }

    @Override
    public void close() {
        isClose = true;
        mLock.lock();
        if (head != null) {
            head.next = null;
        }
        head = null;
        tail = null;
        mLock.unlock();
    }

    private static class Node<P, T> {
        private final String tag;
        private final Object[] params;
        private final IProcess<P, T> process;
        private final boolean isMain;
        private Node<?, ?> next;

        private Node(String tag, IProcess<P, T> p, boolean main, Object... args) {
            this.tag = tag;
            process = p;
            isMain = main;
            params = args.length > 0 ? args : null;
        }

        public T process(IFlow<P> flow, P last) {
            if (process == null) return null;
            if (params == null) {
                return process.process(flow, last);
            } else {
                return process.process(flow, last, params);
            }
        }
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
            Log.d(TAG, "new Thread " + t.getName());
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
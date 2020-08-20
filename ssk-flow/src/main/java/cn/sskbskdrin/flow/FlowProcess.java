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

    public FlowProcess(Object first) {
        lastResult = first;
    }

    /**
     * 运行在主线程
     *
     * @param p    Process处理器
     * @param args process参数
     * @param <T>  process参数返回值类型
     * @return process计算结果
     */
    public <T> FlowProcess main(IProcess<T> p, Object... args) {
        if (isStart) return this;
        add(p, true, args);
        return this;
    }

    /**
     * 运行在io线程
     *
     * @param p    Process处理器
     * @param args process参数
     * @param <T>  process参数返回值类型
     * @return process计算结果
     */
    public <T> FlowProcess io(IProcess<T> p, Object... args) {
        if (isStart) return this;
        add(p, false, args);
        return this;
    }

    private <T> void add(IProcess<T> p, boolean isMain, Object... args) {
        if (p == null) return;
        Node<T> node = new Node<>(p, isMain, args);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
        }
        tail = node;
    }

    /**
     * 开始处理
     */
    public void start() {
        if (isStart) return;
        isStart = true;
        Platform.get().callback(circuit);
    }

    /**
     * 设置io线程池
     *
     * @param executor 执行器
     */
    public static void setExecutor(Executor executor) {
        FlowProcess.executor = executor;
    }

    private Runnable circuit = new Runnable() {
        @Override
        public void run() {
            if (head == null) return;
            boolean isMain = head.isMain;
            Thread.currentThread().setName("flow-" + (isMain ? "main" : "io"));
            int[] jump = new int[1];
            while (head != null && head.isMain == isMain) {
                jump[0] = 0;
                lastResult = head.process(jump, lastResult);
                head = head.next;
                if (jump[0] == -1) head = null;
                while (head != null && jump[0] > 0) {
                    head = head.next;
                    jump[0]--;
                }
            }
            if (head != null) {
                if (isMain) {
                    executor.execute(this);
                } else {
                    Platform.get().callback(this);
                }
            }
        }
    };

    private static class Node<T> implements IProcess<T> {
        private Object[] params;
        private IProcess<T> process;
        private boolean isMain;
        private Node next;

        private Node(IProcess<T> p, boolean main, Object... args) {
            process = p;
            isMain = main;
            if (args.length > 0) params = args;
        }

        @Override
        public T process(int[] interrupt, Object... objects) {
            Object obj = objects.length > 0 ? objects[0] : null;
            if (obj == null) {
                if (params == null) {
                    return process.process(interrupt);
                } else {
                    return process.process(interrupt, params);
                }
            } else {
                if (params == null) {
                    return process.process(interrupt, obj);
                } else {
                    return process.process(interrupt, obj, params);
                }
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

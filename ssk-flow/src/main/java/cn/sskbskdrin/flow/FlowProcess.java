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
     * 构造方法
     *
     * @param first 执行第一条IProcess时传的参数
     */
    public FlowProcess(Object first) {
        lastResult = first;
    }

    public <T, L> FlowProcess main(IProcess<T, L> p, Object... args) {
        main(null, p, args);
        return this;
    }

    public <T, L> FlowProcess main(String tag, IProcess<T, L> p, Object... args) {
        add(tag, p, true, args);
        return this;
    }

    public <T, L> FlowProcess io(IProcess<T, L> p, Object... args) {
        io(null, p, args);
        return this;
    }

    public <T, L> FlowProcess io(String tag, IProcess<T, L> p, Object... args) {
        add(tag, p, false, args);
        return this;
    }

    private <T, L> void add(String tag, IProcess<T, L> p, boolean isMain, Object... args) {
        if (p == null || isStart) return;
        Node<T, L> node = new Node<>(tag, p, isMain, args);
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

    private static class Flow implements IFlow, Runnable {

        private Node head;
        private Node tail;
        private Object lastResult;

        private Flow(FlowProcess process) {
            head = process.head;
            tail = process.tail;
            lastResult = process.lastResult;
        }

        @Override
        public void run() {
            if (head == null) return;
            boolean isMain = head.isMain;
            Thread.currentThread().setName("flow-" + (isMain ? "main" : "io"));
            while (head != null && head.isMain == isMain) {
                lastResult = head.process(Flow.this, lastResult);
                head = head.next;
            }
            if (head != null) {
                if (isMain) {
                    executor.execute(this);
                } else {
                    Platform.get().callback(this);
                }
            } else {
                tail = null;
            }
        }

        @Override
        public <T, L> IFlow main(IProcess<T, L> p, Object... args) {
            main(null, p, args);
            return this;
        }

        @Override
        public <T, L> IFlow main(String tag, IProcess<T, L> p, Object... args) {
            add(tag, p, true, args);
            return this;
        }

        private <T, L> void add(String tag, IProcess<T, L> p, boolean main, Object... args) {
            if (p == null) return;
            Node<T, L> node = new Node<>(tag, p, main, args);
            if (head == null) {
                head = node;
                tail = node;
            } else {
                tail.next = node;
            }
            tail = node;
        }

        @Override
        public <T, L> IFlow io(IProcess<T, L> p, Object... args) {
            io(null, p, args);
            return this;
        }

        @Override
        public <T, L> IFlow io(String tag, IProcess<T, L> p, Object... args) {
            add(tag, p, false, args);
            return this;
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
        public void removeAll() {
            head.next = null;
            tail = head;
        }
    }

    private static class Node<T, L> implements IProcess<T, L> {
        private String tag;
        private Object[] params;
        private IProcess<T, L> process;
        private boolean isMain;
        private Node next;

        private Node(String tag, IProcess<T, L> p, boolean main, Object... args) {
            this.tag = tag;
            process = p;
            isMain = main;
            if (args.length > 0) params = args;
        }

        @Override
        public T process(IFlow flow, L last, Object... objects) {
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

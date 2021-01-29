package cn.sskbskdrin.flow;

import java.io.Closeable;

/**
 * Created by keayuan on 2021/1/29.
 *
 * @author keayuan
 */

class Flow<L> implements IFlow<L>, Runnable {

    private Node head;
    private Node tail;
    private Object lastResult;
    private volatile boolean isClose;

    Flow() {}

    @Override
    public void run() {
        if (isClose || head == null) return;

        boolean isMain = head.isMain;
        while (head != null && head.isMain == isMain) {
            lastResult = head.process(Flow.this, lastResult);
            head = head.next;
        }

        if (head != null) {
            if (isMain) {
                FlowProcess.execute(this);
            } else {
                Platform.get().callback(this);
            }
        } else {
            tail = null;
        }
    }

    private <T> void add(String tag, IProcess<T, L> p, boolean main, Object... args) {
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
    public <T> IFlow<T> main(IProcess<T, L> p, Object... args) {
        main(null, p, args);
        return (IFlow<T>) this;
    }

    @Override
    public <T> IFlow<T> main(String tag, IProcess<T, L> p, Object... args) {
        add(tag, p, true, args);
        return (IFlow<T>) this;
    }

    @Override
    public <T> IFlow<T> io(IProcess<T, L> p, Object... args) {
        io(null, p, args);
        return (IFlow<T>) this;
    }

    @Override
    public <T> IFlow<T> io(String tag, IProcess<T, L> p, Object... args) {
        add(tag, p, false, args);
        return (IFlow<T>) this;
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
        if (head != null) {
            if (head.isMain) {
                Platform.get().callback(this);
            } else {
                FlowProcess.execute(this);
            }
        }
        return this;
    }

    @Override
    public void close() {
        isClose = true;
        head.next = null;
        tail = head;
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
        public T process(IFlow<T> flow, L last, Object... objects) {
            if (params == null) {
                return process.process(flow, last);
            } else {
                return process.process(flow, last, params);
            }
        }
    }
}
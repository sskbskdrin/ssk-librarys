package cn.sskbskdrin.flow;

/**
 * Created by keayuan on 2020/8/20.
 *
 * @author keayuan
 */
public class Flow {
    private Node head;
    private Node tail;

    public static Flow get(int params) {
        return new Flow();
    }

    public Flow main(Process p) {
        add(new Node(p, true));
        return this;
    }

    public Flow io(Process p) {
        add(new Node(p, false));
        return this;
    }

    private void add(Node node) {
        if (node == null) return;
        if (head == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
        }
        tail = node;
    }

    public void start() {
        while (head != null) {
            if (head.isMain) {

            }
        }
    }

    private class MainTask implements Runnable {

        @Override
        public void run() {
            while (head != null && head.isMain) {
                head.process.process();
            }
            if (head != null) {
                //TODO runIOTask
            }
        }
    }

    private class IOTask implements Runnable {

        @Override
        public void run() {
            while (head != null && !head.isMain) {
                head.process.process(null);
                head = head.next;
            }
            if (head != null) {
                //TODO runIOMain
            }
        }
    }

    private static class Node {
        private Process process;
        private boolean isMain;
        private Node next;

        private Node(Process p, boolean main) {
            process = p;
            isMain = main;
        }
    }
}

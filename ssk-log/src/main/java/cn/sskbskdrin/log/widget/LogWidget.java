package cn.sskbskdrin.log.widget;

import android.app.Activity;
import android.app.Application;
import android.view.ViewGroup;

import cn.sskbskdrin.log.Printer;

/**
 * Created by ex-keayuan001 on 2019-07-05.
 *
 * @author ex-keayuan001
 */
public class LogWidget {
    private static LogWidget instance;

    private LogWidget() {}

    static LogWidget getInstance() {
        if (instance == null) {
            instance = new LogWidget();
        }
        return instance;
    }

    private LogView root;

    private LogCache printer = new LogCache() {
        @Override
        protected void onRefresh(Log[] list) {
            root.updateList(list);
        }
    };

    public static void setCacheSize(int size) {
        getInstance().printer.setCacheMax(size);
    }

    public static void init(Application context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        context.unregisterActivityLifecycleCallbacks(LogLifecycle.getInstance());
        context.registerActivityLifecycleCallbacks(LogLifecycle.getInstance());
    }

    void attach(Activity activity) {
        if (root == null) {
            root = new LogView(activity);
            root.setFilter(printer);
        }
        if (root.getParent() != null) {
            ((ViewGroup) root.getParent()).removeView(root);
        }
        ViewGroup group = activity.findViewById(android.R.id.content);
        group.addView(root);
    }

    public static Printer getPrinter() {
        return getInstance().printer;
    }
}

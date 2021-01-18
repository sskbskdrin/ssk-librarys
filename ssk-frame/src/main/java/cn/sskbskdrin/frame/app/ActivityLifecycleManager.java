package cn.sskbskdrin.frame.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ex-keayuan001
 * @date 2017/8/21
 */
public class ActivityLifecycleManager implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "ActivityLifecycleManage";

    private final List<Activity> activities = new ArrayList<>();

    private volatile static ActivityLifecycleManager mInstance;

    private ActivityLifecycleManager() {
    }

    public static ActivityLifecycleManager getInstance() {
        if (mInstance == null) {
            synchronized (ActivityLifecycleManager.class) {
                if (mInstance == null) {
                    mInstance = new ActivityLifecycleManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activities.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activities.remove(activity);
    }

    public void finishAllActivity() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public Activity getTopActivity() {
        if (activities.size() > 0) {
            return activities.get(activities.size() - 1);
        }
        return null;
    }
}

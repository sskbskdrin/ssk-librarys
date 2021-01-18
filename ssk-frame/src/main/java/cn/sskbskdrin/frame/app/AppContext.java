package cn.sskbskdrin.frame.app;

import android.app.Application;

/**
 * Created by keayuan on 2021/1/18.
 *
 * @author keayuan
 */
public class AppContext extends Application {
    private static Application mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        registerActivityLifecycleCallbacks(ActivityLifecycleManager.getInstance());
        CrashHandler.getInstance().init(this);
    }

    public static Application get() {
        return mInstance;
    }
}

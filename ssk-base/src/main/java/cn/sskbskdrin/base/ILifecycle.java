package cn.sskbskdrin.base;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by keayuan on 2021/1/18.
 *
 * @author keayuan
 */
interface ILifecycle {

    void onActivityCreated(Activity activity, Bundle savedInstanceState);

    void onActivityStarted(Activity activity);

    void onActivityResumed(Activity activity);

    void onActivityPaused(Activity activity);

    void onActivityStopped(Activity activity);

    void onActivitySaveInstanceState(Activity activity, Bundle outState);

    void onActivityDestroyed(Activity activity);
}

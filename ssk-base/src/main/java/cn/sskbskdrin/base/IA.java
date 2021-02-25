package cn.sskbskdrin.base;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * @author keayuan
 * 2020/3/27
 */
public interface IA extends IWindow, IContext, IView, IPermission, IResource, IPost {

    @Override
    Context getContext();

    /**
     * 判断页面是否已经销毁
     *
     * @return 是否已经销毁
     */
    @Override
    boolean isFinish();

    default void openActivity(Intent intent) {
        openActivity(intent, -1);
    }

    default void openActivity(Intent intent, int requestCode) {
        Context context = context();
        if (context != null) {
            try {
                if (requestCode >= 0) {
                    if (context instanceof Activity) {
                        ((Activity) context).startActivityForResult(intent, requestCode);
                    } else {
                        throw new IllegalStateException("context is not activity " + context);
                    }
                } else {
                    context.startActivity(intent);
                }
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                throw new ActivityNotFoundException("activity not found " + intent);
            }
        } else {
            throw new IllegalStateException("context is null");
        }
    }

    default void openActivity(Class clazz) {
        openActivity(clazz, null);
    }

    default void openActivity(Class clazz, int requestCode) {
        openActivity(clazz, null, requestCode);
    }

    default void openActivity(Class clazz, Bundle bundle) {
        openActivity(clazz, bundle, -1);
    }

    default void openActivity(Class clazz, Bundle bundle, int requestCode) {
        Context context = context();
        if (context != null) {
            Intent intent = new Intent(context, clazz);
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            try {
                if (requestCode >= 0) {
                    if (context instanceof Activity) {
                        ((Activity) context).startActivityForResult(intent, requestCode);
                    } else {
                        throw new IllegalStateException("context is not activity " + context);
                    }
                } else {
                    context.startActivity(intent);
                }
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                throw new ActivityNotFoundException("activity not found " + clazz.getCanonicalName());
            }
        } else {
            throw new IllegalStateException("context is null");
        }
    }

}

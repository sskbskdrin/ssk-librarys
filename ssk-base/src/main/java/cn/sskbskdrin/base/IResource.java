package cn.sskbskdrin.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;

/**
 * @author keayuan
 * 2020/3/27
 */
public interface IResource extends IContext {

    default int color(int resId) {
        Context context = context();
        int color = 0;
        if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                color = context.getColor(resId);
            } else {
                color = context.getResources().getColor(resId);
            }
        }
        return color;
    }

    default Drawable drawable(int resId) {
        Drawable drawable = null;
        Context context = context();
        if (context != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                drawable = context.getResources().getDrawable(resId);
            } else {
                drawable = context.getDrawable(resId);
            }
        }
        return drawable;
    }

    default String string(int resId, Object... args) {
        Context context = context();
        if (context != null) {
            return context.getString(resId, args);
        }
        return null;
    }

    default int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context().getResources()
            .getDisplayMetrics());
    }
}

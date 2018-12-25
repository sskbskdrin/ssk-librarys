package cn.sskbskdrin.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by sskbskdrin on 2018/9/19.
 */
public final class ProxyView {

    private Dialog dialog;
    private Toast toast;
    private final WeakReference<IView> VIEW;

    public ProxyView(@NonNull IView view) {
        VIEW = new WeakReference<>(view);
    }

    public boolean isFinish() {
        return VIEW.get() == null || VIEW.get().isFinish();
    }

    private Context getContext() {
        if (VIEW.get() != null) {
            return VIEW.get().context();
        }
        return null;
    }

    public void showLoadingDialog(int resId) {
        Context context = getContext();
        if (context != null) {
            showLoadingDialog(context.getString(resId));
        }
    }

    public void showLoadingDialog(String content) {
        if (dialog != null) {
            dialog.dismiss();
        }
        if (!isFinish() && VIEW.get() != null) {
            dialog = VIEW.get().generateLoadingDialog(content);
            dialog.show();
        }
    }

    public void hideLoadingDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = null;
    }

    public void showToast(String text) {
        showToast(text, false);
    }

    public void showToast(String text, boolean isLong) {
        if (toast != null) {
            toast.cancel();
        }
        Context context = getContext();
        if (context != null) {
            toast = Toast.makeText(context, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private <T extends View> T getView(@IdRes int viewId) {
        if (VIEW.get() != null) {
            return VIEW.get().getView(viewId);
        }
        return null;
    }


    public void showView(boolean show, @IdRes int... ids) {
        if (ids != null) {
            int visible = show ? View.VISIBLE : View.GONE;
            for (int id : ids) {
                View view = getView(id);
                if (view != null) {
                    view.setVisibility(visible);
                }
            }
        }
    }

    public static void showView(boolean show, View... views) {
        if (views != null) {
            int visible = show ? View.VISIBLE : View.GONE;
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(visible);
                }
            }
        }
    }

    public void setBackground(@IdRes int viewId, Drawable drawable) {
        setBackground(getView(viewId), drawable);
    }

    public static void setBackground(View view, Drawable drawable) {
        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        }
    }

    public static void setBackgroundResource(View view, @DrawableRes int resId) {
        if (view != null) {
            view.setBackgroundResource(resId);
        }
    }

    public static void setBackgroundColor(View view, @ColorInt int color) {
        if (view != null) {
            view.setBackgroundColor(color);
        }
    }

    public void setText(@IdRes int viewId, @StringRes int resId) {
        TextView view = null;
        if (VIEW.get() != null) {
            view = VIEW.get().getView(viewId);
        }
        if (view != null) {
            view.setText(resId);
        }
    }

    public static void setText(TextView view, CharSequence text) {
        if (view != null) {
            view.setText(text != null ? text : "");
        }
    }

    public void setText(@IdRes int viewId, CharSequence text) {
        TextView view = getView(viewId);
        if (view != null) {
            view.setText(text != null ? text : "");
        }
    }

    public void setImageDrawable(@IdRes int viewId, Drawable drawable) {
        setImageDrawable((ImageView) getView(viewId), drawable);
    }

    public void setImageResource(@IdRes int viewId, @DrawableRes int resId) {
        setImageResource((ImageView) getView(viewId), resId);
    }

    public void setImageBitmap(@IdRes int viewId, Bitmap bitmap) {
        setImageBitmap((ImageView) getView(viewId), bitmap);
    }

    public static void setImageDrawable(ImageView view, Drawable drawable) {
        if (view != null) {
            view.setImageDrawable(drawable);
        }
    }

    public static void setImageResource(ImageView view, @DrawableRes int resId) {
        if (view != null) {
            view.setImageResource(resId);
        }
    }

    public static void setImageBitmap(ImageView view, Bitmap bitmap) {
        if (view != null) {
            view.setImageBitmap(bitmap);
        }
    }
}

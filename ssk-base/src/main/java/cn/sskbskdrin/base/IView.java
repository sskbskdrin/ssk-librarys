package cn.sskbskdrin.base;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public interface IView extends IContext, IResource {
    /**
     * 通过id查找view
     *
     * @param id  view 的 id
     * @param <T> view 的类型
     * @return 返回找到的view，找不到则返回空
     */
    default <T extends View> T getView(int id){
        return getView((View) this,id);
    }

    @SuppressWarnings("unchecked")
    static <T extends View> T getView(View parent, int id) {
        if (parent != null) {
            return (T) parent.findViewById(id);
        }
        return null;
    }

    default int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
            context().getResources().getDisplayMetrics());
    }

    default void showView(boolean show, int... ids) {
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

    default void showView(boolean show, View... views) {
        if (views != null) {
            int visible = show ? View.VISIBLE : View.GONE;
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(visible);
                }
            }
        }
    }

    default void setBackground(int viewId, Drawable drawable) {
        setBackground(getView(viewId), drawable);
    }

    default void setBackground(View view, Drawable drawable) {
        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        }
    }

    default void setBackgroundResource(View view, int resId) {
        if (view != null) {
            view.setBackgroundResource(resId);
        }
    }

    default void setBackgroundColor(View view, int color) {
        if (view != null) {
            view.setBackgroundColor(color);
        }
    }

    default void setText(int viewId, int resId) {
        TextView view = getView(viewId);
        if (view != null) {
            view.setText(resId);
        }
    }

    default void setText(TextView view, CharSequence text) {
        if (view != null) {
            view.setText(text != null ? text : "");
        }
    }

    default void setText(int viewId, CharSequence text) {
        TextView view = getView(viewId);
        if (view != null) {
            view.setText(text != null ? text : "");
        }
    }

    default void setImageDrawable(int viewId, Drawable drawable) {
        setImageDrawable(getView(viewId), drawable);
    }

    default void setImageResource(int viewId, int resId) {
        setImageResource(getView(viewId), resId);
    }

    default void setImageBitmap(int viewId, Bitmap bitmap) {
        setImageBitmap(getView(viewId), bitmap);
    }

    default void setImageDrawable(ImageView view, Drawable drawable) {
        if (view != null) {
            view.setImageDrawable(drawable);
        }
    }

    default void setImageResource(ImageView view, int resId) {
        if (view != null) {
            view.setImageResource(resId);
        }
    }

    default void setImageBitmap(ImageView view, Bitmap bitmap) {
        if (view != null) {
            view.setImageBitmap(bitmap);
        }
    }
}

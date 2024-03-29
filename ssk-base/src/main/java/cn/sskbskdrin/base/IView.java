package cn.sskbskdrin.base;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface IView extends IContext, IResource, IPost {
    /**
     * 通过id查找view
     *
     * @param id  view 的 id
     * @param <V> view 的类型
     * @return 返回找到的view，找不到则返回空
     */
    default <V extends View> V getView(int id) {
        if (this instanceof Activity) {
            return getView(((Activity) this).getWindow().getDecorView(), id);
        }
        return getView((View) this, id);
    }

    @SuppressWarnings("unchecked")
    static <V extends View> V getView(View parent, int id) {
        if (parent != null) {
            return (V) parent.findViewById(id);
        }
        return null;
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

    default void invisibleView(View... views) {
        if (views != null) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.INVISIBLE);
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
        setText(getView(viewId), text);
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

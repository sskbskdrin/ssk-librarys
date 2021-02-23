package cn.sskbskdrin.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import cn.sskbskdrin.view.R;

/**
 * Created by firmament on 2017/3/8.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class RoundView extends FrameLayout {
    private static final String TAG = "RoundView";

    private final RoundDelegate delegate;

    public RoundView(Context context) {
        this(context, null);
    }

    public RoundView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        delegate = new RoundDelegate(this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundView);
        float strokeWidth = a.getFloat(R.styleable.RoundView_android_strokeWidth, 0);
        if (strokeWidth != 0) {
            strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, strokeWidth,
                getResources().getDisplayMetrics());
        }
        delegate.setStrokeWidth(strokeWidth);
        delegate.setStrokeColor(a.getColor(R.styleable.RoundView_android_strokeColor, 0));
        delegate.setRadius(a.getDimension(R.styleable.RoundView_android_radius, 0));
        delegate.setTopLeftRadius(a.getDimension(R.styleable.RoundView_android_topLeftRadius, -1));
        delegate.setTopRightRadius(a.getDimension(R.styleable.RoundView_android_topRightRadius, -1));
        delegate.setBottomLeftRadius(a.getDimension(R.styleable.RoundView_android_bottomLeftRadius, -1));
        delegate.setBottomRightRadius(a.getDimension(R.styleable.RoundView_android_bottomRightRadius, -1));
        a.recycle();
    }

    @Override
    public final void draw(Canvas canvas) {
        delegate.preDraw(canvas);
        super.draw(canvas);
        delegate.postDraw(canvas);
    }

    public void setStrokeWidth(float strokeWidth) {
        delegate.setStrokeWidth(strokeWidth);
    }

    public void setStrokeColor(int strokeColor) {
        delegate.setStrokeColor(strokeColor);
    }

    public void setRadius(float radius) {
        delegate.setRadius(radius);
    }

    public void setBottomRightRadius(float bottomRightRadius) {
        delegate.setBottomRightRadius(bottomRightRadius);
    }

    public void setTopLeftRadius(float topLeftRadius) {
        delegate.setTopLeftRadius(topLeftRadius);
    }

    public void setTopRightRadius(float topRightRadius) {
        delegate.setTopRightRadius(topRightRadius);
    }

    public void setBottomLeftRadius(float bottomLeftRadius) {
        delegate.setBottomLeftRadius(bottomLeftRadius);
    }
}
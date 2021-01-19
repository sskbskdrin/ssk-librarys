package cn.sskbskdrin.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BannerIndicatorView extends View implements BannerView.OnScrollListener {

    private int focusColor = Color.YELLOW;
    private int normalColor = Color.GRAY;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mCount = 0;
    private int margin = dp2px(5);
    private int cycleRadius = dp2px(3);
    private int selected;
    private float offset;

    public BannerIndicatorView(Context context) {
        this(context, null);
    }

    public BannerIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public BannerIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setCount(int count) {
        mCount = count;
        postInvalidate();
    }

    public void setSelected(int position) {
        selected = position;
        postInvalidate();
    }

    public void setSpace(int px) {
        margin = px;
        postInvalidate();
    }

    public void setColor(int focus, int normal) {
        focusColor = focus;
        normalColor = normal;
        postInvalidate();
    }

    public void setCycleRadius(int radius) {
        cycleRadius = radius;
        postInvalidate();
    }

    private int dp2px(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCount > 0) {
            int w = mCount * (cycleRadius * 2 + margin) - margin;
            int x = (getWidth() - w) / 2 + cycleRadius;
            int y = getHeight() - getPaddingBottom() - cycleRadius * 2 + cycleRadius;
            mPaint.setColor(normalColor);
            for (int i = 0; i < mCount; i++) {
                canvas.drawCircle(x + i * (margin + 2 * cycleRadius), y, cycleRadius, mPaint);
            }
            mPaint.setColor(focusColor);
            canvas.drawCircle(x + (selected + offset) * (margin + 2 * cycleRadius), y, cycleRadius, mPaint);
        }
    }

    @Override
    public void onScroll(BannerView parent, int position, float offset) {
        selected = position;
        this.offset = offset;
        if (selected == mCount - 1) {
            this.offset = 0;
            if (offset > 0.5) {
                selected = 0;
            }
        }
        postInvalidate();
    }
}

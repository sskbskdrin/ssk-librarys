package cn.sskbskdrin.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;

/**
 * Created by keayuan on 2021/1/28.
 *
 * @author keayuan
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class RoundDelegate {

    private float strokeWidth;
    private int strokeColor;
    private float radius;
    private float topLeftRadius;
    private float topRightRadius;
    private float bottomLeftRadius;
    private float bottomRightRadius;

    private Paint mPaint;
    private final Path mPath = new Path();
    private final Path mStrokePath = new Path();
    private final float[] radii = new float[8];
    private final float[] strokeRadii = new float[8];
    private boolean mDirty = true;
    private View view;

    RoundDelegate(View view) {
        this.view = view;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        view.setClipToOutline(true);
    }

    void preDraw(Canvas canvas) {
        if (mDirty) {
            mDirty = false;
            mPath.reset();
            mStrokePath.reset();
            radii[0] = radii[1] = topLeftRadius < 0 ? radius : topLeftRadius;
            radii[2] = radii[3] = topRightRadius < 0 ? radius : topRightRadius;
            radii[4] = radii[5] = bottomRightRadius < 0 ? radius : bottomRightRadius;
            radii[6] = radii[7] = bottomLeftRadius < 0 ? radius : bottomLeftRadius;
            mPath.addRoundRect(view.getPaddingLeft(), view.getPaddingTop(), view.getWidth() - view.getPaddingRight(),
                view
                .getHeight() - view.getPaddingBottom(), radii, Path.Direction.CW);

            float halfStroke = strokeWidth / 2;
            strokeRadii[0] = strokeRadii[1] = radii[0] - halfStroke;
            strokeRadii[2] = strokeRadii[3] = radii[2] - halfStroke;
            strokeRadii[4] = strokeRadii[5] = radii[4] - halfStroke;
            strokeRadii[6] = strokeRadii[7] = radii[6] - halfStroke;
            mStrokePath.addRoundRect(view.getPaddingLeft() + halfStroke, view.getPaddingTop() + halfStroke,
                view.getWidth() - view
                .getPaddingRight() - halfStroke, view.getHeight() - view.getPaddingBottom() - halfStroke, strokeRadii
                , Path.Direction.CW);
            mPaint.setStrokeWidth(strokeWidth);
            mPaint.setColor(strokeColor);
        }
        canvas.save();
        canvas.clipPath(mPath, Region.Op.INTERSECT);
    }

    void postDraw(Canvas canvas) {
        canvas.restore();
        if (strokeWidth > 0 && Color.alpha(strokeColor) > 0) {
            canvas.save();
            canvas.drawPath(mStrokePath, mPaint);
            canvas.restore();
        }
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        notifyUpdate();
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        notifyUpdate();
    }

    public void setRadius(float radius) {
        this.radius = radius;
        notifyUpdate();
    }

    public void setBottomRightRadius(float bottomRightRadius) {
        this.bottomRightRadius = bottomRightRadius;
        notifyUpdate();
    }

    public void setTopLeftRadius(float topLeftRadius) {
        this.topLeftRadius = topLeftRadius;
        notifyUpdate();
    }

    public void setTopRightRadius(float topRightRadius) {
        this.topRightRadius = topRightRadius;
        notifyUpdate();
    }

    public void setBottomLeftRadius(float bottomLeftRadius) {
        this.bottomLeftRadius = bottomLeftRadius;
        notifyUpdate();
    }

    private void notifyUpdate() {
        mDirty = true;
        view.postInvalidate();
    }
}

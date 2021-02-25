package cn.sskbskdrin.lib.demo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import cn.sskbskdrin.base.IView;

/**
 * Created by keayuan on 2021/2/20.
 *
 * @author keayuan
 */
public class CubicBezierView extends View implements IView {

    public static final int[] colors = {Color.RED, Color.GRAY, Color.CYAN, Color.GREEN};

    public CubicBezierView(Context context) {
        super(context);
    }

    public CubicBezierView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setTextSize(30);
    }

    private PointF[] points = new PointF[]{new PointF(50, 50), new PointF(50, 100), new PointF(200, 100),
        new PointF(200, 50)};
    private RectF rectF = new RectF();
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private PointF current;
    private Path path = new Path();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        canvas.drawCircle(0, 320, 320, paint);
        canvas.drawCircle(480, 480, 160, paint);
        path.reset();
        path.moveTo(points[0].x, points[0].y);
        path.cubicTo(points[1].x, points[1].y, points[2].x, points[2].y, points[3].x, points[3].y);
        canvas.drawPath(path, paint);

        paint.setStyle(Paint.Style.FILL);
        int bottom = getHeight();
        for (int i = 0; i < 4; i++) {
            paint.setColor(colors[i]);
            canvas.drawCircle(points[i].x, points[i].y, 4, paint);
            canvas.drawText(((int) points[i].x) + "," + ((int) points[i].y), i * 240, bottom - 60, paint);
            canvas.drawRect(i * 240, bottom - 60, i * 240 + 60, bottom, paint);
        }
    }

    float lastX = 0;
    float lastY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                current = touchPoint(x, y);
                lastX = x;
                lastY = y;
                if (current != null) return true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (current != null) {
                    current.offset((x - lastX) / 2, (y - lastY) / 2);
                    postInvalidate();
                }
                break;
        }
        lastX = x;
        lastY = y;
        return super.onTouchEvent(event);
    }

    private PointF touchPoint(float x, float y) {
        rectF.set(x - 30, y - 30, x + 30, y + 30);
        for (PointF point : points) {
            if (rectF.contains(point.x, point.y)) return point;
        }
        int top = getHeight() - 60;
        for (int i = 0; i < 4; i++) {
            if (rectF.contains(i * 240 + 30, top + 30)) return points[i];
        }
        return null;
    }
}

package cn.sskbskdrin.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import cn.sskbskdrin.view.R;

/**
 * Created by keayuan on 2021/2/10.
 *
 * @author keayuan
 */
public class GridLayout extends ViewGroup {
    private static int GRAVITY_DEFAULT = Gravity.LEFT | Gravity.TOP;
    private static int HORIZONTAL = 0;
    private static int VERTICAL = 1;

    private int mOrientation;

    private int columnCount;
    private int gravity;

    private int verticalSpacing;
    private int horizontalSpacing;

    public GridLayout(Context context) {
        this(context, null);
    }

    public GridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GridLayout, defStyleAttr, 0);

        mOrientation = a.getInt(R.styleable.GridLayout_android_orientation, VERTICAL);
        gravity = a.getInt(R.styleable.GridLayout_android_gravity, GRAVITY_DEFAULT);
        columnCount = a.getInt(R.styleable.GridLayout_android_columnCount, 1);
        if (columnCount < 1) {
            columnCount = 1;
        }
        horizontalSpacing = a.getDimensionPixelSize(R.styleable.GridLayout_android_horizontalSpacing, 0);
        verticalSpacing = a.getDimensionPixelSize(R.styleable.GridLayout_android_verticalSpacing, 0);

        int spacing = a.getDimensionPixelSize(R.styleable.GridLayout_android_spacing, 0);
        if (spacing > 0) {
            horizontalSpacing = verticalSpacing = spacing;
        }
        a.recycle();
    }

    private boolean debugDraw = true;
    private Rect[] debugFrame;

    public void debugDraw(boolean draw) {
        debugDraw = draw;
        requestLayout();
    }

    public void setColumnCount(int columnCount) {
        if (columnCount < 1) return;
        this.columnCount = columnCount;
        requestLayout();
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
        requestLayout();
    }

    public void setHorizontalSpacing(int spacing) {
        this.horizontalSpacing = spacing;
        requestLayout();
    }

    public void setVerticalSpacing(int spacing) {
        this.verticalSpacing = spacing;
        requestLayout();
    }

    public void setSpacing(int horizontalSpacing, int verticalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
        requestLayout();
    }

    private int getItemWidth(int gridWidth, int span) {
        return (gridWidth + horizontalSpacing) * span - horizontalSpacing;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int gridWidth =
            (sizeWidth - getPaddingLeft() - getPaddingRight() - (columnCount - 1) * horizontalSpacing) / columnCount;
        int count = getChildCount();
        int currentSpan = 0;
        int heightTotal = getPaddingTop() + getPaddingBottom();
        int lineHeight = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.columnSpan > columnCount) lp.columnSpan = columnCount;
            if (lp.columnSpan < 1) lp.columnSpan = 1;
            if (lp.offsetSpan <= 0) lp.offsetSpan = 0;

            int remainSpan = columnCount - currentSpan;
            lp.realOffsetSpan = 0;
            if (lp.offsetSpan + lp.columnSpan > remainSpan) {
                currentSpan = 0;
                if (lp.offsetSpan >= remainSpan) {
                    int tempOffset = lp.offsetSpan - remainSpan;
                    tempOffset %= columnCount;
                    if (tempOffset + lp.columnSpan > columnCount) {
                        lp.realOffsetSpan = 0;
                    } else {
                        lp.realOffsetSpan = tempOffset;
                    }
                }
                if (lineHeight > 0) {
                    heightTotal += lineHeight + verticalSpacing;
                }
                lineHeight = 0;
            } else {
                lp.realOffsetSpan = lp.offsetSpan;
            }
            int mode = MeasureSpec.EXACTLY;
            int width = getItemWidth(gridWidth, lp.columnSpan) - lp.leftMargin - lp.rightMargin;
            if (lp.width == LayoutParams.WRAP_CONTENT) {
                mode = MeasureSpec.AT_MOST;
            } else if (lp.width >= 0) {
                width = lp.width;
            }
            child.measure(MeasureSpec.makeMeasureSpec(width, mode), MeasureSpec.makeMeasureSpec(heightMeasureSpec,
                MeasureSpec.UNSPECIFIED));
            currentSpan += lp.columnSpan + lp.realOffsetSpan;
            lineHeight = Math.max(lineHeight, child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
        }
        if (lineHeight > 0) {
            heightTotal += lineHeight;
        }
        setMeasuredDimension(MeasureSpec.makeMeasureSpec(sizeWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(heightTotal, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        if (count < 0) return;
        int top = getPaddingTop();

        if (debugDraw) {
            if (debugFrame == null || debugFrame.length != count) {
                debugFrame = new Rect[count];
            }
        }

        int gridWidth =
            (r - l - getPaddingLeft() - getPaddingRight() - (columnCount - 1) * horizontalSpacing) / columnCount;

        int lineHeight = 0;
        int currentColumn = 0;
        int startChild = 0;
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (currentColumn + lp.columnSpan + lp.offsetSpan > columnCount) {
                layoutRow(startChild, i, gridWidth, top, top + lineHeight);
                if (lineHeight > 0) {
                    top += lineHeight + verticalSpacing;
                }
                startChild = i;
                lineHeight = 0;
                currentColumn = 0;
            }
            currentColumn += lp.columnSpan + lp.realOffsetSpan;
            lineHeight = Math.max(lineHeight, child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin);
        }
        if (startChild < count) {
            layoutRow(startChild, count, gridWidth, top, top + lineHeight);
        }
    }

    private void layoutRow(int start, int end, int gridWidth, int top, int bottom) {
        int left = getPaddingLeft();
        while (start < end) {
            View child = getChildAt(start++);
            LayoutParams lp = (LayoutParams) child.getLayoutParams();
            int column = lp.columnSpan;
            if (lp.realOffsetSpan > 0) {
                left += lp.realOffsetSpan * (gridWidth + horizontalSpacing);
            }
            int width = getItemWidth(gridWidth, column);
            layoutChildren(child, left, top, left + width, bottom);
            if (debugDraw) {
                debugFrame[start - 1] = new Rect(left, top, left + width, bottom);
            }
            left += width + horizontalSpacing;
        }
    }

    private Rect rect = new Rect();
    private Rect out = new Rect();

    private void layoutChildren(View child, int left, int top, int right, int bottom) {
        if (child.getVisibility() != GONE) {
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            int gravity = lp.gravity;
            if (gravity == -1) {
                gravity = this.gravity;
            }

            rect.set(left + lp.leftMargin, top + lp.topMargin, right - lp.rightMargin, bottom - lp.bottomMargin);
            Gravity.apply(gravity, width, height, rect, out);
            child.layout(out.left, out.top, out.right, out.bottom);
        }
    }

    Paint paint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (debugDraw) {
            paint.setAntiAlias(true);
            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);
            for (Rect r : debugFrame) {
                if (r != null) {
                    canvas.drawRect(r, paint);
                }
            }
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public static class LayoutParams extends MarginLayoutParams {

        private int columnSpan = 1;
        private int offsetSpan = 0;
        private int realOffsetSpan = 0;
        private int rowSpan = 1;
        private int gravity = -1;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.GridLayout_Layout);
            columnSpan = a.getInt(R.styleable.GridLayout_Layout_android_layout_columnSpan, 1);
            offsetSpan = a.getInt(R.styleable.GridLayout_Layout_android_startOffset, 0);
            gravity = a.getInt(R.styleable.GridLayout_Layout_android_layout_gravity, -1);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }
}

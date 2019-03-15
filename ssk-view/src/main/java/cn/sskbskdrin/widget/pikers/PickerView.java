package cn.sskbskdrin.widget.pikers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Region;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

public class PickerView extends View {
	private static final String TAG = "PickerView";

	private static final int MODE_NONE = 0x01;
	private static final int MODE_MOVE = MODE_NONE << 1;//滑动模式
	private static final int MODE_FLING = MODE_NONE << 2;//惯性模式
	private static final int MODE_SCROLL = MODE_NONE << 3;//调整模式
	private int mFlag = 0;

	private boolean isMeasure = false;

	private final int TEXT_SIZE = 14;
	private double STEP_SIZE = 0;//每弧度需移动的相素  px/r
	private double STEP_RADIAN = 0;// Math.PI / 9;每个Item所占的弧度  r/item
	private double STEP_HALF_RADIAN = 0;//= Math.PI / 9 / 2;  r/item/2
	private double STEP_SIZE_RADIAN = 0;//移动一个Item所需要的相素 px/item

	private float mRadius;
	private int centerY = 0;
	private int centerX = 0;

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker = VelocityTracker.obtain();

	private List<?> mDataList = new ArrayList<>();
	private Paint mPaintText;
	private Paint mPaintCenterText;

	private String mUnitText = "";

	private int mViewHeight;
	private int mViewWidth;

	private int mTopLine;
	private int mBottomLine;

	private int showCount = 9;

	private float mLastDownY;
	private float mLastScrollY;
	private onSelectListener mSelectListener;

	private boolean isCycle = false;

	private Item mHeaderItem = new Item();

	LinearGradient mTopGradient;
	private Paint mPaintGradient;

	public PickerView(Context context) {
		this(context, null);
	}

	public PickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mDataList = new ArrayList<>();
		mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintText.setStyle(Paint.Style.FILL);
		mPaintText.setColor(0xc0888888);

		mPaintCenterText = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaintCenterText.setStyle(Paint.Style.FILL);
		mPaintCenterText.setColor(0xff333333);

		setTextSize(dp2px(TEXT_SIZE));
		mScroller = new Scroller(getContext());
		mPaintGradient = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	public void setDataList(List<?> list) {
		if (list != null) mDataList = list;
	}

	public void setTextColor(int color) {
		mPaintText.setColor(color);
	}

	public void setTextSize(int size) {
		mPaintText.setTextSize(size);
		mPaintCenterText.setTextSize(size + dp2px(3));
	}

	private int dp2px(int dp) {
		return (int) (getContext().getResources().getDisplayMetrics().density * dp + 0.5f);
	}

	public void setSelectTextColor(int color) {
		mPaintCenterText.setColor(color);
	}

	public void setOnSelectListener(onSelectListener listener) {
		mSelectListener = listener;
	}

	public void setShowCount(int count) {
		showCount = count;
		updateItem();
	}

	public int getCurrentSelect() {
		return mHeaderItem.position;
	}

	public void setCurrentSelect(int position) {
		setCurrentSelect(position, true);
	}

	public void setCurrentSelect(int position, boolean smooth) {
		if (!smooth) {
			mHeaderItem.position = getPosition(position);
			updateItem();
		}
		setMode(MODE_FLING);
		int dy = getPosition(position) - getPosition(mHeaderItem.position);
		dy *= -STEP_SIZE_RADIAN;
		mScroller.startScroll(0, 0, 0, dy);
		postInvalidate();
	}

	public void setCycle(boolean cycle) {
		isCycle = cycle;
		updateItem();
	}

	public void setUnitText(String unit) {
		mUnitText = unit;
		requestLayout();
	}

	public void notifyDataSetChange() {
		if (mHeaderItem.position >= getItemCount()) {
			mHeaderItem.position = getItemCount() - 1;
		}
		updateItem();
	}

	private void updateItem() {
		if (!isMeasure) return;

		Item item = mHeaderItem;
		int count = 0;
		while (count++ < showCount) {
			if (item.next == null) {
				item.next = new Item();
				item.next.parent = item;
			}
			item = item.next;
		}
		mHeaderItem.parent = item;
		item.next = mHeaderItem;
		mHeaderItem.refresh(0, mRadius);
		setCache(mHeaderItem.parent, true, (int) Math.ceil(count / 2.0f));
		setCache(mHeaderItem.next, false, (int) Math.floor(count / 2.0f));

		postInvalidate();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		setMode(MODE_NONE);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		float textMaxWidth = measureTextWidth();
		float unitTextWidth = mPaintCenterText.measureText(mUnitText);

		if (widthMode == MeasureSpec.EXACTLY) {
			mViewWidth = widthSize;
			centerX = mViewWidth / 2;
		} else if (widthMode == MeasureSpec.AT_MOST) {
			mViewWidth = (int) (textMaxWidth + unitTextWidth);
			centerX = (int) (textMaxWidth / 2);
		}

		if (heightMode == MeasureSpec.EXACTLY) {
			mViewHeight = heightSize;
		} else if (heightMode == MeasureSpec.AT_MOST) {
			mViewHeight = (int) (showCount * mPaintText.getTextSize());
		}

		centerY = mViewHeight / 2;
		if (mViewWidth == textMaxWidth + unitTextWidth) {
			centerX = (int) (textMaxWidth / 2);
		} else {
			centerX = mViewWidth / 2;
		}

		mRadius = centerY;

		STEP_SIZE = (float) (mViewHeight * 1.2f / Math.PI);
		STEP_RADIAN = Math.PI / showCount;
		STEP_HALF_RADIAN = STEP_RADIAN / 2;
		STEP_SIZE_RADIAN = STEP_RADIAN * STEP_SIZE;

		mTopLine = (int) ((Math.sin(-STEP_HALF_RADIAN) + 1) * mRadius);
		mBottomLine = (int) ((Math.sin(STEP_HALF_RADIAN) + 1) * mRadius);

		// 按照View的高度计算字体大小
		isMeasure = true;
		updateItem();
		setMeasuredDimension(mViewWidth, mViewHeight);
	}

	private float measureTextWidth() {
		float maxWidth = 0;
		for (int i = 0; i < getItemCount(); i++) {
			float width = mPaintCenterText.measureText(getItem(i));
			if (width > maxWidth) {
				maxWidth = width;
			}
		}
		return maxWidth;
	}

	@Override
	protected final void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mTopGradient == null) {
			mTopGradient = new LinearGradient(0, 0, 0, mTopLine, 0xffffffff, 0x80ffffff, Shader.TileMode
					.CLAMP);
			mPaintGradient.setShader(mTopGradient);
		}
		canvas.drawLine(0, mTopLine, mViewWidth, mTopLine, mPaintText);
		canvas.drawLine(0, mBottomLine, mViewWidth, mBottomLine, mPaintText);
		Item item = mHeaderItem;
		for (int i = 0; i <= showCount; i++) {
			if (item.isDraw()) {
				boolean isContain = item.isContain(mTopLine, mBottomLine);
				canvas.save();
				canvas.clipRect(0, item.top, mViewWidth, item.bottom);
				if (isContain) {
					canvas.clipRect(0, mTopLine - 1, mViewWidth, mBottomLine + 1, Region.Op.DIFFERENCE);
				}
				canvas.scale(1.0F, (float) item.scale);
				DrawTextUtil.drawText(canvas, getItem(item.position), centerX, item.getCenterLine(), DrawTextUtil
						.AlignMode.CENTER, mPaintText);
				canvas.restore();
				if (isContain) {
					canvas.save();
					canvas.clipRect(0, mTopLine - 1, mViewWidth, mBottomLine + 1);
					canvas.clipRect(0, item.top, mViewWidth, item.bottom);
					canvas.scale(1.0F, (float) item.scale);
					DrawTextUtil.drawText(canvas, getItem(item.position), centerX, item.getCenterLine(), DrawTextUtil
							.AlignMode.CENTER, mPaintCenterText);
					canvas.restore();
				}
			}
			item = item.next;
		}
		DrawTextUtil.drawText(canvas, mUnitText, mViewWidth, centerY, DrawTextUtil.AlignMode.RIGHT_CENTER,
				mPaintCenterText);
//		canvas.drawRect(0, 0, mViewWidth, mTopLine, mPaintGradient);
	}

	@Override
	public final boolean onTouchEvent(MotionEvent event) {
		mVelocityTracker.addMovement(event);
		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				setMode(MODE_MOVE);
				mLastScrollY = 0;
				mLastDownY = event.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				computeOffset(event.getY() - mLastDownY);
				mLastDownY = event.getY();
				break;
			case MotionEvent.ACTION_UP:
			default:
				mLastDownY = 0;
				mVelocityTracker.computeCurrentVelocity(500);
				int velocity = (int) mVelocityTracker.getYVelocity();
				if (Math.abs(velocity) > 30) {
					fling(velocity);
				} else {
					reviseOffset();
				}
				break;
		}
		return true;
	}

	@Override
	public final void computeScroll() {
		if (!isMove()) {
			if (mScroller.computeScrollOffset()) {
				computeOffset(mScroller.getCurrY() - mLastScrollY);
				mLastScrollY = mScroller.getCurrY();
			} else {
				mLastScrollY = 0;
				if (isFling()) {
					reviseOffset();
				} else if (isScroll()) {
					performSelect(0, mHeaderItem.position, true);
					setMode(MODE_NONE);
				}
			}
		}
	}

	/**
	 * 调整位置,使选择项处于中间位置
	 */
	private void reviseOffset() {
		setMode(MODE_SCROLL);
		int de = (int) (((float) Math.sin(mHeaderItem.radian)) * STEP_SIZE);
		mScroller.startScroll(0, 0, 0, -de);
		invalidate();
	}

	/**
	 * 计算增加每个Item的偏移量
	 *
	 * @param delta 变化量大小
	 */
	private void computeOffset(double delta) {
		boolean isDown = delta > 0;
		// 如果不循环
		if (!isCycle && delta != 0) {
			if (isDown) {
				double maxDelta = mHeaderItem.position * STEP_SIZE_RADIAN + (STEP_HALF_RADIAN - mHeaderItem.radian) *
						STEP_SIZE;
				if (delta > maxDelta) {
					delta = maxDelta;
					mScroller.abortAnimation();
				}
			} else {
				double maxDelta = (mHeaderItem.position - getItemCount()) * STEP_SIZE_RADIAN + (STEP_HALF_RADIAN -
						mHeaderItem.radian) * STEP_SIZE;
				if (delta < maxDelta) {
					delta = maxDelta;
					mScroller.abortAnimation();
				}
			}
		}

		// 如果变化量等0,直接返回
		if (delta == 0) {
			postInvalidate();
			return;
		}

		int deltaCount = (int) (delta / STEP_SIZE_RADIAN);
		delta = delta % STEP_SIZE_RADIAN;
		double deltaRadian = delta / STEP_SIZE;
		int oldPosition = mHeaderItem.position;
		int tempHeadPos = getPosition(mHeaderItem.position - deltaCount);
		mHeaderItem.refresh(mHeaderItem.radian + deltaRadian, mRadius);
		if (!mHeaderItem.isContain(centerY)) {
			if (isDown) {
				mHeaderItem.parent.refresh(mHeaderItem.radian - STEP_RADIAN, mRadius);
				mHeaderItem = mHeaderItem.parent;
				tempHeadPos = getPosition(tempHeadPos - 1);
			} else {
				mHeaderItem.next.refresh(mHeaderItem.radian + STEP_RADIAN, mRadius);
				mHeaderItem = mHeaderItem.next;
				tempHeadPos = getPosition(tempHeadPos + 1);
			}
		}
		mHeaderItem.position = tempHeadPos;

		setCache(mHeaderItem.parent, true, showCount / 2);
		setCache(mHeaderItem.next, false, showCount / 2);

		int newPosition = oldPosition;
		while (newPosition != mHeaderItem.position) {
			newPosition = getPosition(isDown ? oldPosition - 1 : oldPosition + 1);
			performSelect(oldPosition, newPosition, false);
			oldPosition = newPosition;
		}
		postInvalidate();
	}

	private void setCache(Item item, boolean down, int time) {
		Item temp = item;
		Item parent = temp.parent;
		Item next = temp.next;
		if (down) {
			temp.position = getPosition(next.position - 1);
			temp.refresh(next.radian - STEP_RADIAN, mRadius);
			temp = parent;
		} else {
			temp.position = getPosition(parent.position + 1);
			temp.refresh(parent.radian + STEP_RADIAN, mRadius);
			temp = next;
		}
		if (--time > 0) {
			setCache(temp, down, time);
		}
	}

	protected String getItem(int position) {
		String result = "";
		if (position < 0 || position >= getItemCount()) {
			return result;
		}
		if (mDataList.get(position) instanceof IPickerViewData)
			result = ((IPickerViewData) mDataList.get(position)).getPickerViewText();
		else result = mDataList.get(position).toString();
		return result;
	}

	protected int getPosition(int position) {
		if (!isCycle) {
			if (position < 0 || position >= getItemCount()) return -2;
		}
		if (getItemCount() == 0) return 0;
		return (position + getItemCount()) % getItemCount();
	}

	protected int getItemCount() {
		return mDataList.size();
	}

	private void fling(int velocityY) {
		setMode(MODE_FLING);
		mScroller.fling(0, 0, 0, velocityY, 0, 0, -800, 800);
		postInvalidate();
	}

	private boolean isFling() {
		return (mFlag & MODE_FLING) > 0;
	}

	private boolean isMove() {
		return (mFlag & MODE_MOVE) > 0;
	}

	private boolean isScroll() {
		return (mFlag & MODE_SCROLL) > 0;
	}

	private void setMode(int mode) {
		mFlag = mode;
	}

	/**
	 * @param oldPosition 滚出的位置
	 * @param newPosition 滚进的位置
	 * @param isSelect    是否选择
	 */
	private void performSelect(int oldPosition, int newPosition, boolean isSelect) {
		if (mSelectListener != null) {
			if (isSelect) {
				mSelectListener.onSelect(this, newPosition);
			} else {
				mSelectListener.onTicker(this, oldPosition, newPosition);
			}
		}
	}

	public interface IPickerViewData {
		String getPickerViewText();
	}

	public interface onSelectListener {
		void onSelect(PickerView view, int position);

		void onTicker(PickerView view, int oldPosition, int newPosition);
	}

	public static class SimpleOnSelectListener implements onSelectListener {

		@Override
		public void onSelect(PickerView view, int position) {
		}

		@Override
		public void onTicker(PickerView view, int oldPosition, int newPosition) {
		}
	}

	private class Item {
		private static final double HALF_PI = Math.PI / 2;
		private double radian = Math.PI;//当前的弧度
		int position;
		Item next;//后一个Item
		Item parent;//前一个Item

		private double scale = 0;

		float top = 0;
		float bottom = 0;

		Item() {
		}

		/**
		 * 更新Item所在的弧度值
		 *
		 * @param radian 新的弧度值
		 */
		void refresh(double radian, float mRadius) {
			this.radian = radian;
			scale = Math.cos(radian);
			top = (float) (Math.sin(radian - STEP_HALF_RADIAN) + 1) * mRadius;
			bottom = (float) (Math.sin(radian + STEP_HALF_RADIAN) + 1) * mRadius;
		}

		/**
		 * 两条线的范围与Item是否有交叉
		 *
		 * @param t 上面一条线的值
		 * @param b 下面一条线的值
		 * @return true则有交叉
		 */
		boolean isContain(float t, float b) {
			int bottom = (int) Math.ceil(this.bottom);
			int top = (int) Math.floor(this.top);
			return (t >= top && t <= bottom) || (b >= top && b <= bottom);
		}

		boolean isContain(float y) {
			int bottom = (int) Math.ceil(this.bottom);
			int top = (int) Math.floor(this.top);
			return y >= top && y <= bottom;
		}

		/**
		 * 获取Item的中间位置
		 *
		 * @return 中间位置
		 */
		float getCenterLine() {
			return (float) ((bottom + top) / 2 / scale);
		}

		/**
		 * 是否显示出来
		 */
		boolean isDraw() {
			return Math.abs(radian) < HALF_PI;
		}
	}
}

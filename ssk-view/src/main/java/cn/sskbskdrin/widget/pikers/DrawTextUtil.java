package cn.sskbskdrin.widget.pikers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * Created by ex-keayuan001 on 2017/10/31.
 *
 * @author ex-keayuan001
 */
public class DrawTextUtil {

	public enum AlignMode {
		LEFT_TOP, LEFT_CENTER, LEFT_BOTTOM, TOP_CENTER, RIGHT_TOP, RIGHT_CENTER, RIGHT_BOTTOM, BOTTOM_CENTER, CENTER
	}

	public static void drawText(Canvas canvas, String text, float x, float y, AlignMode mode, Paint paint) {
		Rect r = new Rect();
		paint.getTextBounds(text, 0, text.length(), r);
		int height = r.height();
		y -= r.bottom;
		switch (mode) {
			case LEFT_TOP:
				paint.setTextAlign(Paint.Align.LEFT);
				y += height;
				break;
			case LEFT_CENTER:
				paint.setTextAlign(Paint.Align.LEFT);
				y += height / 2;
				break;
			case LEFT_BOTTOM:
				paint.setTextAlign(Paint.Align.LEFT);
				break;
			case TOP_CENTER:
				paint.setTextAlign(Paint.Align.CENTER);
				y += height;
				break;
			case RIGHT_TOP:
				paint.setTextAlign(Paint.Align.RIGHT);
				y += height;
				break;
			case RIGHT_CENTER:
				paint.setTextAlign(Paint.Align.RIGHT);
				y += height / 2;
				break;
			case RIGHT_BOTTOM:
				paint.setTextAlign(Paint.Align.RIGHT);
				break;
			case BOTTOM_CENTER:
				paint.setTextAlign(Paint.Align.CENTER);
				break;
			case CENTER:
				paint.setTextAlign(Paint.Align.CENTER);
				y += height / 2;
				break;
		}
		canvas.drawText(text, x, y, paint);
	}
}

package ds.photosight.ui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import com.androidquery.util.AQUtility;
import ds.photosight.R;
import ds.photosight.utils.L;

public class Histogram extends View {

	private int[] data;
	private Bitmap bitmap;
	private Bitmap result;
	private Paint paint;
	private ValueAnimator mAnimator;
	private int alpha = 0;


	public Histogram(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.GRAY);
		paint.setTextSize(AQUtility.dip2pixel(context, 14));

		fillDemo();
	}


	private void fillDemo() {


	}


	public void setData(int[] data) {
		this.data = data;
	}


	public void setBitmap(Bitmap b) {
		bitmap = b;
		invalidate();
	}


	@Override
	protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		L.v(String.format("w=%s h=%s", w, h));
		if (bitmap != null)
			process();
	}


	@Override
	protected void onDraw(final Canvas canvas) {
		int w = getWidth();
		int h = getHeight();

		if (bitmap != null) {
			if (result == null) {
				// process
				process();
			} else {
				// draw
				if (mAnimator == null && alpha == 0)
					startAnimator();

				paint.setAlpha(alpha);
				canvas.drawBitmap(result, 0, 0, paint);
			}

		}

		String text = getContext().getString(R.string.processing);
		paint.setAlpha(255 - alpha);
		canvas.drawText(text, (w - paint.measureText(text)) / 2, h / 2, paint);

	}


	private void startAnimator() {
		mAnimator = ValueAnimator.ofInt(0, 255);
		mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				alpha = (int) animation.getAnimatedValue();
				postInvalidate();
			}
		});
		mAnimator.start();

	}


	public void process() {
		AQUtility.postAsync(new Runnable() {
			@Override
			public void run() {

				L.v("process...");
				int[] r = new int[256];
				int[] g = new int[256];
				int[] b = new int[256];
				final int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
				bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

				for (int p : pixels) {
					r[Color.red(p)]++;
					g[Color.green(p)]++;
					b[Color.blue(p)]++;
				}

				int max = Math.max(Math.max(max(r), max(g)), max(b));
				int height = getHeight();
				int width = getWidth();
				float minFactor = height / 12000f;
				float xFactor = width / 256f;
				float yFactor = Math.max((float) height / max, minFactor);
				L.v("xFactor=" + xFactor + " yFactor=" + yFactor);

				Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
				paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
				Path pathR = new Path();
				Path pathG = new Path();
				Path pathB = new Path();
				result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
				Canvas c = new Canvas(result);

				pathR.moveTo(0, height);
				pathG.moveTo(0, height);
				pathB.moveTo(0, height);

				for (int i = 0; i < 255; i++) {

					float x1 = i * xFactor;
					float y1 = height - r[i] * yFactor;
					float x2 = (i + 1) * xFactor;
					float y2 = height - r[i + 1] * yFactor;

					pathR.quadTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2);

					y1 = height - g[i] * yFactor;
					y2 = height - g[i + 1] * yFactor;
					pathG.quadTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2);

					y1 = height - b[i] * yFactor;
					y2 = height - b[i + 1] * yFactor;
					pathB.quadTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2);

				}
				pathR.lineTo(width, height);
				pathG.lineTo(width, height);
				pathB.lineTo(width, height);
				paint.setColor(Color.RED);
				c.drawPath(pathR, paint);
				paint.setColor(Color.GREEN);
				c.drawPath(pathG, paint);
				paint.setColor(Color.BLUE);
				c.drawPath(pathB, paint);

				postInvalidate();
			}
		});
	}


	private static int max(int[] values) {
		int max = Integer.MIN_VALUE;
		for (int value : values) {
			if (value > max)
				max = value;
		}
		return max;
	}

}

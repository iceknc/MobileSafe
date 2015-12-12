package org.itheima.mobilesafe.activity;

import org.itheima.mobilesafe.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

public abstract class SjfdBaseSetupActivity extends Activity {
	private GestureDetector mDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 初始化手势识别器
		mDetector = new GestureDetector(this, new SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				float x1 = e1.getX();
				float x2 = e2.getX();
				float y1 = e1.getY();
				float y2 = e2.getY();

				// x方向要达到一定的速率才认为是水平移动
				if (Math.abs(velocityX) > 200) {
					if (Math.abs(x1 - x2) > Math.abs(y1 - y2)) {
						if (x1 > x2) {
							doNext();
						} else if (x2 > x1) {
							doPre();
						}
					}
				}
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	public void clickPre(View view) {
		doPre();
	}

	public void clickNext(View view) {
		doNext();
	}

	private void doNext() {
		if (performNext()) {
			return;
		}
		overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
		finish();
	}

	protected void doPre() {
		if (performPre()) {
			return;
		}
		overridePendingTransition(R.anim.pre_enter, R.anim.pre_exit);
		finish();
	}

	protected abstract boolean performPre();

	protected abstract boolean performNext();
}

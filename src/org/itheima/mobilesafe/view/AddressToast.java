package org.itheima.mobilesafe.view;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.utils.Constants;
import org.itheima.mobilesafe.utils.PreferenceUtils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class AddressToast implements OnTouchListener {
	private WindowManager mWM;
	private View mView;
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private Context mContext;
	private float startX;
	private float startY;

	public AddressToast(Context context) {
		mContext = context;
		mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		final WindowManager.LayoutParams params = mParams;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
		// | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		// params.windowAnimations =
		// com.android.internal.R.style.Animation_Toast;
		// params.type = WindowManager.LayoutParams.TYPE_TOAST;// toast默认是不可以触摸
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;// 可以触摸,并且可以显示在拨打电话的界面上
		params.setTitle("Toast");

		Log.d("AddressToast", mParams + "  " + params);
	}

	/**
	 * 显示号码归属地信息
	 * 
	 * @param address
	 *            归属地
	 */
	public void show(String address) {
		if (mView == null) {
			mView = View.inflate(mContext, R.layout.toast_address, null);
			// 设置样式
			int style = PreferenceUtils.getInt(mContext,
					Constants.STYLE_ADDRESS, R.drawable.toast_normal);
			mView.setBackgroundResource(style);
			// 设置触摸监听
			mView.setOnTouchListener(this);
		}
		TextView tvAddress = (TextView) mView
				.findViewById(R.id.toast_tv_address);
		tvAddress.setText(address);

		mWM.addView(mView, mParams);

	}

	/**
	 * 隐藏号码归属地
	 */
	public void hide() {
		if (mView != null) {
			if (mView.getParent() != null) {
				mWM.removeView(mView);
			}
			mView = null;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			startX = event.getX();
			startY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			float moveX = event.getX();
			float moveY = event.getY();

			Log.d("abc", mParams.x + "  " + mParams.y);
			Log.d("abc", event.getX() + " e " + event.getY());

			mParams.x += (moveX - startX);
			mParams.y += (moveY - startY);

			mWM.updateViewLayout(mView, mParams);

			startX = moveX;
			startY = moveY;
			break;
		case MotionEvent.ACTION_UP:
			break;
		default:
			break;
		}

		return false;
	}
}

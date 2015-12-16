package org.itheima.mobilesafe.view;

import org.itheima.mobilesafe.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class SegementView extends LinearLayout implements OnClickListener {
	private TextView mTvLeft;
	private TextView mTvRight;

	private static final int SELECTED_LEFT = 0;
	private static final int SELECTED_RIGHT = 1;

	private boolean isLeftSelected = true;

	private OnSegementListener mListener;

	public SegementView(Context context) {
		super(context, null);
	}

	public SegementView(Context context, AttributeSet attrs) {
		super(context, attrs);

		View.inflate(context, R.layout.view_segement, this);

		// 读取属性
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.SegementView);

		String leftText = ta.getString(R.styleable.SegementView_svLeft);
		String rightText = ta.getString(R.styleable.SegementView_svRight);
		int selected = ta.getInt(R.styleable.SegementView_svSelected,
				SELECTED_LEFT);

		ta.recycle();

		mTvLeft = (TextView) findViewById(R.id.vs_tv_left);
		mTvRight = (TextView) findViewById(R.id.vs_tv_right);

		mTvLeft.setText(leftText);
		mTvRight.setText(rightText);

		switch (selected) {
		case SELECTED_LEFT:
			mTvLeft.setSelected(true);
			mTvRight.setSelected(false);

			isLeftSelected = true;
			break;
		case SELECTED_RIGHT:
			mTvLeft.setSelected(false);
			mTvRight.setSelected(true);

			isLeftSelected = false;
			break;
		default:
			break;
		}

		// 设置点击事件
		mTvLeft.setOnClickListener(this);
		mTvRight.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (v == mTvLeft) {
			if (!isLeftSelected) {
				// 如果点击的是左侧的view
				// 左侧的选中
				mTvLeft.setSelected(true);
				// 右侧的没有选中
				mTvRight.setSelected(false);

				isLeftSelected = true;

				// 左侧的选中
				// mTvTmp.setText("左侧选中");
				if (mListener != null) {
					mListener.onSelected(true);
				}
			}

		} else if (v == mTvRight) {
			if (isLeftSelected) {
				// 点击的是右侧的view
				// 左侧的没有选中
				mTvLeft.setSelected(false);
				// 右侧的选中
				mTvRight.setSelected(true);

				isLeftSelected = false;

				// 右侧的选中
				// mTvTmp.setText("右侧选中");
				if (mListener != null) {
					mListener.onSelected(false);
				}
			}
		}

	}

	public void setOnSegementListener(OnSegementListener listener) {
		this.mListener = listener;
	}

	public interface OnSegementListener {
		/**
		 * 当选中时的回调
		 * 
		 * @param left
		 *            为true时 ，就是左侧选中，否则右侧选中
		 */
		void onSelected(boolean leftSelected);
	}
}

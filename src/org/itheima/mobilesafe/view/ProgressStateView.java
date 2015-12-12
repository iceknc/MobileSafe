package org.itheima.mobilesafe.view;

import org.itheima.mobilesafe.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressStateView extends LinearLayout {
	private ProgressBar mProgressBar;
	private TextView mTvTitle;
	private TextView mTvLeft;
	private TextView mTvRight;

	public ProgressStateView(Context context) {
		super(context, null);
	}

	public ProgressStateView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 挂在xml
		View.inflate(context, R.layout.view_progress_state, this);

		// 读取自定义属性
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.ProgressStateView);
		String title = ta.getString(R.styleable.ProgressStateView_psvTitle);

		ta.recycle();

		// 初始化view
		mTvTitle = (TextView) findViewById(R.id.view_psv_tv_title);
		mTvLeft = (TextView) findViewById(R.id.view_psv_tv_left);
		mTvRight = (TextView) findViewById(R.id.view_psv_tv_right);
		mProgressBar = (ProgressBar) findViewById(R.id.view_psv_progress);

		mTvTitle.setText(title);

	}

	public void setTextLeft(CharSequence text) {
		mTvLeft.setText(text);
	}

	public void setTextRight(CharSequence text) {
		mTvRight.setText(text);
	}

	public void setProgress(int progress) {
		mProgressBar.setProgress(progress);
	}

	public void setMax(int max) {
		mProgressBar.setMax(max);
	}

}

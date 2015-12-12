package org.itheima.mobilesafe.view;

import org.itheima.mobilesafe.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingItemTextView extends RelativeLayout {
	private final static int BACKGROUND_START = 0;
	private final static int BACKGROUND_MIDDLE = 1;
	private final static int BACKGROUND_END = 2;

	private TextView mTvText;
	private ImageView mIvToggle;

	public SettingItemTextView(Context context) {
		super(context, null);
	}

	public SettingItemTextView(Context context, AttributeSet set) {
		super(context, set);

		// 将xml和class建立关系，挂载布局
		View.inflate(context, R.layout.view_setting_item, this);

		// 将自定义控件的属性集合读取出来展示
		TypedArray ta = context.obtainStyledAttributes(set,
				R.styleable.SettingItemView);
		// 取出属性值
		String text = ta.getString(R.styleable.SettingItemView_sivText);
		int background = ta.getInt(R.styleable.SettingItemView_sivBackground,
				BACKGROUND_START);
		boolean toggleEnable = ta.getBoolean(
				R.styleable.SettingItemView_sivToggleEnable, true);

		// 回收
		ta.recycle();

		// 给view设置数据
		mTvText = (TextView) findViewById(R.id.view_setting_item_tv_text);
		mIvToggle = (ImageView) findViewById(R.id.view_setting_item_iv_toggle);
		mTvText.setText(text);

		// 设置item的背景
		switch (background) {
		case BACKGROUND_START:
			setBackgroundResource(R.drawable.setting_first_selector);
			break;
		case BACKGROUND_MIDDLE:
			setBackgroundResource(R.drawable.setting_middle_selector);
			break;
		case BACKGROUND_END:
			setBackgroundResource(R.drawable.setting_end_selector);
			break;
		default:
			break;
		}

		mIvToggle.setVisibility(toggleEnable ? View.VISIBLE : View.GONE);
	}

	/**
	 * 设置开关显示的图片
	 * 
	 * @param opened
	 *            开关状态
	 */
	public void setToggle(boolean opened) {
		mIvToggle.setImageResource(opened ? R.drawable.on : R.drawable.off);
	}

}

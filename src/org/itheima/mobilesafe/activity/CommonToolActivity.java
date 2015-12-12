package org.itheima.mobilesafe.activity;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.view.SettingItemTextView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class CommonToolActivity extends Activity {
	private SettingItemTextView mSivNumberAddress;
	private SettingItemTextView mSivCommonNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_common_tool);

		initView();
		initEvent();
	}

	private void initView() {
		mSivNumberAddress = (SettingItemTextView) findViewById(R.id.ct_siv_numberaddress);
		mSivCommonNumber = (SettingItemTextView) findViewById(R.id.ct_siv_commonnumber);
	}

	private void initEvent() {
		//号码归属地点击事件
		mSivNumberAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CommonToolActivity.this,
						NumberAddressActivity.class);
				startActivity(intent);
			}
		});
		//常用号码点击事件
		mSivCommonNumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CommonToolActivity.this,
						CommonNumberActivity.class);
				startActivity(intent);
				
			}
		});
	}
}

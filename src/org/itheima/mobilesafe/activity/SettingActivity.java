package org.itheima.mobilesafe.activity;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.service.CallSmsSafeService;
import org.itheima.mobilesafe.service.NumberAddressService;
import org.itheima.mobilesafe.utils.Constants;
import org.itheima.mobilesafe.utils.PreferenceUtils;
import org.itheima.mobilesafe.utils.ServiceStateUtils;
import org.itheima.mobilesafe.view.AddressDialog;
import org.itheima.mobilesafe.view.SettingItemTextView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class SettingActivity extends Activity {
	private SettingItemTextView mSivAutoUpdate;
	private SettingItemTextView mSivCallSmsSafe;
	private SettingItemTextView mSivNumberAddress;
	private SettingItemTextView mSivNumberAddressStyle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_setting);

		// 初始化view
		initView();
		// 设置事件
		initEvent();
	}

	@Override
	protected void onStart() {
		super.onStart();

		// 回显骚扰拦截的服务状态
		mSivCallSmsSafe.setToggle(ServiceStateUtils.isRunning(this,
				CallSmsSafeService.class));

		// 回显号码归属地服务
		mSivNumberAddress.setToggle(ServiceStateUtils.isRunning(this,
				NumberAddressService.class));
	}

	private void initView() {
		mSivAutoUpdate = (SettingItemTextView) findViewById(R.id.setting_siv_autoupdate);
		mSivCallSmsSafe = (SettingItemTextView) findViewById(R.id.setting_siv_callsmssafe);
		mSivNumberAddress = (SettingItemTextView) findViewById(R.id.setting_siv_numberaddress);
		mSivNumberAddressStyle = (SettingItemTextView) findViewById(R.id.setting_siv_numberaddressstyle);

		// 回显自动更新
		mSivAutoUpdate.setToggle(PreferenceUtils.getBoolean(
				SettingActivity.this, Constants.SETTING_AUTO_UPDATE, true));
	}

	private void initEvent() {
		// 自动更新开关
		mSivAutoUpdate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 如果开关是打开的就关闭
				boolean flag = PreferenceUtils.getBoolean(SettingActivity.this,
						Constants.SETTING_AUTO_UPDATE, true);
				if (flag) {
					// 显示关闭的图片
					mSivAutoUpdate.setToggle(false);
					// 存储配置
					PreferenceUtils.putBoolean(SettingActivity.this,
							Constants.SETTING_AUTO_UPDATE, !flag);
				} else {
					// 显示打开的图片
					mSivAutoUpdate.setToggle(true);
					// 存储配置
					PreferenceUtils.putBoolean(SettingActivity.this,
							Constants.SETTING_AUTO_UPDATE, !flag);
				}
			}
		});

		// 骚扰拦截开关
		mSivCallSmsSafe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isRunning = ServiceStateUtils.isRunning(
						SettingActivity.this, CallSmsSafeService.class);

				if (isRunning) {
					// 关闭服务
					stopService(new Intent(SettingActivity.this,
							CallSmsSafeService.class));
					mSivCallSmsSafe.setToggle(!isRunning);
				} else {
					// 服务开启
					startService(new Intent(SettingActivity.this,
							CallSmsSafeService.class));
					mSivCallSmsSafe.setToggle(!isRunning);
				}
			}
		});

		// 号码归属地显示开关
		mSivNumberAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isRunning = ServiceStateUtils.isRunning(
						SettingActivity.this, NumberAddressService.class);
				if (isRunning) {
					stopService(new Intent(SettingActivity.this,
							NumberAddressService.class));
					mSivNumberAddress.setToggle(!isRunning);
				} else {
					startService(new Intent(SettingActivity.this,
							NumberAddressService.class));
					mSivNumberAddress.setToggle(!isRunning);
				}
			}
		});

		// 归属地样式点击事件
		mSivNumberAddressStyle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddressDialog dialog = new AddressDialog(SettingActivity.this);
				dialog.show();
			}
		});
	}

}

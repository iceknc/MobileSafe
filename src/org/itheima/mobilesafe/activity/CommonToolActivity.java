package org.itheima.mobilesafe.activity;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.service.AppLockService1;
import org.itheima.mobilesafe.service.AppLockService2;
import org.itheima.mobilesafe.utils.ServiceStateUtils;
import org.itheima.mobilesafe.utils.SmsUtils;
import org.itheima.mobilesafe.utils.SmsUtils.OnSmsBackupListener;
import org.itheima.mobilesafe.utils.SmsUtils.OnSmsRestoreListener;
import org.itheima.mobilesafe.view.SettingItemTextView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

public class CommonToolActivity extends Activity {
	private SettingItemTextView mSivNumberAddress;
	private SettingItemTextView mSivCommonNumber;
	private SettingItemTextView mSivSmsBackup;
	private SettingItemTextView mSivSmsRestore;
	private SettingItemTextView mSivAppLock;
	private SettingItemTextView mSivLockService1;
	private SettingItemTextView mSivLockService2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_common_tool);

		initView();
		initEvent();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 回显开关状态
		mSivLockService1.setToggle(ServiceStateUtils.isRunning(this,
				AppLockService1.class));
		mSivLockService2.setToggle(ServiceStateUtils.isRunning(this,
				AppLockService2.class));
	}

	private void initView() {
		mSivNumberAddress = (SettingItemTextView) findViewById(R.id.ct_siv_numberaddress);
		mSivCommonNumber = (SettingItemTextView) findViewById(R.id.ct_siv_commonnumber);
		mSivSmsBackup = (SettingItemTextView) findViewById(R.id.ct_siv_sms_bakcup);
		mSivSmsRestore = (SettingItemTextView) findViewById(R.id.ct_siv_sms_restore);
		mSivAppLock = (SettingItemTextView) findViewById(R.id.ct_siv_applock);
		mSivLockService1 = (SettingItemTextView) findViewById(R.id.ct_siv_lockservice1);
		mSivLockService2 = (SettingItemTextView) findViewById(R.id.ct_siv_lockservice2);
	}

	private void initEvent() {
		// 号码归属地点击事件
		mSivNumberAddress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CommonToolActivity.this,
						NumberAddressActivity.class);
				startActivity(intent);
			}
		});
		// 常用号码点击事件
		mSivCommonNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CommonToolActivity.this,
						CommonNumberActivity.class);
				startActivity(intent);

			}
		});
		// 短信备份点击事件
		mSivSmsBackup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				smsBackup();
			}
		});
		// 短信还原点击事件
		mSivSmsRestore.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				smsRestore();
			}
		});
		// 程序锁管理点击事件
		mSivAppLock.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CommonToolActivity.this,
						AppLockActivity.class);
				startActivity(intent);
			}
		});
		// 程序锁服务1点击事件
		mSivLockService1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CommonToolActivity.this,
						AppLockService1.class);

				boolean running = ServiceStateUtils.isRunning(
						CommonToolActivity.this, AppLockService1.class);
				if (running) {
					stopService(intent);
					mSivLockService1.setToggle(false);
				} else {
					startService(intent);
					mSivLockService1.setToggle(true);
				}
			}
		});
		// 程序服务2点击事件
		mSivLockService2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setAction("android.settings.ACCESSIBILITY_SETTINGS");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.addCategory("android.intent.category.VOICE_LAUNCH");

				startActivity(intent);
			}
		});
	}

	private void smsBackup() {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		SmsUtils.smsBackup(this, new OnSmsBackupListener() {

			@Override
			public void onPreExecute() {
				dialog.show();
			}

			@Override
			public void onProgress(Integer max, Integer progress) {
				dialog.setMax(max);
				dialog.setProgress(progress);
			}

			@Override
			public void onError(Exception result) {
				dialog.dismiss();
				Toast.makeText(CommonToolActivity.this, "备份失败",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSeccuss() {
				dialog.dismiss();
				Toast.makeText(CommonToolActivity.this, "备份成功",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void smsRestore() {
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		SmsUtils.smsRestore(this, new OnSmsRestoreListener() {
			@Override
			public void onPreExecute() {
				dialog.show();
			}

			@Override
			public void onProgress(Integer max, Integer progress) {
				dialog.setMax(max);
				dialog.setProgress(progress);
			}

			@Override
			public void onSeccuss() {
				Toast.makeText(CommonToolActivity.this, "还原成功",
						Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}

			@Override
			public void onError(Exception result) {
				if (result instanceof FileNotFoundException) {
					Toast.makeText(CommonToolActivity.this, "未发现任何的备份文件",
							Toast.LENGTH_SHORT).show();
				} else if (result instanceof IOException) {
					Toast.makeText(CommonToolActivity.this, "备份文件异常",
							Toast.LENGTH_SHORT).show();
				}
				dialog.dismiss();
			}
		});
	}
}
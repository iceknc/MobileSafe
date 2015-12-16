package org.itheima.mobilesafe.activity;

import org.itheima.mobilesafe.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LockActivity extends Activity {
	public static final String KEY_PKG = "key_pkg";

	private ImageView mIvIcon;
	private TextView mTvName;
	private EditText mEtPwd;
	private Button mBtnOk;
	private String mPackageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock);

		initView();
		initData();
		initEvent();
	}

	private void initView() {
		mIvIcon = (ImageView) findViewById(R.id.lock_iv_icon);
		mTvName = (TextView) findViewById(R.id.lock_tv_name);
		mEtPwd = (EditText) findViewById(R.id.lock_et_pwd);
		mBtnOk = (Button) findViewById(R.id.lock_btn_ok);
	}

	private void initData() {
		mPackageName = getIntent().getStringExtra(KEY_PKG);

		PackageManager pm = getPackageManager();
		ApplicationInfo info = null;
		try {
			info = pm.getApplicationInfo(mPackageName, 0);
			mTvName.setText(info.loadLabel(pm));
			mIvIcon.setImageDrawable(info.loadIcon(pm));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initEvent() {
		mBtnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String pwd = mEtPwd.getText().toString().trim();
				if (TextUtils.isEmpty(pwd)) {
					Toast.makeText(LockActivity.this, "请输入密码",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if ("123".equals(pwd)) {

					// 发送广播，通知放行
					Intent intent = new Intent();
					intent.setAction("org.itheima.mobilesafe.applock.free");
					intent.putExtra(KEY_PKG, mPackageName);
					sendBroadcast(intent);
				}
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addCategory("android.intent.category.MONKEY");

		startActivity(intent);

		// 结束自己
		super.onBackPressed();
	}
}

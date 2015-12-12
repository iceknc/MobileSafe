package org.itheima.mobilesafe.activity;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.receiver.SafeAdminReceiver;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class SjfdSetupActivity4 extends SjfdBaseSetupActivity {
	private DevicePolicyManager mDpm;
	private ImageView mIvAdmin;
	private static final int REQUEST_CODE_ENABLE_ADMIN = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sjfd_setup4);

		mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

		initView();
	}

	private void initView() {
		mIvAdmin = (ImageView) findViewById(R.id.setup4_iv_admin);
		// 状态回显
		ComponentName who = new ComponentName(this, SafeAdminReceiver.class);
		mIvAdmin.setImageResource(mDpm.isAdminActive(who) ? R.drawable.admin_activated
				: R.drawable.admin_inactivated);
	}

	@Override
	protected boolean performPre() {
		Intent intent = new Intent(this, SjfdSetupActivity3.class);
		startActivity(intent);
		return false;
	}

	@Override
	protected boolean performNext() {
		// 判断是否激活了设备管理员
		ComponentName who = new ComponentName(this, SafeAdminReceiver.class);
		if (!mDpm.isAdminActive(who)) {
			Toast.makeText(this, "如果要开启防盗保护，必须激活设备管理员", Toast.LENGTH_SHORT)
					.show();
			return true;
		}

		Intent intent = new Intent(this, SjfdSetupActivity5.class);
		startActivity(intent);
		return false;
	}

	public void clickAdmin(View view) {
		ComponentName who = new ComponentName(this, SafeAdminReceiver.class);
		if (mDpm.isAdminActive(who)) {// 如果是激活的，取消激活
			mDpm.removeActiveAdmin(who);
			mIvAdmin.setImageResource(R.drawable.admin_inactivated);
		} else {// 激活管理员
			// TODO:
			Intent intent = new Intent(
					DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
			intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "黑马手机卫士");
			startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_ENABLE_ADMIN) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				mIvAdmin.setImageResource(R.drawable.admin_activated);
				break;
			case Activity.RESULT_CANCELED:
				break;
			default:
				break;
			}
		}
	}
}

package org.itheima.mobilesafe.activity;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.utils.Constants;
import org.itheima.mobilesafe.utils.PreferenceUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class SjfdSetupActivity5 extends SjfdBaseSetupActivity {
	private CheckBox mCbProtecting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sjfd_setup5);

		initView();
		initEvent();
	}

	private void initView() {
		mCbProtecting = (CheckBox) findViewById(R.id.setup5_cb_protecting);

		// 回显状态
		boolean b = PreferenceUtils.getBoolean(this, Constants.SJFD_PROTECTING);
		mCbProtecting.setChecked(b);
	}

	private void initEvent() {
		mCbProtecting.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// 存储勾选状态
				PreferenceUtils.putBoolean(SjfdSetupActivity5.this,
						Constants.SJFD_PROTECTING, isChecked);
			}
		});
	}

	@Override
	protected boolean performPre() {
		Intent intent = new Intent(this, SjfdSetupActivity4.class);
		startActivity(intent);
		return false;
	}

	@Override
	protected boolean performNext() {
		if (!mCbProtecting.isChecked()) {
			Toast.makeText(this, "勾选后才可以开启防盗保护", Toast.LENGTH_SHORT).show();
			return true;
		}

		// 存储勾选状态
		PreferenceUtils.putBoolean(this, Constants.SJFD_PROTECTING, true);
		// 存储设置完成状态
		PreferenceUtils.putBoolean(this, Constants.SJFD_SETUP, true);

		// 下一步
		Intent intent = new Intent(this, SjfdActivity.class);
		startActivity(intent);
		return false;
	}
}

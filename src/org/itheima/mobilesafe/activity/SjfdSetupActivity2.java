package org.itheima.mobilesafe.activity;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.utils.Constants;
import org.itheima.mobilesafe.utils.PreferenceUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class SjfdSetupActivity2 extends SjfdBaseSetupActivity {
	private ImageView mIvBind;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sjfd_setup2);

		initView();
	}

	private void initView() {
		mIvBind = (ImageView) findViewById(R.id.setup2_iv_bind);
		String sim = PreferenceUtils.getString(this, Constants.SJFD_SIM);
		// 状态回显
		mIvBind.setImageResource(TextUtils.isEmpty(sim) ? R.drawable.unlock
				: R.drawable.lock);
	}

	@Override
	protected boolean performPre() {
		Intent intent = new Intent(this, SjfdSetupActivity1.class);
		startActivity(intent);
		return false;
	}

	@Override
	protected boolean performNext() {
		// 校验是否有绑定SIM卡
		String sim = PreferenceUtils.getString(this, Constants.SJFD_SIM);
		if (TextUtils.isEmpty(sim)) {
			Toast.makeText(this, "如果要开启防盗保护，必须绑定SIM卡", Toast.LENGTH_SHORT)
					.show();
			return true;
		}

		Intent intent = new Intent(this, SjfdSetupActivity3.class);
		startActivity(intent);
		return false;
	}

	public void clickBindSim(View view) {
		// 校验是否有绑定SIM卡
		String sim = PreferenceUtils.getString(this, Constants.SJFD_SIM);
		if (!TextUtils.isEmpty(sim)) {
			// 如果绑定过，点击擦除记录
			PreferenceUtils.putString(this, Constants.SJFD_SIM, null);
			//更新图片
			mIvBind.setImageResource(R.drawable.unlock);
		} else {
			// 如果没有绑定就进行绑定操作
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			//获得sim卡的唯一标识
			String simSerialNumber = tm.getSimSerialNumber();
			PreferenceUtils.putString(this, Constants.SJFD_SIM, simSerialNumber);
			//更新图片
			mIvBind.setImageResource(R.drawable.lock);
		}
	}
}

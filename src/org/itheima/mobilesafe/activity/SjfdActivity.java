package org.itheima.mobilesafe.activity;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.utils.Constants;
import org.itheima.mobilesafe.utils.PreferenceUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class SjfdActivity extends Activity {
	private TextView mTvSafeNumber;
	private ImageView mIvProtecting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sjfd);

		initView();
	}

	private void initView() {
		mTvSafeNumber = (TextView) findViewById(R.id.sjfd_tv_Safe_number);
		// 回显号码
		String number = PreferenceUtils.getString(this,
				Constants.SJFD_SAFE_NUMBER);
		mTvSafeNumber.setText(number);

		mIvProtecting = (ImageView) findViewById(R.id.sjfd_iv_protecting);
		// 回显状态
		mIvProtecting.setImageResource(PreferenceUtils.getBoolean(this,
				Constants.SJFD_PROTECTING) ? R.drawable.lock
				: R.drawable.unlock);
	}

	public void clickProtecting(View view) {
		if (PreferenceUtils.getBoolean(this, Constants.SJFD_PROTECTING)) {// 如果是开启的就关闭
			//更新UI
			mIvProtecting.setImageResource(R.drawable.unlock);
			//存储状态
			PreferenceUtils.putBoolean(this, Constants.SJFD_PROTECTING, false);
		}else{
			mIvProtecting.setImageResource(R.drawable.lock);
			PreferenceUtils.putBoolean(this, Constants.SJFD_PROTECTING, true);
		}
	}
	
	public void clickSetup(View view){
		Intent intent = new Intent(this, SjfdSetupActivity1.class);
		startActivity(intent );
		
		overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
		
		finish();
	}
}

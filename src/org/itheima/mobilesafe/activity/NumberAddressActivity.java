package org.itheima.mobilesafe.activity;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.db.NumberAddressDao;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberAddressActivity extends Activity {
	private EditText mEtNumber;
	private TextView mTvAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_number_address);

		initView();
		initEvent();
	}

	private void initView() {
		mEtNumber = (EditText) findViewById(R.id.na_et_number);
		mTvAddress = (TextView) findViewById(R.id.na_et_address);
	}

	private void initEvent() {
		// 输入内容改变时的监听
		mEtNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String number = mEtNumber.getText().toString().trim();
				if (TextUtils.isEmpty(number)) {
					return;
				}
				String address = NumberAddressDao.findAddress(
						NumberAddressActivity.this, number);
				mTvAddress.setText("归属地:" + address);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void clickQuery(View view) {
		// 校验输入
		String number = mEtNumber.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(this, "号码不能为空", Toast.LENGTH_SHORT).show();

			// 抖窗
			TranslateAnimation anim = new TranslateAnimation(0, 10, 0, 0);
			anim.setDuration(1000);
			anim.setInterpolator(new CycleInterpolator(7));

			mEtNumber.startAnimation(anim);
			return;
		}

		String address = NumberAddressDao.findAddress(this, number);
		mTvAddress.setText("归属地:" + address);
	}
}

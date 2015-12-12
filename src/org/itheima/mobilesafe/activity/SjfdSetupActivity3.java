package org.itheima.mobilesafe.activity;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.utils.Constants;
import org.itheima.mobilesafe.utils.PreferenceUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class SjfdSetupActivity3 extends SjfdBaseSetupActivity {
	private EditText mEtNumber;
	private static final int REQUEST_CODE_CONTACT = 0x0001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sjfd_setup3);

		initView();
	}

	private void initView() {
		mEtNumber = (EditText) findViewById(R.id.setup3_et_number);
		// 号码回显
		String number = PreferenceUtils.getString(this,
				Constants.SJFD_SAFE_NUMBER);
		if (!TextUtils.isEmpty(number)) {
			mEtNumber.setText(number);
			// 光标放在文本后面
			mEtNumber.setSelection(number.length());
		}
	}

	@Override
	protected boolean performPre() {
		Intent intent = new Intent(this, SjfdSetupActivity2.class);
		startActivity(intent);
		return false;
	}

	@Override
	protected boolean performNext() {
		// 校验是否输入了安全号码
		String number = mEtNumber.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(this, "如果要开启防盗保护,必须设置安全号码", Toast.LENGTH_SHORT)
					.show();
			return true;
		}
		// 存储安全号码
		PreferenceUtils.putString(this, Constants.SJFD_SAFE_NUMBER, number);

		Intent intent = new Intent(this, SjfdSetupActivity4.class);
		startActivity(intent);
		return false;
	}

	public void clickSelectContact(View view) {
		Intent intent = new Intent(this, SelectContactActivity.class);
		startActivityForResult(intent, REQUEST_CODE_CONTACT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_CONTACT) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				// 数据返回
				String number = data
						.getStringExtra(SelectContactActivity.KEY_NUMBER);
				//设置显示
				mEtNumber.setText(number);
				mEtNumber.setSelection(number.length());
				break;
			default:
				break;
			}
		}
	}
}

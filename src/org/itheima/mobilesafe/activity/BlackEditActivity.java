package org.itheima.mobilesafe.activity;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.bean.BlackBean;
import org.itheima.mobilesafe.db.BlackDao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class BlackEditActivity extends Activity implements OnClickListener {
	public static final String ACTION_ADD = "add";
	public static final String ACITON_UPDATE = "update";

	public static final String KEY_NUMBER = "number";
	public static final String KEY_TYPE = "type";
	public static final String KEY_POSITION = "position";

	private TextView mTvTitle;
	private EditText mEtNumber;
	private RadioGroup mRgType;
	private Button mBtnOk;
	private Button mBtnCancel;

	private BlackDao mDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_balck_edit);

		mDao = new BlackDao(this);

		initView();
		initEvent();
	}

	private void initView() {
		mTvTitle = (TextView) findViewById(R.id.be_tv_title);
		mEtNumber = (EditText) findViewById(R.id.be_et_number);
		mRgType = (RadioGroup) findViewById(R.id.be_rg_type);
		mBtnOk = (Button) findViewById(R.id.be_btn_ok);
		mBtnCancel = (Button) findViewById(R.id.be_btn_cancel);

		Intent intent = getIntent();

		if (ACTION_ADD.equals(intent.getAction())) {
			// 添加页面的控件展示
			mTvTitle.setText("添加黑名单");
			mEtNumber.setEnabled(true);
			mBtnOk.setText("保存");
		} else if (ACITON_UPDATE.equals(intent.getAction())) {
			// 修改页面的控件展示
			mTvTitle.setText("更新黑名单");
			// 编辑框不可用且显示要修改的号码
			mEtNumber.setEnabled(false);
			mEtNumber.setText(intent.getStringExtra(KEY_NUMBER));
			// 类型显示原类型
			int type = intent.getIntExtra(KEY_TYPE, BlackBean.TYPE_NONE);

			switch (type) {
			case BlackBean.TYPE_CALL:
				mRgType.check(R.id.be_rb_call);
				break;
			case BlackBean.TYPE_SMS:
				mRgType.check(R.id.be_rb_sms);
				break;
			case BlackBean.TYPE_ALL:
				mRgType.check(R.id.be_rb_all);
				break;
			default:
				break;
			}

			// 左按钮改更新
			mBtnOk.setText("更新");
		} else {
			throw new RuntimeException("没有指定的action");
		}
	}

	private void initEvent() {
		mBtnOk.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.be_btn_ok:
			clickOk();
			break;
		case R.id.be_btn_cancel:
			finish();
			break;
		default:
			break;
		}
	}

	private void clickOk() {
		// 校验编辑框
		String number = mEtNumber.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			Toast.makeText(this, "号码不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		// 校验拦截类型
		int id = mRgType.getCheckedRadioButtonId();
		if (id == -1) {
			Toast.makeText(this, "类型不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		int type = BlackBean.TYPE_NONE;
		switch (mRgType.getCheckedRadioButtonId()) {
		case R.id.be_rb_call:
			type = BlackBean.TYPE_CALL;
			break;
		case R.id.be_rb_sms:
			type = BlackBean.TYPE_SMS;
			break;
		case R.id.be_rb_all:
			type = BlackBean.TYPE_ALL;
			break;
		default:
			break;

		}

		if (ACTION_ADD.equals(getIntent().getAction())) {
			// 数据存储
			boolean add = mDao.add(number, type);
			if (add) {
				// 返回数据给上一个页面
				Intent intent = getIntent();
				intent.putExtra(KEY_NUMBER, number);
				intent.putExtra(KEY_TYPE, type);
				setResult(Activity.RESULT_OK, intent);

				Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "添加失败", Toast.LENGTH_SHORT).show();
			}
		} else if (ACITON_UPDATE.equals(getIntent().getAction())) {
			// 数据更新
			boolean update = mDao.update(number, type);
			if (update) {
				// 返回数据给上一个页面
				Intent intent = getIntent();
				intent.putExtra(KEY_TYPE, type);
				intent.putExtra(KEY_POSITION,
						getIntent().getIntExtra(KEY_POSITION, -1));
				setResult(Activity.RESULT_OK, intent);
				Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
			}
		}
		finish();
	}
}

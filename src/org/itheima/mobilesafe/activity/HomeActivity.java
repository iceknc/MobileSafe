package org.itheima.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.bean.HomeBean;
import org.itheima.mobilesafe.utils.Constants;
import org.itheima.mobilesafe.utils.PreferenceUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.ObjectAnimator;

public class HomeActivity extends Activity implements OnItemClickListener {
	private ImageView mIvLogo;
	private GridView mGridView;
	private List<HomeBean> mDatas;

	private final static String[] TITLES = new String[] { "手机防盗", "骚扰拦截",
			"软件管家", "进程管理", "流量统计", "手机杀毒", "缓存清理", "常用工具" };
	private final static String[] DESCS = new String[] { "远程定位手机", "全面拦截骚扰",
			"管理您的软件", "管理运行进程", "流量一目了然", "病毒无处藏身", "系统快如火箭", "工具大全" };
	private final static int[] ICONS = new int[] { R.drawable.sjfd,
			R.drawable.srlj, R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj,
			R.drawable.sjsd, R.drawable.hcql, R.drawable.cygj };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);

		// 初始化控件
		initView();
		// logo动画
		logoAnimation();
		// 初始化数据
		initData();
		// 设置事件
		initEvent();

		// 添加一个病毒记录
		//AntiVirusDao.add(this, "0404d4cf63ff7ca4c56bd7700c49536d");
	}

	public void clickSetting(View v) {
		Intent intent = new Intent(this, SettingActivity.class);
		startActivity(intent);
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mIvLogo = (ImageView) findViewById(R.id.home_iv_logo);
		mGridView = (GridView) findViewById(R.id.home_grid_view);
	}

	/**
	 * 初始化logo动画
	 */
	private void logoAnimation() {
		ObjectAnimator ani1 = ObjectAnimator.ofFloat(mIvLogo, "rotationY", 0,
				15, 30, 50, 70, 100, 130, 160, 190, 230, 270, 315, 360, 315,
				270, 230, 190, 160, 130, 100, 70, 50, 30, 15, 0);
		ani1.setDuration(3000);
		ani1.setRepeatCount(ObjectAnimator.INFINITE);
		ani1.setRepeatMode(ObjectAnimator.RESTART);
		ani1.start();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		mDatas = new ArrayList<HomeBean>();
		for (int i = 0; i < ICONS.length; i++) {
			HomeBean bean = new HomeBean();
			bean.icon = ICONS[i];
			bean.title = TITLES[i];
			bean.desc = DESCS[i];

			mDatas.add(bean);
		}

		mGridView.setAdapter(new HomeAdapter());
	}

	private void initEvent() {
		mGridView.setOnItemClickListener(this);
	}

	private class HomeAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mDatas == null) {
				return 0;
			} else {
				return mDatas.size();
			}
		}

		@Override
		public Object getItem(int position) {
			if (mDatas == null) {
				return null;
			} else {
				return mDatas.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(HomeActivity.this,
						R.layout.item_home, null);
			}

			ImageView ivIcon = (ImageView) convertView
					.findViewById(R.id.item_home_iv_icon);
			TextView tvTitle = (TextView) convertView
					.findViewById(R.id.item_home_tv_title);
			TextView tvDesc = (TextView) convertView
					.findViewById(R.id.item_home_tv_desc);

			HomeBean bean = mDatas.get(position);
			ivIcon.setImageResource(bean.icon);
			tvTitle.setText(bean.title);
			tvDesc.setText(bean.desc);

			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			clickSjfd();
			break;
		case 1:
			clickSrlj();
			break;
		case 2:
			clickRjgj();
			break;
		case 3:
			clickJcgl();
			break;
		case 4:
			clickLltj();
			break;
		case 5:
			clickSjsd();
			break;
		case 6:
			clickHcql();
			break;
		case 7:
			clickCygj();
			break;

		}
	}

	/**
	 * 手机防盗item点击响应
	 */
	private void clickSjfd() {
		String password = PreferenceUtils.getString(this, Constants.SJFD_PWD);
		if (TextUtils.isEmpty(password)) {
			// 提示设置密码
			showSetupPwdDialog();
		} else {
			// 提示输入密码
			showEnterPwdDialog();
		}
	}

	/**
	 * 骚扰拦截item点击响应
	 */
	private void clickSrlj() {
		Intent intent = new Intent(this, CallSmsSafeActivity.class);
		startActivity(intent);
	}

	/**
	 * 软件管家item点击响应
	 */
	private void clickRjgj() {
		Intent intent = new Intent(this, AppManagerActivity.class);
		startActivity(intent);
	}

	/**
	 * 软件管家item点击响应
	 */
	private void clickJcgl() {
		Intent intent = new Intent(this, ProcessManagerActivity.class);
		startActivity(intent);
	}

	/**
	 * 流量统计item点击响应
	 */
	private void clickLltj() {
		Intent intent = new Intent(this, TrafficActivity.class);
		startActivity(intent);
	}

	/**
	 * 手机杀毒item点击响应
	 */
	private void clickSjsd() {
		Intent intent = new Intent(this, AntiVirusActivity1.class);
		startActivity(intent);
	}

	/**
	 * 缓存清理item点击响应
	 */
	private void clickHcql() {
		Intent intent = new Intent(this, CacheCleanActivity.class);
		startActivity(intent);
	}

	/**
	 * 常用工具item点击响应
	 */
	private void clickCygj() {
		Intent intent = new Intent(this, CommonToolActivity.class);
		startActivity(intent);
	}

	private void showSetupPwdDialog() {
		// 自定义dialog布局
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.dialog_setup_pwd, null);

		// 查找view
		final EditText etPwd = (EditText) view.findViewById(R.id.dialog_et_pwd);
		final EditText etConfirm = (EditText) view
				.findViewById(R.id.dialog_et_confirm);
		Button btnOk = (Button) view.findViewById(R.id.dialog_btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.dialog_btn_cancel);

		// 设置自定义布局
		builder.setView(view);
		final AlertDialog dialog = builder.show();

		// 设置按钮的点击事件
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 校验输入非空
				String pwd = etPwd.getText().toString().trim();
				if (TextUtils.isEmpty(pwd)) {
					Toast.makeText(getApplicationContext(), "密码不能为空",
							Toast.LENGTH_SHORT).show();
					// 让输入框获得焦点
					etPwd.requestFocus();
					return;
				}

				String confirm = etConfirm.getText().toString().trim();
				if (TextUtils.isEmpty(confirm)) {
					Toast.makeText(getApplicationContext(), "确认密码不能为空",
							Toast.LENGTH_SHORT).show();
					// 让输入框获得焦点
					etConfirm.requestFocus();
					return;
				}

				// 对比两次输入
				if (!pwd.equals(confirm)) {
					Toast.makeText(getApplicationContext(), "两次密码不一致",
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					// 存储密码
					PreferenceUtils.putString(getApplicationContext(),
							Constants.SJFD_PWD, pwd);
					// 隐藏dialog
					dialog.dismiss();
				}
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 隐藏dialog
				dialog.dismiss();
			}
		});

	}

	private void showEnterPwdDialog() {
		// 自定义dialog布局
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.dialog_enter_pwd, null);

		// 查找view
		final EditText etPwd = (EditText) view.findViewById(R.id.dialog_et_pwd);
		Button btnOk = (Button) view.findViewById(R.id.dialog_btn_ok);
		Button btnCancel = (Button) view.findViewById(R.id.dialog_btn_cancel);

		builder.setView(view);
		final AlertDialog dialog = builder.show();

		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 输入非空校验
				String pwd = etPwd.getText().toString().trim();
				if (TextUtils.isEmpty(pwd)) {
					Toast.makeText(HomeActivity.this, "密码不能为空",
							Toast.LENGTH_SHORT).show();
					etPwd.requestFocus();
					return;
				}

				// 校验正确性
				String savePwd = PreferenceUtils.getString(HomeActivity.this,
						Constants.SJFD_PWD);
				if (!savePwd.equals(pwd)) {
					Toast.makeText(HomeActivity.this, "密码错误",
							Toast.LENGTH_SHORT).show();
					etPwd.requestFocus();
					return;
				} else {
					// 检查用户是否设置过手机防盗，若无，提示进行设置
					boolean setup = PreferenceUtils.getBoolean(
							HomeActivity.this, Constants.SJFD_SETUP);
					// 隐藏密码输入框
					dialog.dismiss();
					// 判断是否设置手机防盗
					if (setup) {
						Intent intent = new Intent(HomeActivity.this,
								SjfdActivity.class);
						startActivity(intent);
					} else {
						Intent intent = new Intent(HomeActivity.this,
								SjfdSetupActivity1.class);
						startActivity(intent);
					}
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}

}

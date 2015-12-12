package org.itheima.mobilesafe.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.bean.AppBean;
import org.itheima.mobilesafe.business.AppProvider;
import org.itheima.mobilesafe.view.ProgressStateView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class AppManagerActivity extends Activity implements OnScrollListener,
		OnItemClickListener {
	private ProgressStateView mPsvRom;
	private ProgressStateView mPsvSD;
	private ListView mListView;
	private LinearLayout mIncludeLoading;
	private TextView mTvTitle;

	private List<AppBean> mDatas;
	private List<AppBean> mUserDatas;
	private List<AppBean> mSystemDatas;

	private AppAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);

		initView();

	}

	@Override
	protected void onStart() {
		super.onStart();
		initData();
		initEvent();
	}

	private void initView() {
		mPsvRom = (ProgressStateView) findViewById(R.id.am_psv_rom);
		mPsvSD = (ProgressStateView) findViewById(R.id.am_psv_sd);
		mListView = (ListView) findViewById(R.id.am_listview);
		mIncludeLoading = (LinearLayout) findViewById(R.id.include_loading);
		mTvTitle = (TextView) findViewById(R.id.am_tv_title);
	}

	private void initData() {
		// 内部存储:/data文件夹
		File romDir = Environment.getDataDirectory();
		// rom剩余空间
		long romFreeSpace = romDir.getFreeSpace();
		// rom总空间
		long romTotalSpace = romDir.getTotalSpace();
		// rom已用空间
		long romUsedSpace = romTotalSpace - romFreeSpace;
		// rom已用比例
		int romProgress = (int) (romUsedSpace * 100f / romTotalSpace + 0.5f);
		// UI
		mPsvRom.setTextLeft(Formatter.formatFileSize(this, romUsedSpace) + "已用");
		mPsvRom.setTextRight(Formatter.formatFileSize(this, romFreeSpace)
				+ "可用");
		mPsvRom.setProgress(romProgress);

		// SD卡存储:/mmt or ...
		File sdDir = Environment.getExternalStorageDirectory();
		// sd剩余空间
		long sdFreeSpace = sdDir.getFreeSpace();
		// sd总空间
		long sdTotalSpace = sdDir.getTotalSpace();
		// sd已用空间
		long sdUsedSpace = sdTotalSpace - sdFreeSpace;
		// sd已用比例
		int sdProgress = (int) (sdUsedSpace * 100f / sdTotalSpace + 0.5f);
		// UI
		mPsvSD.setTextLeft(Formatter.formatFileSize(this, sdUsedSpace) + "已用");
		mPsvSD.setTextRight(Formatter.formatFileSize(this, sdFreeSpace) + "可用");
		mPsvSD.setProgress(sdProgress);

		mIncludeLoading.setVisibility(View.VISIBLE);
		mTvTitle.setVisibility(View.GONE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 获取app信息
				mDatas = AppProvider.getAllApps(AppManagerActivity.this);
				mUserDatas = new ArrayList<AppBean>();
				mSystemDatas = new ArrayList<AppBean>();

				// 过滤系统程序和用户程序
				for (AppBean bean : mDatas) {
					if (bean.isSystem) {
						mSystemDatas.add(bean);
					} else {
						mUserDatas.add(bean);
					}
				}
				mDatas.clear();
				mDatas.addAll(mUserDatas);
				mDatas.addAll(mSystemDatas);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mAdapter = new AppAdapter();
						mListView.setAdapter(mAdapter);
						// 数据加载完毕
						mTvTitle.setText("用户程序(" + mUserDatas.size() + "个)");
						mTvTitle.setVisibility(View.VISIBLE);
						mIncludeLoading.setVisibility(View.GONE);
					}
				});
			}
		}).start();
	}

	private void initEvent() {
		mListView.setOnScrollListener(this);
		mListView.setOnItemClickListener(this);
	}

	private class AppAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// if (mDatas != null) {
			// return mDatas.size();
			// }
			int count = 0;
			if (mUserDatas != null) {
				count += mUserDatas.size();
				// 加上用户程序的title
				count++;
			}
			if (mSystemDatas != null) {
				count += mSystemDatas.size();
				// 加上系统程序的title
				count++;
			}
			return count;
		}

		@Override
		public Object getItem(int position) {
			if (position == 0) {// 用户程序的title
				return null;
			}
			if (position <= mUserDatas.size()) {
				return mUserDatas.get(position - 1);
			}
			if (position == mUserDatas.size() + 1) {// 系统程序的title
				return null;
			}
			return mSystemDatas.get(position - mUserDatas.size() - 2);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 用户程序的头
			int userSize = mUserDatas.size();
			if (position == 0) {
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setText("用户程序(" + userSize + "个)");
				tv.setBackgroundColor(Color.parseColor("#FFCECFCE"));
				tv.setPadding(4, 4, 4, 4);

				return tv;
			}

			// 系统程序的头
			int systemSize = mSystemDatas.size();
			if (position == userSize + 1) {
				TextView tv = new TextView(AppManagerActivity.this);
				tv.setText("系统程序(" + systemSize + "个)");
				tv.setBackgroundColor(Color.parseColor("#FFCECFCE"));
				tv.setPadding(4, 4, 4, 4);

				return tv;
			}

			ViewHolder holder = null;
			// 用户程序的头或者系统程序的头变成convertView时，应该从新new一个holder，
			// 不然会getTag()拿到空对象，下面赋值的时候就会空指针
			if (convertView == null || convertView instanceof TextView) {
				convertView = View.inflate(AppManagerActivity.this,
						R.layout.item_app, null);
				holder = new ViewHolder();
				convertView.setTag(holder);

				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_app_iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_app_tv_name);
				holder.tvInstall = (TextView) convertView
						.findViewById(R.id.item_app_tv_install);
				holder.tvSize = (TextView) convertView
						.findViewById(R.id.item_app_tv_size);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			AppBean bean = (AppBean) getItem(position);
			holder.ivIcon.setImageDrawable(bean.icon);
			holder.tvName.setText(bean.name);
			holder.tvSize.setText(Formatter.formatFileSize(
					AppManagerActivity.this, bean.size));
			holder.tvInstall.setText(bean.isInstallSD ? "SD安装" : "内存安装");
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvInstall;
		TextView tvSize;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 暂时无逻辑需要在次执行2015.12.11
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (mUserDatas == null) {
			return;
		}

		int size = mUserDatas.size();
		if (firstVisibleItem >= size + 1) {
			mTvTitle.setText("系统程序(" + mSystemDatas.size() + "个)");
		} else {
			mTvTitle.setText("用户程序(" + mUserDatas.size() + "个)");
		}
	}

	/**
	 * 弹出泡泡窗
	 * 
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final AppBean bean = (AppBean) mAdapter.getItem(position);
		if (bean == null) {
			return;
		}

		View contentView = View.inflate(this, R.layout.popup_app, null);
		int width = LayoutParams.WRAP_CONTENT;
		int height = LayoutParams.WRAP_CONTENT;
		final PopupWindow popupWindow = new PopupWindow(contentView, width,
				height);

		// 只弹出一个popup
		popupWindow.setBackgroundDrawable(new ColorDrawable());
		popupWindow.setFocusable(true);// 可以获得焦点
		popupWindow.setOutsideTouchable(true);// 设置外部可点击
		// 弹出泡泡窗
		popupWindow.showAsDropDown(view, 80, -view.getHeight());

		// 系统程序不可卸载,把卸载按钮隐藏
		contentView.findViewById(R.id.popup_tv_uninstall).setVisibility(
				bean.isSystem ? View.GONE : View.VISIBLE);

		// 判断是否有启动项，没有的话把打开按钮隐藏
		Intent intent = getPackageManager().getLaunchIntentForPackage(
				bean.packageName);
		contentView.findViewById(R.id.popup_tv_open).setVisibility(
				intent == null ? View.GONE : View.VISIBLE);

		// 设置点击事件,信息 按钮
		contentView.findViewById(R.id.popup_tv_info).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 打开系统的程序信息界面
						Intent intent = new Intent();
						intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
						intent.addCategory("android.intent.category.DEFAULT");
						intent.setData(Uri.parse("package:" + bean.packageName));
						startActivity(intent);

						// 隐藏泡泡窗
						popupWindow.dismiss();
					}
				});

		// 设置点击事件，打开 按钮
		contentView.findViewById(R.id.popup_tv_open).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 前面已对无界面不可打开的程序进行了隐藏按钮操作
						Intent intent = getPackageManager()
								.getLaunchIntentForPackage(bean.packageName);
						startActivity(intent);

						// 隐藏泡泡窗
						popupWindow.dismiss();
					}
				});

		// 设置点击事件，卸载 按钮
		contentView.findViewById(R.id.popup_tv_uninstall).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setAction("android.intent.action.DELETE");
						intent.addCategory("android.intent.category.DEFAULT");
						intent.setData(Uri.parse("package:" + bean.packageName));
						startActivity(intent);

						// 隐藏泡泡窗
						popupWindow.dismiss();
					}
				});

		// 设置点击事件,分享 按钮
		contentView.findViewById(R.id.popup_tv_share).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						showShare();
					}
				});
	}

	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();

		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		// oks.setTitle(getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		// oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("hello world!!!");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		// oks.setImagePath("/sdcard/test.jpg");// 确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		// oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		// oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		// oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		// oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		oks.show(this);
	}
}

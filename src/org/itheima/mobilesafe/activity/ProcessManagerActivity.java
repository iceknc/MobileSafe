package org.itheima.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.bean.ProcessBean;
import org.itheima.mobilesafe.business.ProcessProvider;
import org.itheima.mobilesafe.service.AutoCleanSerivice;
import org.itheima.mobilesafe.utils.Constants;
import org.itheima.mobilesafe.utils.PreferenceUtils;
import org.itheima.mobilesafe.utils.ServiceStateUtils;
import org.itheima.mobilesafe.view.ProgressStateView;
import org.itheima.mobilesafe.view.SettingItemTextView;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.ObjectAnimator;

@SuppressWarnings("deprecation")
public class ProcessManagerActivity extends Activity {
	private ProgressStateView mPsvProcess;
	private ProgressStateView mPsvMemory;
	private StickyListHeadersListView mListView;

	private List<ProcessBean> mDatas;
	private List<ProcessBean> mUserDatas;
	private List<ProcessBean> mSystemDatas;
	private ProcessAdapter mAdapter;

	private LinearLayout mLlLoading;

	// 抽屉控件
	private SlidingDrawer mDrawer;
	private ImageView mIvArrow1;
	private ImageView mIvArrow2;
	private ObjectAnimator mAlpha1;
	private ObjectAnimator mAlpha2;

	private SettingItemTextView mSivShowSystem;
	private SettingItemTextView mSivLockClean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_manager);

		initView();
		initData();
		initEvent();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 服务的状态回显
		mSivLockClean.setToggle(ServiceStateUtils.isRunning(this,
				AutoCleanSerivice.class));
	}

	private void initView() {
		mPsvProcess = (ProgressStateView) findViewById(R.id.pm_psv_progress);
		mPsvMemory = (ProgressStateView) findViewById(R.id.pm_psv_memory);
		mListView = (StickyListHeadersListView) findViewById(R.id.pm_listview);
		mLlLoading = (LinearLayout) findViewById(R.id.include_loading);

		mDrawer = (SlidingDrawer) findViewById(R.id.pm_drawer);
		mIvArrow1 = (ImageView) findViewById(R.id.pm_iv_arrow1);
		mIvArrow2 = (ImageView) findViewById(R.id.pm_iv_arrow2);

		mSivShowSystem = (SettingItemTextView) findViewById(R.id.pm_siv_showsystem);
		mSivLockClean = (SettingItemTextView) findViewById(R.id.pm_siv_lockclean);

		// 箭头跳动动画
		startArrowAnimation();
		// 回显显示系统进程
		mSivShowSystem.setToggle(PreferenceUtils.getBoolean(this,
				Constants.SHOW_SYSTEM));

	}

	private void initData() {
		// 进程
		int runningProcessCount = ProcessProvider.getRunningProcessCount(this);
		int allRunningProcessCount = ProcessProvider
				.getAllRunableProcessCount(this);
		int processProgress = (int) (runningProcessCount * 100f
				/ allRunningProcessCount + 0.5f);
		// 进程的UI显示
		mPsvProcess.setTextLeft("正在运行" + runningProcessCount + "个");
		mPsvProcess.setTextRight("可有进程" + allRunningProcessCount + "个");
		mPsvProcess.setProgress(processProgress);

		// 内存
		long availMemory = ProcessProvider.getAvailMemory(this);
		long totalMemory = ProcessProvider.getTotalMemory(this);
		long usedMemory = totalMemory - availMemory;
		int memoryProgress = (int) (usedMemory * 100 / totalMemory + 0.5f);
		// 内存的UI显示
		mPsvMemory.setTextLeft("占用内存:"
				+ Formatter.formatFileSize(this, usedMemory));
		mPsvMemory.setTextRight("可用内存:"
				+ Formatter.formatFileSize(this, availMemory));
		mPsvMemory.setProgress(memoryProgress);

		mLlLoading.setVisibility(View.VISIBLE);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 加载数据并分开系统进程和用户进程
				mDatas = ProcessProvider
						.getProcesses(ProcessManagerActivity.this);
				mUserDatas = new ArrayList<ProcessBean>();
				mSystemDatas = new ArrayList<ProcessBean>();

				for (ProcessBean bean : mDatas) {
					if (bean.isSystem) {
						mSystemDatas.add(bean);
					} else {
						mUserDatas.add(bean);
					}
				}
				// 排序，先显示用户进程再显示系统进程
				mDatas.clear();
				mDatas.addAll(mUserDatas);
				mDatas.addAll(mSystemDatas);

				runOnUiThread(new Runnable() {
					public void run() {
						mAdapter = new ProcessAdapter();
						mListView.setAdapter(mAdapter);

						mLlLoading.setVisibility(View.GONE);
					}
				});
			}
		}).start();
	}

	private void initEvent() {
		// 抽屉拉开的监听
		mDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				endArrowAnimation();
			}
		});
		// 抽屉关上的监听
		mDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				startArrowAnimation();
			}
		});

		mSivShowSystem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean flag = PreferenceUtils.getBoolean(
						ProcessManagerActivity.this, Constants.SHOW_SYSTEM,
						true);
				if (flag) {
					mSivShowSystem.setToggle(false);
					PreferenceUtils.putBoolean(ProcessManagerActivity.this,
							Constants.SHOW_SYSTEM, false);
				} else {
					mSivShowSystem.setToggle(true);
					PreferenceUtils.putBoolean(ProcessManagerActivity.this,
							Constants.SHOW_SYSTEM, true);
				}
				// UI更新
				mAdapter.notifyDataSetChanged();
			}
		});

		mSivLockClean.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean running = ServiceStateUtils.isRunning(
						ProcessManagerActivity.this, AutoCleanSerivice.class);
				if (running) {
					stopService(new Intent(ProcessManagerActivity.this,
							AutoCleanSerivice.class));
					// UI
					mSivLockClean.setToggle(false);
				} else {
					startService(new Intent(ProcessManagerActivity.this,
							AutoCleanSerivice.class));
					// UI
					mSivLockClean.setToggle(true);
				}
			}
		});

	}

	private class ProcessAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {

		@Override
		public int getCount() {
			boolean flag = PreferenceUtils.getBoolean(
					ProcessManagerActivity.this, Constants.SHOW_SYSTEM);
			if (flag) {// 显示所有
				if (mDatas != null) {
					return mDatas.size();
				}
			} else {// 只显示用户进程
				if (mUserDatas != null) {
					return mUserDatas.size();
				}
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			boolean flag = PreferenceUtils.getBoolean(
					ProcessManagerActivity.this, Constants.SHOW_SYSTEM);
			if (flag) {// 显示所有
				if (mDatas != null) {
					return mDatas.get(position);
				}
			} else {// 只显示用户进程
				if (mUserDatas != null) {
					return mUserDatas.get(position);
				}
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(ProcessManagerActivity.this,
						R.layout.item_process, null);

				holder = new ViewHolder();
				convertView.setTag(holder);

				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_process_tv_name);
				holder.tvMemory = (TextView) convertView
						.findViewById(R.id.item_process_tv_memory);
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_process_iv_icon);
				holder.cbSelected = (CheckBox) convertView
						.findViewById(R.id.item_process_cb_selected);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ProcessBean bean = (ProcessBean) getItem(position);

			// 设置数据
			holder.cbSelected.setChecked(bean.isChecked);
			// 把本程序的选择框隐藏
			holder.cbSelected.setVisibility(bean.packageName
					.equals(getPackageName()) ? View.GONE : View.VISIBLE);

			holder.ivIcon.setImageDrawable(bean.icon);
			holder.tvName.setText(bean.name);
			holder.tvMemory.setText("占用内存:"
					+ Formatter.formatFileSize(ProcessManagerActivity.this,
							bean.memory));
			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			HeaderViewHolder holder = null;
			if (convertView == null) {
				convertView = new TextView(ProcessManagerActivity.this);

				holder = new HeaderViewHolder();
				convertView.setTag(holder);

				holder.tvTitle = (TextView) convertView;
				holder.tvTitle.setPadding(4, 4, 4, 4);
				holder.tvTitle
						.setBackgroundColor(Color.parseColor("#FFCECECE"));
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}

			ProcessBean bean = (ProcessBean) getItem(position);
			if (bean.isSystem) {
				holder.tvTitle.setText("系统程序(" + mSystemDatas.size() + ")");
			} else {
				holder.tvTitle.setText("用户程序(" + mUserDatas.size() + ")");
			}
			return convertView;
		}

		@Override
		public long getHeaderId(int position) {
			ProcessBean bean = (ProcessBean) getItem(position);
			return bean.isSystem ? 0 : 1;
		}
	}

	private class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvMemory;
		CheckBox cbSelected;
	}

	private class HeaderViewHolder {
		TextView tvTitle;
	}

	private void startArrowAnimation() {
		// 有动画在播放的话先取消
		if (mAlpha1 != null) {
			mAlpha1.cancel();
		}
		if (mAlpha2 != null) {
			mAlpha2.cancel();
		}

		mIvArrow1.setImageResource(R.drawable.drawer_arrow_up);
		mIvArrow2.setImageResource(R.drawable.drawer_arrow_up);

		mAlpha1 = ObjectAnimator.ofFloat(mIvArrow1, "alpha", 0.3f, 1.0f);
		mAlpha1.setDuration(300);
		mAlpha1.setRepeatMode(Animation.REVERSE);
		mAlpha1.setRepeatCount(Animation.INFINITE);
		mAlpha1.start();

		mAlpha2 = ObjectAnimator.ofFloat(mIvArrow2, "alpha", 1.0f, 0.3f);
		mAlpha2.setDuration(300);
		mAlpha2.setRepeatMode(Animation.REVERSE);
		mAlpha2.setRepeatCount(Animation.INFINITE);
		mAlpha2.start();
	}

	private void endArrowAnimation() {
		// 有动画在播放的话先取消
		if (mAlpha1 != null) {
			mAlpha1.cancel();
		}
		if (mAlpha2 != null) {
			mAlpha2.cancel();
		}

		mIvArrow1.setImageResource(R.drawable.drawer_arrow_down);
		mIvArrow2.setImageResource(R.drawable.drawer_arrow_down);

		// 用恒定的动画来兼容sdk-10， setAlpha(int)在sdk-10设置无效,setAlpha(float)要求sdk-11以上
		mAlpha1 = ObjectAnimator.ofFloat(mIvArrow1, "alpha", 1.0f, 1.0f);
		mAlpha1.setDuration(250);
		mAlpha1.start();

		mAlpha2 = ObjectAnimator.ofFloat(mIvArrow2, "alpha", 1.0f, 1.0f);
		mAlpha2.setDuration(250);
		mAlpha2.start();
	}

	public void clickAll(View view) {
		// 数据
		for (ProcessBean bean : mDatas) {
			if (bean.packageName.equals(getPackageName())) {
				continue;
			}
			bean.isChecked = true;
		}
		// UI更新
		mAdapter.notifyDataSetChanged();
	}

	public void clickReverse(View view) {
		// 数据
		for (ProcessBean bean : mDatas) {
			if (bean.packageName.equals(getPackageName())) {
				continue;
			}
			bean.isChecked = !bean.isChecked;
		}
		// UI更新
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 点击清理选中的进程
	 * 
	 * @param view
	 */
	public void clickClean(View view) {
		int count = 0;
		long memory = 0;

		// 显示系统的，也就是全部显示
		if (PreferenceUtils.getBoolean(this, Constants.SHOW_SYSTEM)) {
			// mDatas里找选中的移除,用for会有并发修改异常
			ListIterator<ProcessBean> iterator = mDatas.listIterator();
			while (iterator.hasNext()) {
				ProcessBean next = iterator.next();
				if (next.isChecked) {
					// 杀死进程
					ProcessProvider.killProcess(this, next.packageName);

					// mDatas集合里移除数据
					iterator.remove();
					// 用户进程的集合中也要移除
					mUserDatas.remove(next);

					count++;
					memory += next.memory;
				}
			}

			// UI更新
			// mAdapter.notifyDataSetChanged();
			initData();
		} else {// 显示用户的
			ListIterator<ProcessBean> iterator = mUserDatas.listIterator();
			while (iterator.hasNext()) {
				ProcessBean next = iterator.next();
				if (next.isChecked) {
					// 杀死进程
					ProcessProvider.killProcess(this, next.packageName);

					// mUserDatas里移除数据
					iterator.remove();
					// mDatas里也一并移除
					mDatas.remove(next);

					count++;
					memory += next.memory;
				}
			}
			// UI更新
			// mAdapter.notifyDataSetChanged();
			initData();
		}

		if (count == 0) {
			Toast.makeText(this, "请选中后再清理", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(
					this,
					"清理了" + count + "个进程,释放了"
							+ Formatter.formatFileSize(this, memory) + "的内存",
					Toast.LENGTH_SHORT).show();
		}
	}
}

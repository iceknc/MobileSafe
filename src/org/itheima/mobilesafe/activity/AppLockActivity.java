package org.itheima.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.bean.AppBean;
import org.itheima.mobilesafe.business.AppProvider;
import org.itheima.mobilesafe.db.AppLockDao;
import org.itheima.mobilesafe.view.SegementView;
import org.itheima.mobilesafe.view.SegementView.OnSegementListener;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AppLockActivity extends Activity {
	private SegementView mSegementView;

	private TextView mTvTitle;

	private ListView mLvUnlock;
	private ListView mLvLock;

	private LinearLayout mLoading;

	private List<AppBean> mUnlockDatas;
	private List<AppBean> mLockDatas;

	private boolean isLock = false;// 默认显示未加锁的页面

	private AppLockDao mDao;

	private TranslateAnimation lockTa;
	private TranslateAnimation unlockTa;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);

		mDao = new AppLockDao(this);

		initView();
		initAnimation();
		initEvent();
		initData();

	}

	private void initView() {
		mSegementView = (SegementView) findViewById(R.id.al_sv_lock);
		mTvTitle = (TextView) findViewById(R.id.al_tv_title);
		mLvUnlock = (ListView) findViewById(R.id.al_listview_unlock);
		mLvLock = (ListView) findViewById(R.id.al_listview_lock);
		mLoading = (LinearLayout) findViewById(R.id.include_loading);
	}

	private void initAnimation() {
		lockTa = new TranslateAnimation(TranslateAnimation.RELATIVE_TO_PARENT,
				0, TranslateAnimation.RELATIVE_TO_PARENT, 1,
				TranslateAnimation.RELATIVE_TO_PARENT, 0,
				TranslateAnimation.RELATIVE_TO_PARENT, 0);
		lockTa.setDuration(200);

		unlockTa = new TranslateAnimation(
				TranslateAnimation.RELATIVE_TO_PARENT, 0,
				TranslateAnimation.RELATIVE_TO_PARENT, -1,
				TranslateAnimation.RELATIVE_TO_PARENT, 0,
				TranslateAnimation.RELATIVE_TO_PARENT, 0);
		unlockTa.setDuration(200);
	}

	private void initEvent() {
		mSegementView.setOnSegementListener(new OnSegementListener() {

			@Override
			public void onSelected(boolean leftSelected) {
				if (leftSelected) {
					if (mUnlockDatas != null) {
						mTvTitle.setText("未加锁(" + mUnlockDatas.size() + ")");
					}

					isLock = false;
					// 换ListView页面
					mLvUnlock.setVisibility(View.VISIBLE);
					mLvLock.setVisibility(View.GONE);
				} else {
					if (mLockDatas != null) {
						mTvTitle.setText("已加锁(" + mLockDatas.size() + ")");
					}

					isLock = true;
					// 换ListView页面
					mLvUnlock.setVisibility(View.GONE);
					mLvLock.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	private void initData() {
		new AsyncTask<Void, Void, Void>() {
			protected void onPreExecute() {
				mTvTitle.setVisibility(View.GONE);
				mLoading.setVisibility(View.VISIBLE);
			};

			@Override
			protected Void doInBackground(Void... params) {
				// 获得所有可启动的应用程序
				List<AppBean> allApps = AppProvider
						.getLaunchedApps(AppLockActivity.this);

				mLockDatas = new ArrayList<AppBean>();
				mUnlockDatas = new ArrayList<AppBean>();

				for (AppBean bean : allApps) {
					if (mDao.isLock(bean.packageName)) {
						// 上锁的
						mLockDatas.add(bean);
					} else {
						// 未上锁的
						mUnlockDatas.add(bean);
					}
				}

				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				mLvLock.setAdapter(new AppLockAdapter(true));
				mLvUnlock.setAdapter(new AppLockAdapter(false));

				// 设置title的显示
				if (isLock) {
					mTvTitle.setText("已加锁(" + mLockDatas.size() + ")");
				} else {
					mTvTitle.setText("未加锁(" + mUnlockDatas.size() + ")");
				}
				mTvTitle.setVisibility(View.VISIBLE);
				mLoading.setVisibility(View.GONE);
			};
		}.execute();
	}

	private class AppLockAdapter extends BaseAdapter {
		private boolean mLock;

		public AppLockAdapter(boolean lock) {
			this.mLock = lock;
		}

		@Override
		public int getCount() {
			if (mLock) {
				// 已经加锁的
				if (mLockDatas != null) {
					return mLockDatas.size();
				}
			} else {
				if (mUnlockDatas != null) {
					return mUnlockDatas.size();
				}
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mLock) {
				// 已经加锁的
				if (mLockDatas != null) {
					return mLockDatas.get(position);
				}
			} else {
				if (mUnlockDatas != null) {
					return mUnlockDatas.get(position);
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
				convertView = View.inflate(AppLockActivity.this,
						R.layout.item_applock, null);
				holder = new ViewHolder();
				convertView.setTag(holder);

				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_applock_iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_applock_tv_name);
				holder.ivLock = (ImageView) convertView
						.findViewById(R.id.item_applock_iv_lock);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			AppBean bean = (AppBean) getItem(position);

			holder.ivIcon.setImageDrawable(bean.icon);
			holder.tvName.setText(bean.name);
			// 已加锁界面要显示解锁的图片，未加锁的界面要显示加锁的图片
			holder.ivLock
					.setImageResource(mLock ? R.drawable.btn_unlock_selector
							: R.drawable.btn_lock_selector);
			final View view = convertView;
			// 标记播放动画的item
			Flag flag = (Flag) holder.ivLock.getTag();
			// 没有item在播放动画
			if (flag == null) {
				flag = new Flag();
				flag.isAnimationPlaying = false;
				flag.bean = bean;
				holder.ivLock.setTag(flag);
			} else {
				flag.bean = bean;
			}

			holder.ivLock.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final Flag flag = (Flag) v.getTag();
					if (flag.isAnimationPlaying) {
						return;
					}
					if (!mLock) {
						// 添加，动画从左往右走
						lockTa.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								flag.isAnimationPlaying = true;
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								// 动画完成
								boolean add = mDao.add(flag.bean.packageName);
								if (add) {
									// 添加成功,未加锁的数据移除，已加锁的数据添加
									mUnlockDatas.remove(flag.bean);
									mLockDatas.add(flag.bean);

									// UI更新
									notifyDataSetChanged();
									mTvTitle.setText("未加锁("
											+ mUnlockDatas.size() + ")");
								} else {
									Toast.makeText(AppLockActivity.this,
											"添加失败", Toast.LENGTH_SHORT).show();
								}
								// 改变动画播放中的标记
								flag.isAnimationPlaying = false;
							}
						});
						view.startAnimation(lockTa);
					} else {
						// 删除，动画从右往左走
						unlockTa.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								flag.isAnimationPlaying = true;
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								boolean delete = mDao
										.delete(flag.bean.packageName);
								if (delete) {
									// 未加锁的数据添加，已加锁的数据移除
									mUnlockDatas.add(flag.bean);
									mLockDatas.remove(flag.bean);
									// UI更新
									notifyDataSetChanged();
									mTvTitle.setText("已加锁(" + mLockDatas.size()
											+ ")");
								} else {
									Toast.makeText(AppLockActivity.this,
											"解锁失败", Toast.LENGTH_SHORT).show();
								}
								flag.isAnimationPlaying = false;
							}
						});
						view.startAnimation(unlockTa);
					}
				}
			});
			return convertView;
		}
	}

	private class Flag {
		boolean isAnimationPlaying;
		AppBean bean;
	}

	private class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		ImageView ivLock;
	}
}

package org.itheima.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.bean.VirusBean;
import org.itheima.mobilesafe.binder.AntiVirusBinder;
import org.itheima.mobilesafe.binder.AntiVirusBinder.OnAntiVirusBinderListener;
import org.itheima.mobilesafe.service.AntiVirusService;
import org.itheima.mobilesafe.utils.ServiceStateUtils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class AntiVirusActivity1 extends Activity implements
		OnAntiVirusBinderListener {
	protected static final String TAG = "AntiVirusActivity1";
	private ListView mListView;
	private TextView mTvRlName;
	private RelativeLayout mRlContainerScan;
	private LinearLayout mLlContainerResult;
	private TextView mTvResult;
	private Button mBtnRescan;
	private ArcProgress mArcProgress;
	private ImageView mIvAnimLeft;
	private ImageView mIvAnimRight;

	private List<VirusBean> mDatas;
	private AntiAdapter mAdapter;

	private int mProgress = 0;
	private int mTotalVirus;

	private AntiVirusBinder mBinder;

	private ServiceConnection mConn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "服务连接成功");
			mBinder = (AntiVirusBinder) service;
			mBinder.setOnAntiVirusBinderListener(AntiVirusActivity1.this);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);

		initView();

		initEvent();
	}

	@Override
	protected void onStart() {
		super.onStart();
		initData();
		performScan();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (ServiceStateUtils.isRunning(getApplicationContext(),
				AntiVirusService.class)) {
			// 解绑服务
			unbindService(mConn);
		}
	}

	private void initView() {
		mListView = (ListView) findViewById(R.id.anti_listview);
		mTvRlName = (TextView) findViewById(R.id.anti_rl_name);
		mRlContainerScan = (RelativeLayout) findViewById(R.id.anti_rl_cantainer_scan);
		mLlContainerResult = (LinearLayout) findViewById(R.id.anti_ll_container_result);
		mTvResult = (TextView) findViewById(R.id.anti_tv_result);
		mBtnRescan = (Button) findViewById(R.id.anti_btn_rescan);
		mArcProgress = (ArcProgress) findViewById(R.id.anti_arc_progress);
		mIvAnimLeft = (ImageView) findViewById(R.id.anti_anim_left);
		mIvAnimRight = (ImageView) findViewById(R.id.anti_anim_right);
	}

	private void initData() {
		mAdapter = new AntiAdapter();
		mDatas = new ArrayList<VirusBean>();
		mListView.setAdapter(mAdapter);
	}

	private void performScan() {
		Intent service = new Intent(this, AntiVirusService.class);
		startService(service);
		bindService(service, mConn, Context.BIND_AUTO_CREATE);

		mDatas.clear();
		mAdapter.notifyDataSetChanged();
		mTotalVirus = 0;
	}

	private void initEvent() {
		mBtnRescan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doAnimation(false);
			}
		});
	}

	private void doAnimation(final boolean isOpen) {
		mIvAnimLeft.measure(0, 0);
		mIvAnimRight.measure(0, 0);
		int leftWidth = mIvAnimLeft.getMeasuredWidth();
		int rightWidth = mIvAnimRight.getMeasuredWidth();

		AnimatorSet set = new AnimatorSet();
		if (isOpen) {
			set.playTogether(//
					ObjectAnimator.ofFloat(mIvAnimLeft, "translationX", 0,
							-leftWidth), //
					ObjectAnimator.ofFloat(mIvAnimLeft, "alpha", 1, 0), //
					ObjectAnimator.ofFloat(mIvAnimRight, "translationX", 0,
							rightWidth),//
					ObjectAnimator.ofFloat(mIvAnimRight, "alpha", 1, 0),//
					ObjectAnimator.ofFloat(mLlContainerResult, "alpha", 0, 1));//
		} else {
			set.playTogether(//
					ObjectAnimator.ofFloat(mIvAnimLeft, "translationX",
							-leftWidth, 0), //
					ObjectAnimator.ofFloat(mIvAnimLeft, "alpha", 0, 1), //
					ObjectAnimator.ofFloat(mIvAnimRight, "translationX",
							rightWidth, 0),//
					ObjectAnimator.ofFloat(mIvAnimRight, "alpha", 0, 1),//
					ObjectAnimator.ofFloat(mLlContainerResult, "alpha", 1, 0));//
		}
		set.setDuration(3000);
		set.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {
				// 扫描按钮设置不能用
				mBtnRescan.setEnabled(false);
			}

			@Override
			public void onAnimationRepeat(Animator arg0) {
			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				mBtnRescan.setEnabled(true);
				if (!isOpen) {
					performScan();
				}
			}

			@Override
			public void onAnimationCancel(Animator arg0) {
			}
		});

		set.start();
	}

	private Bitmap getBitmap(Bitmap bitmap, boolean left) {
		// 画纸
		Bitmap copy = Bitmap.createBitmap(
				(int) (bitmap.getWidth() / 2f + 0.5f), bitmap.getHeight(),
				bitmap.getConfig());
		// 画布
		Canvas canvas = new Canvas(copy);
		// 画笔
		Paint paint = new Paint();
		// 规则
		Matrix matrix = new Matrix();
		if (!left) {
			matrix.setTranslate(-(int) (bitmap.getWidth() / 2f + 0.5f), 0);
		}

		canvas.drawBitmap(bitmap, matrix, paint);
		return copy;
	}

	private class AntiAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mDatas != null) {
				return mDatas.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mDatas != null) {
				return mDatas.get(position);
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
				convertView = View.inflate(AntiVirusActivity1.this,
						R.layout.item_virus, null);
				holder = new ViewHolder();
				convertView.setTag(holder);

				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_virus_iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_virus_tv_name);
				holder.tvIsVirus = (TextView) convertView
						.findViewById(R.id.item_virus_tv_isvirus);
				holder.ivClean = (ImageView) convertView
						.findViewById(R.id.item_virus_iv_clean);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final VirusBean bean = (VirusBean) getItem(position);

			holder.ivIcon.setImageDrawable(bean.icon);
			holder.tvName.setText(bean.name);
			holder.tvIsVirus.setText(bean.isVirus ? "病毒" : "安全");
			holder.tvIsVirus.setTextColor(bean.isVirus ? Color.RED
					: Color.GREEN);
			holder.ivClean.setVisibility(bean.isVirus ? View.VISIBLE
					: View.GONE);

			holder.ivClean.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
					intent.addCategory("android.intent.category.DEFAULT");
					intent.setData(Uri.parse("package:" + bean.packageName));

					startActivity(intent);
				}
			});
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView ivIcon;
		TextView tvName;
		TextView tvIsVirus;
		ImageView ivClean;
	}

	@Override
	public void onPreExecute() {
		mRlContainerScan.setVisibility(View.VISIBLE);
		mLlContainerResult.setVisibility(View.GONE);
		mIvAnimLeft.setVisibility(View.GONE);
		mIvAnimRight.setVisibility(View.GONE);
		Log.d(TAG, "准备工作完成");
	}

	@Override
	public void onProgressUpdate(VirusBean bean, int max) {
		// 记录病毒的数量
		if (bean.isVirus) {
			mTotalVirus++;
			mDatas.add(0, bean);
		} else {
			mDatas.add(bean);
		}
		mAdapter.notifyDataSetChanged();

		// 更新到最后一个回滚到第一个的位置，否则滚动到最后一个的位置
		if (mAdapter.getCount() >= max) {
			mListView.smoothScrollToPosition(0);
		} else {
			mListView.smoothScrollToPosition(mAdapter.getCount());
		}

		// 设置当前扫描的名字显示
		mTvRlName.setText(bean.name);
		// 设置进度条
		mProgress++;
		int percent = (int) (mProgress * 100f / max + 0.5f);
		if (mTotalVirus > 0) {
			if (mTotalVirus <= 5) {
				mArcProgress.setFinishedStrokeColor(Color
						.parseColor("#88FF0000"));
			} else if (mTotalVirus <= 10) {
				mArcProgress.setFinishedStrokeColor(Color
						.parseColor("#99FF0000"));
			} else if (mTotalVirus <= 15) {
				mArcProgress.setFinishedStrokeColor(Color
						.parseColor("#AAFF0000"));
			} else if (mTotalVirus <= 20) {
				mArcProgress.setFinishedStrokeColor(Color
						.parseColor("#BBFF0000"));
			} else if (mTotalVirus <= 25) {
				mArcProgress.setFinishedStrokeColor(Color
						.parseColor("#CCFF0000"));
			} else if (mTotalVirus <= 30) {
				mArcProgress.setFinishedStrokeColor(Color
						.parseColor("#DDFF0000"));
			} else if (mTotalVirus <= 35) {
				mArcProgress.setFinishedStrokeColor(Color
						.parseColor("#EEFF0000"));
			} else if (mTotalVirus <= 40) {
				mArcProgress.setFinishedStrokeColor(Color
						.parseColor("#FFFF0000"));
			}
		}
		mArcProgress.setProgress(percent);
	}

	@Override
	public void onPostExecute() {
		// Log.d("AntiVirusService", Thread.currentThread().getName());
		// listview移动回顶部
		// mAdapter.notifyDataSetChanged();
		// mListView.smoothScrollToPosition(0);

		// 显示扫描结果
		if (mTotalVirus > 0) {
			mTvResult.setText("共发现" + mTotalVirus + "个病毒");
		}
		// 隐藏扫描中的页面
		mRlContainerScan.setVisibility(View.GONE);
		mLlContainerResult.setVisibility(View.VISIBLE);

		// 取出扫描完成的背景
		mRlContainerScan.setDrawingCacheEnabled(true);
		mRlContainerScan
				.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
		Bitmap cache = mRlContainerScan.getDrawingCache();

		// 扫描容器开门效果渐渐消失，扫描结果渐渐出现
		mIvAnimLeft.setImageBitmap(getBitmap(cache, true));
		mIvAnimRight.setImageBitmap(getBitmap(cache, false));
		mIvAnimLeft.setVisibility(View.VISIBLE);
		mIvAnimRight.setVisibility(View.VISIBLE);
		doAnimation(true);

		if (ServiceStateUtils.isRunning(getApplicationContext(),
				AntiVirusService.class)) {
			// 解绑服务
			unbindService(mConn);
		}
	}
}

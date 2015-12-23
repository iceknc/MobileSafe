package org.itheima.mobilesafe.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.bean.VirusBean;
import org.itheima.mobilesafe.db.AntiVirusDao;
import org.itheima.mobilesafe.utils.MD5Utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class AntiVirusActivity extends Activity {
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

	private PackageManager mPm;

	private int mTotalVirus;

	private boolean needStop = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);

		initView();
		initData();
		initEvent();
	}

	@Override
	protected void onStart() {
		super.onStart();
		needStop = false;
		performScan();
	}

	@Override
	protected void onStop() {
		super.onStop();
		needStop = true;
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
		mDatas.clear();
		mAdapter.notifyDataSetChanged();
		mTotalVirus = 0;

		new AsyncTask<Void, VirusBean, Void>() {
			private int max;
			private int progress;

			protected void onPreExecute() {
				mRlContainerScan.setVisibility(View.VISIBLE);
				mLlContainerResult.setVisibility(View.GONE);
				mIvAnimLeft.setVisibility(View.GONE);
				mIvAnimRight.setVisibility(View.GONE);
			};

			@Override
			protected Void doInBackground(Void... params) {
				mPm = getPackageManager();

				List<PackageInfo> packages = mPm.getInstalledPackages(0);
				max = packages.size();

				for (PackageInfo info : packages) {
					if (needStop) {
						break;
					}
					ApplicationInfo applicationInfo = info.applicationInfo;
					Drawable icon = applicationInfo.loadIcon(mPm);
					String name = applicationInfo.loadLabel(mPm).toString();
					String packageName = info.packageName;

					VirusBean bean = new VirusBean();
					bean.icon = icon;
					bean.name = name;
					bean.packageName = packageName;

					// 对比病毒库看是否是病毒
					try {
						String apkFile = applicationInfo.sourceDir;
						File file = new File(apkFile);

						String md5 = MD5Utils.encode(new FileInputStream(file));

						bean.isVirus = AntiVirusDao.isVirus(AntiVirusActivity.this, md5);

					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}

					// 添加到集合，病毒程序靠前
					if (bean.isVirus) {
						mDatas.add(0, bean);
					} else {
						mDatas.add(bean);
					}

					publishProgress(bean);
					progress++;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return null;
			}

			protected void onProgressUpdate(VirusBean... values) {
				mAdapter.notifyDataSetChanged();
				// 记录病毒的数量
				if (values[0].isVirus) {
					mTotalVirus++;
				}

				mListView.smoothScrollToPosition(progress);

				// 设置当前扫描的名字显示
				mTvRlName.setText(values[0].name);
				// 设置进度条
				int percent = (int) (progress * 100 / max + 0.5f);
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
			};

			protected void onPostExecute(Void result) {
				// listview移动回顶部
				mListView.smoothScrollToPosition(0);
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
			};
		}.execute();
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
				// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
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
				convertView = View.inflate(AntiVirusActivity.this,
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
}

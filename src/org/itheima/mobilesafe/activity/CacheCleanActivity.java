package org.itheima.mobilesafe.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.db.CacheBean;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CacheCleanActivity extends Activity implements OnClickListener {
	private static final String TAG = "CacheCleanActivity";

	private ImageView mIvScanLine;// 扫描线
	private ImageView mIvScanIcon;// 扫描中的图标
	private ProgressBar mPbScanProgress;// 扫描的进度条
	private TextView mTvScanName;// 扫描的程序名字
	private TextView mTvScanCache;// 扫描的缓存大小
	private ListView mListView;
	private Button mBtnClean;
	private RelativeLayout mRlContainerScan;// 扫描中的容器
	private RelativeLayout mRlContainerResult;// 扫描结果的容器
	private Button mBtnScan;
	private TextView mTvScanResult;

	private PackageManager mPm;

	private CacheAdapter mAdapter;

	private List<CacheBean> mDatas;

	private int mCacheCount;// 存在缓存的应用个数
	private long mCacheTotalSize;// 总的缓存数

	private boolean needStop = false;// 标记扫描动作是否需要停止

	private ScanTask mTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cache_clean);
		
		mPm = getPackageManager();

		initView();
		initData();
		initEvent();
	}

	@Override
	protected void onStart() {
		super.onStart();
		needStop = false;
		// 执行扫描
		performScan();
	}

	@Override
	protected void onPause() {
		super.onPause();

		needStop = true;
	}

	private void initView() {
		mIvScanIcon = (ImageView) findViewById(R.id.cc_iv_icon);
		mIvScanLine = (ImageView) findViewById(R.id.cc_iv_scan_line);
		mPbScanProgress = (ProgressBar) findViewById(R.id.cc_pb_scan_progress);
		mTvScanName = (TextView) findViewById(R.id.cc_tv_scan_name);
		mTvScanCache = (TextView) findViewById(R.id.cc_tv_scan_cache);
		mListView = (ListView) findViewById(R.id.cc_listview);
		mBtnClean = (Button) findViewById(R.id.cc_btn_clean);
		mRlContainerScan = (RelativeLayout) findViewById(R.id.cc_scan_container);
		mRlContainerResult = (RelativeLayout) findViewById(R.id.cc_scan_container_result);
		mBtnScan = (Button) findViewById(R.id.cc_btn_scan);
		mTvScanResult = (TextView) findViewById(R.id.cc_tv_scan_result);
	}

	private void initData() {
		// listview加载一个view更新一次，就会呈现出动画
		mAdapter = new CacheAdapter();
		mListView.setAdapter(mAdapter);
	}

	private void performScan() {
		needStop = false;

		mTask = new ScanTask();
		mTask.execute();
	}

	private class ScanTask extends AsyncTask<Void, CacheBean, Void> {
		private int max;
		private int progress;

		private boolean isFinish = false;

		protected void onPreExecute() {
			// 扫描线做上下运动
			TranslateAnimation ta = new TranslateAnimation(
					TranslateAnimation.RELATIVE_TO_PARENT, 0,
					TranslateAnimation.RELATIVE_TO_PARENT, 0,
					TranslateAnimation.RELATIVE_TO_PARENT, 0,
					TranslateAnimation.RELATIVE_TO_PARENT, 1);
			ta.setDuration(600);
			ta.setRepeatCount(TranslateAnimation.INFINITE);
			ta.setRepeatMode(TranslateAnimation.REVERSE);
			mIvScanLine.startAnimation(ta);

			// 设置扫描中的容器可见，扫描结果的容器不可见
			mRlContainerScan.setVisibility(View.VISIBLE);
			mRlContainerResult.setVisibility(View.GONE);

			// 清除记录的数据
			mCacheCount = 0;
			mCacheTotalSize = 0;
			isFinish = false;
			
			mDatas = new ArrayList<CacheBean>();
			mAdapter.notifyDataSetChanged();
			
			mBtnClean.setEnabled(false);
		};

		@Override
		protected Void doInBackground(Void... params) {
			List<PackageInfo> pkgs = mPm.getInstalledPackages(0);
			max = pkgs.size();

			for (PackageInfo info : pkgs) {
				if (needStop) {
					break;
				}

				progress++;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				try {
					Method method = mPm.getClass().getMethod(
							"getPackageSizeInfo", String.class,
							IPackageStatsObserver.class);
					method.setAccessible(true);
					method.invoke(mPm, info.packageName, mStatsObserver);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		public void updateProgress(CacheBean... values) {
			publishProgress(values);
		}

		@Override
		protected void onProgressUpdate(CacheBean... values) {
			// 进度
			mPbScanProgress.setMax(max);
			mPbScanProgress.setProgress(progress);

			// listview
			mAdapter.notifyDataSetChanged();
			mListView.smoothScrollToPosition(mAdapter.getCount());

			// 显示正在扫描的信息
			mIvScanIcon.setImageDrawable(values[0].icon);
			mTvScanName.setText(values[0].name);
			mTvScanCache.setText("缓存大小:"
					+ Formatter.formatFileSize(CacheCleanActivity.this,
							values[0].cacheSize));

			if (isFinish) {
				onPostExecute(null);
			}
		};

		@Override
		protected void onPostExecute(Void result) {
			isFinish = true;

			// listview回滚顶部
			mListView.smoothScrollToPosition(0);

			// 扫描结果
			mTvScanResult.setText("发现有"
					+ mCacheCount
					+ "个缓存，大小是"
					+ Formatter.formatFileSize(CacheCleanActivity.this,
							mCacheTotalSize));

			// 设置扫描中的容器不可见，扫描结果的容器可见
			mRlContainerScan.setVisibility(View.GONE);
			mRlContainerResult.setVisibility(View.VISIBLE);

			mBtnClean.setEnabled(true);
		}
	}

	private class CacheAdapter extends BaseAdapter {

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
				convertView = View.inflate(CacheCleanActivity.this,
						R.layout.item_cache, null);

				Log.d(TAG, convertView.getClass().toString());

				holder = new ViewHolder();
				convertView.setTag(holder);

				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_cache_iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_cache_tv_name);
				holder.tvCache = (TextView) convertView
						.findViewById(R.id.item_cache_tv_size);
				holder.ivClean = (ImageView) convertView
						.findViewById(R.id.item_cache_iv_clean);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final CacheBean bean = (CacheBean) getItem(position);
			holder.ivIcon.setImageDrawable(bean.icon);
			holder.tvName.setText(bean.name);
			holder.tvCache.setText("缓存大小:"
					+ Formatter.formatFileSize(CacheCleanActivity.this,
							bean.cacheSize));
			holder.ivClean.setVisibility(bean.cacheSize > 0 ? View.VISIBLE
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
		TextView tvCache;
		ImageView ivClean;
	}

	private void initEvent() {
		mBtnClean.setOnClickListener(this);
		mBtnScan.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cc_btn_clean:
			// 一键清理按钮
			Log.d(TAG, "一键清理");
			break;
		case R.id.cc_btn_scan:
			// 快速扫描按钮
			performScan();
			break;
		}
	}

	final IPackageStatsObserver.Stub mStatsObserver = new IPackageStatsObserver.Stub() {
		public void onGetStatsCompleted(PackageStats stats, boolean succeeded) {
			long cacheSize = stats.cacheSize;

			String packageName = stats.packageName;

			ApplicationInfo applicationInfo;
			try {
				applicationInfo = mPm.getApplicationInfo(packageName, 0);
				CacheBean bean = new CacheBean();
				bean.icon = applicationInfo.loadIcon(mPm);
				bean.name = applicationInfo.loadLabel(mPm).toString();
				bean.packageName = applicationInfo.packageName;
				bean.cacheSize = cacheSize;

				if (cacheSize > 0) {
					mCacheCount++;
					mCacheTotalSize += cacheSize;
					mDatas.add(0, bean);
				} else {
					mDatas.add(bean);
				}

				mTask.updateProgress(bean);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
	};

}

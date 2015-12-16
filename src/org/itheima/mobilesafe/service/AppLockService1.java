package org.itheima.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import org.itheima.mobilesafe.activity.LockActivity;
import org.itheima.mobilesafe.db.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class AppLockService1 extends Service {
	private ActivityManager mAm;
	private AppLockDao mDao;

	private List<String> mFrees = new ArrayList<String>();

	private boolean isRunning;
	private List<String> mAllLocks;

	private AppLockReceiver mReceiver;
	private ContentResolver mCr;
	private DBObserver mObserver;

	private static final String TAG = "AppLockService1";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "程序锁服务1开启");

		mDao = new AppLockDao(this);
		mAllLocks = mDao.findAll();

		// 监听用户开启了哪个程序
		startWatch();

		// 注册广播接收者，监听开关屏幕
		mReceiver = new AppLockReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction("org.itheima.mobilesafe.applock.free");
		registerReceiver(mReceiver, filter);

		// 注册数据库发生改变的内容观察着
		mCr = getContentResolver();
		mObserver = new DBObserver(new Handler());
		Uri uri = Uri.parse("content://applock");
		mCr.registerContentObserver(uri, true, mObserver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "程序锁服务1关闭");

		isRunning = false;
	}

	private void startWatch() {
		isRunning = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (isRunning) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					mAm = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					// 获得当前运行的任务
					List<RunningTaskInfo> tasks = mAm.getRunningTasks(1);
					RunningTaskInfo info = tasks.get(0);
					ComponentName componentName = info.topActivity;

					String className = componentName.getClassName();
					String packageName = componentName.getPackageName();
					Log.d(TAG, className + "---" + packageName);

					if (mFrees.contains(packageName)) {
						continue;
					}

					boolean lock = mAllLocks.contains(packageName);

					// 判断是否上锁
					if (lock) {
						Intent intent = new Intent(AppLockService1.this,
								LockActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra(LockActivity.KEY_PKG, packageName);
						startActivity(intent);
					}
				}
			}
		}).start();

	}

	private class AppLockReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				startWatch();
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				// 锁屏
				isRunning = false;
				mFrees.clear();
			} else {
				// 获得放行的包名
				String packageName = intent
						.getStringExtra(LockActivity.KEY_PKG);
				mFrees.add(packageName);
			}
		}
	}

	private class DBObserver extends ContentObserver {

		public DBObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			Log.d(TAG, "数据库发生改变了");
			mAllLocks = mDao.findAll();
		}
	}
}

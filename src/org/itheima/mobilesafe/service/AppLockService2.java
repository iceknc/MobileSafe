package org.itheima.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import org.itheima.mobilesafe.activity.LockActivity;
import org.itheima.mobilesafe.db.AppLockDao;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;

public class AppLockService2 extends AccessibilityService {
	private AppLockDao mDao;
	List<String> mAllLocks;
	List<String> mFrees = new ArrayList<String>();

	private AppLockReceiver mReceiver;
	private DBObserver mObserver;
	private ContentResolver mCr;

	@Override
	public void onCreate() {
		super.onCreate();

		mDao = new AppLockDao(this);
		mAllLocks = mDao.findAll();

		mReceiver = new AppLockReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction("org.itheima.mobilesafe.applock.free");

		registerReceiver(mReceiver, filter);

		// 注册数据库发生变化的内容观察者
		mCr = getContentResolver();
		mObserver = new DBObserver(new Handler());
		Uri uri = Uri.parse("content://applock");
		mCr.registerContentObserver(uri, true, mObserver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		mCr.unregisterContentObserver(mObserver);
	}

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		CharSequence packageName = event.getPackageName();

		// 是否放行
		if (mFrees.contains(packageName)) {
			return;
		}

		boolean lock = mAllLocks.contains(packageName);
		if (lock) {
			Intent intent = new Intent(AppLockService2.this, LockActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(LockActivity.KEY_PKG, packageName);
			startActivity(intent);
		}
	}

	@Override
	public void onInterrupt() {

	}

	private class AppLockReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				// 锁屏清除放行列表
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
			mAllLocks = mDao.findAll();
		}
	}
}

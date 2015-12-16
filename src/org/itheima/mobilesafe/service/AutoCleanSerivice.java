package org.itheima.mobilesafe.service;

import java.util.List;

import org.itheima.mobilesafe.bean.ProcessBean;
import org.itheima.mobilesafe.business.ProcessProvider;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class AutoCleanSerivice extends Service {

	private LockScreenReceiver mReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 监听锁屏广播
		mReceiver = new LockScreenReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);
		Log.d("abc", "锁屏自动清理");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
	}

	private class LockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 屏幕锁定时
			List<ProcessBean> processes = ProcessProvider
					.getProcesses(AutoCleanSerivice.this);
			for (ProcessBean bean : processes) {
				ProcessProvider.killProcess(AutoCleanSerivice.this,
						bean.packageName);
			}
		}
	}
}

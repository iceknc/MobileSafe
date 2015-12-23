package org.itheima.mobilesafe.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.itheima.mobilesafe.bean.VirusBean;
import org.itheima.mobilesafe.binder.AntiVirusBinder;
import org.itheima.mobilesafe.db.AntiVirusDao;
import org.itheima.mobilesafe.utils.MD5Utils;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class AntiVirusService extends Service {

	private static final String TAG = "AntiVirusService";

	private AntiVirusBinder mBinder;
	private PackageManager mPm;
	private Handler handler = new Handler();

	private List<VirusBean> mDatas = new ArrayList<VirusBean>();

	private boolean isPreFinish = false;
	private boolean isSonProgressFinish = false;
	private boolean isMainProgressFinish = true;

	private int progress = 0;

	// 创建锁
	private Lock lock = new ReentrantLock();
	// 主线程的锁
	private Condition con_main = lock.newCondition();
	// 子线程的锁
	private Condition con_son = lock.newCondition();

	@Override
	public IBinder onBind(Intent intent) {
		mBinder = new AntiVirusBinder();
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "手机杀毒的服务开启");
		mPm = getPackageManager();
		new Thread(new ScanVirusTask()).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "手机杀毒的服务关闭");
	}

	private class ScanVirusTask implements Runnable {

		@Override
		public void run() {
			// 准备耗时操作，主线程
			doPreExecute();

			// 耗时操作，子线程
			List<PackageInfo> packages = mPm.getInstalledPackages(0);
			int max = packages.size();

			for (PackageInfo info : packages) {
				lock.lock();
				// 初始动作没完成就不进行下一步操作
				while (!isPreFinish) {
					try {
						Log.d(TAG, "子线程等待主线程完成初始动作");
						con_son.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// 等到更新完成了才进行下一步操作
				while (!isMainProgressFinish) {
					try {
						Log.d(TAG, "子线程等待主线程完成更新动作");
						con_son.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				Log.d(TAG, "子线程开始造bean--->" + ++progress);

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

					bean.isVirus = AntiVirusDao.isVirus(AntiVirusService.this,
							md5);

				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}

				// 添加到集合，病毒程序靠前
				if (bean.isVirus) {
					mDatas.add(0, bean);
				} else {
					mDatas.add(bean);
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				Log.d(TAG, "子线程通知主线程更新进度");
				// 执行更新操作，主线程
				doProgressUpdate(bean, max);

				// 标记子线程完成一次更新
				isSonProgressFinish = true;
				isMainProgressFinish = false;
				// 唤醒主线程进行进度更新
				con_main.signalAll();
				lock.unlock();
			}
			
			
			lock.lock();
			while(!isMainProgressFinish){
				try {
					Log.d(TAG, "子线程等待最后一次更新的唤醒");
					con_son.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Log.d(TAG, "子线程跳出最后一次更新的等待,调用post方法");	
			lock.unlock();
			
			
			stopSelf();
			doPostExecute();
		}
	}

	public void doPreExecute() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				try {
					// 加锁
					lock.lock();

					if (mBinder != null) {
						mBinder.doPreExecute();
						// 标记准备工作完成
						isPreFinish = true;

						Log.d(TAG, "主线程完成准备工作");

						// 唤醒子线程开始做耗时操作
						con_son.signalAll();
					}
				} finally {
					lock.unlock();
				}
			}
		});
	}

	public void doProgressUpdate(final VirusBean bean, final int max) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				try {
					// 加锁
					lock.lock();

					while (!isSonProgressFinish) {
						try {
							Log.d(TAG, "主线程等待新进度数据");
							// 子线程没数据发过来，就等待
							con_main.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					if (mBinder != null) {
						mBinder.doProgressUpdate(bean, max);

						// 标记更新工作完成一次
						isMainProgressFinish = true;
						// 标记子线程需要发送新数据
						isSonProgressFinish = false;

						Log.d(TAG, "主线程完成一次进度更新");

						// 唤醒子线程开始做耗时操作
						con_son.signalAll();
					}
				} finally {
					lock.unlock();
				}
			}
		});
	}

	public void doPostExecute() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (mBinder != null) {
					mBinder.doPostExecute();
				}
			}
		});
	}
}

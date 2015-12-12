package org.itheima.mobilesafe.business;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;

public class ProgressProvider {
	/**
	 * 获得正在运行的进程数量
	 * 
	 * @param context
	 * @return 正在运行的进程数量
	 */
	public static int getRunningProgressCount(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	public static int getAllRunningProgressCount(Context context) {
		// 所有应用程序中的清单文件中四大组件和Application标签的progress属性
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packages = pm.getInstalledPackages(0);

		// 去重
		Set<String> set = new HashSet<String>();

		for (PackageInfo info : packages) {
			String processName = info.applicationInfo.processName;
			set.add(processName);

			// 遍历所有的activity标签
			ActivityInfo[] activities = info.activities;
			if (activities != null) {
				for (ActivityInfo activity : activities) {
					set.add(activity.processName);
				}
			}

			// 遍历所有的service标签
			ServiceInfo[] services = info.services;
			if (services != null) {
				for (ServiceInfo service : services) {
					set.add(service.processName);
				}
			}

			// 遍历所有的广播接收者标签
			ActivityInfo[] receivers = info.receivers;
			if (receivers != null) {
				for (ActivityInfo recriver : receivers) {
					set.add(recriver.processName);
				}
			}
		}

		return 0;
	}
}

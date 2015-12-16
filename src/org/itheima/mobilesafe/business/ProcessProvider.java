package org.itheima.mobilesafe.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.bean.ProcessBean;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.content.pm.ServiceInfo;
import android.os.Build;

public class ProcessProvider {
	/**
	 * 获得正在运行的进程数量
	 * 
	 * @param context
	 * @return 正在运行的进程数量
	 */
	public static int getRunningProcessCount(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		if (list != null) {
			return list.size();
		}
		return 0;
	}

	/**
	 * 获得设备的可执行进程数量
	 * 
	 * @param context
	 * @return 可执行进程数量
	 */
	public static int getAllRunableProcessCount(Context context) {
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

			// 遍历所有的provider标签
			ProviderInfo[] providers = info.providers;
			if (providers != null) {
				for (ProviderInfo provider : providers) {
					set.add(provider.processName);
				}
			}
		}
		return set.size();
	}

	/**
	 * 获取设备的可用内存
	 * 
	 * @param context
	 * @return 可用内存
	 */
	public static long getAvailMemory(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}

	/**
	 * 获取设备的总内存
	 * 
	 * @param context
	 * @return 总内存数
	 */
	@SuppressLint("NewApi")
	public static long getTotalMemory(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);

		// 版本适配
		int sdkInt = Build.VERSION.SDK_INT;
		if (sdkInt >= Build.VERSION_CODES.JELLY_BEAN) {
			return outInfo.totalMem;
		} else {
			return getLowTotalMemory();
		}
	}

	private static long getLowTotalMemory() {
		// /proc/meminfo
		File file = new File("/proc/meminfo");
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			// MemTotal: 513744 kB
			String line = br.readLine();
			line = line.replace("MemTotal:", "");
			line = line.replace("kB", "");
			line.trim();
			return Long.valueOf(line) * 1024;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				br = null;
			}
		}
	}

	public static List<ProcessBean> getProcesses(Context context) {
		List<ProcessBean> datas = new ArrayList<ProcessBean>();
		// 获得活动管理者
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		// 获得包管理者
		PackageManager pm = context.getPackageManager();

		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		for (RunningAppProcessInfo info : list) {
			String packageName = info.processName;
			int pid = info.pid;

			ProcessBean bean = new ProcessBean();
			try {
				ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);

				bean.icon = appInfo.loadIcon(pm);
				bean.name = appInfo.loadLabel(pm).toString();

				boolean isSystem = false;
				int flags = appInfo.flags;
				if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
					isSystem = true;
				}
				bean.isSystem = isSystem;

			} catch (NameNotFoundException e) {
				// 系统底部C进程无图标无名字
				bean.icon = context.getResources().getDrawable(
						R.drawable.ic_default);
				bean.name = packageName;
				bean.isSystem = true;
				e.printStackTrace();
			}

			android.os.Debug.MemoryInfo memoryInfo = am
					.getProcessMemoryInfo(new int[] { pid })[0];
			bean.memory = memoryInfo.getTotalPss();
			bean.packageName = packageName;

			datas.add(bean);
		}
		return datas;
	}

	/**
	 * 杀死进程
	 * 
	 * @param context
	 * @param packageName
	 *            进程的名字
	 */
	public static void killProcess(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(packageName);
	}
}

package org.itheima.mobilesafe.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.itheima.mobilesafe.bean.AppBean;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class AppProvider {
	/**
	 * 获得全部应用程序的信息
	 * 
	 * @param context
	 * @return
	 */
	public static List<AppBean> getAllApps(Context context) {
		List<AppBean> list = new ArrayList<AppBean>();

		// 包管理器
		PackageManager pm = context.getPackageManager();

		// 获得所有安装的应用程序的清单文件
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);

		for (PackageInfo info : packageInfos) {
			ApplicationInfo applicationInfo = info.applicationInfo;
			// 获得名字图标
			String name = applicationInfo.loadLabel(pm).toString();
			Drawable icon = applicationInfo.loadIcon(pm);
			// 获得安装路径，便可获得文件大小
			File file = new File(applicationInfo.sourceDir);

			// 是否是系统应用和是否安装在SD上
			int flags = applicationInfo.flags;
			boolean isSystem = false;
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				isSystem = true;
			}
			boolean isInstallSD = false;
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				isInstallSD = true;
			}

			AppBean bean = new AppBean();
			bean.name = name;
			bean.icon = icon;
			bean.size = file.length();
			bean.isSystem = isSystem;
			bean.isInstallSD = isInstallSD;
			bean.packageName = info.packageName;

			list.add(bean);
		}
		return list;
	}

	/**
	 * 获得设备中有启动项的程序
	 * 
	 * @param context
	 * @return
	 */
	public static List<AppBean> getLaunchedApps(Context context) {
		List<AppBean> list = new ArrayList<AppBean>();

		PackageManager pm = context.getPackageManager();
		// 获得所有应用程序的清单文件
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		for (PackageInfo info : packages) {
			Intent intent = pm.getLaunchIntentForPackage(info.packageName);
			if (intent == null) {
				// 过滤出没有启动项的程序
				continue;
			}
			ApplicationInfo applicationInfo = info.applicationInfo;

			Drawable icon = applicationInfo.loadIcon(pm);
			String name = applicationInfo.loadLabel(pm).toString();

			File file = new File(applicationInfo.sourceDir);

			int flags = applicationInfo.flags;
			boolean isSystem = false;
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				isSystem = true;
			}

			boolean isInstallSD = false;
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
				isInstallSD = true;
			}

			AppBean bean = new AppBean();
			bean.name = name;
			bean.icon = icon;
			bean.size = file.length();
			bean.isSystem = isSystem;
			bean.isInstallSD = isInstallSD;
			bean.packageName = info.packageName;

			list.add(bean);
		}

		return list;
	}
}

package org.itheima.mobilesafe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class PackageUtils {
	/**
	 * 获取版本信息
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		// 获取包管理器
		PackageManager pm = context.getPackageManager();
		try {
			// 清单文件的对象
			PackageInfo packageInfo = pm.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "unknow";
		}
	}

	/**
	 * 获得版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		// 获取包管理器
		PackageManager pm = context.getPackageManager();
		try {
			// 清单文件的对象
			PackageInfo packageInfo = pm.getPackageInfo(
					context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return -1;
		}
	}
}

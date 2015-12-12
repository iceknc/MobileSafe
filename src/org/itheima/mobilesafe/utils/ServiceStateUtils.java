package org.itheima.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;

public class ServiceStateUtils {

	public static boolean isRunning(Context context,
			Class<? extends Service> clazz) {
		// 获得活动管理者
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = am.getRunningServices(Integer.MAX_VALUE);
		
		for (RunningServiceInfo info : services) {
			ComponentName service = info.service;
			String className = service.getClassName();
			
			//对比类名,相同就是开启
			if(clazz.getName().equals(className)){
				return true;
			}
		}
		return false;
	}
}

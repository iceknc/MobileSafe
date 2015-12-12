package org.itheima.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class AppBean {

	/**
	 * app图标
	 */
	public Drawable icon;
	/**
	 * app名字
	 */
	public String name;
	/**
	 * 是否安装在SD卡上
	 */
	public boolean isInstallSD;
	/**
	 * 安装包的大小
	 */
	public long size;
	/**
	 * 是否是系统程序
	 */
	public boolean isSystem;
	/**
	 * 应用的包名
	 */
	public String packageName;
	@Override
	public String toString() {
		return "AppBean [icon=" + icon + ", name=" + name + ", isInstallSD="
				+ isInstallSD + ", size=" + size + ", isSystem=" + isSystem
				+ ", packageName=" + packageName + "]";
	}
	
	
}

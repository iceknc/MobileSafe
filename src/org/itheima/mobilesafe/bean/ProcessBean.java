package org.itheima.mobilesafe.bean;

import android.graphics.drawable.Drawable;

public class ProcessBean {
	/**
	 * 进程的图标
	 */
	public Drawable icon;
	/**
	 * 进程的名字
	 */
	public String name;
	/**
	 * 进程的包名
	 */
	public String packageName;
	/**
	 * 进程占用的内存
	 */
	public long memory;
	/**
	 * 是否系统进程
	 */
	public boolean isSystem;
	/**
	 * 标记是否被用户选中
	 */
	public boolean isChecked = false;
}

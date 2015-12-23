package org.itheima.mobilesafe.binder;

import org.itheima.mobilesafe.bean.VirusBean;

import android.os.Binder;

public class AntiVirusBinder extends Binder {
	private OnAntiVirusBinderListener mListener;

	public void setOnAntiVirusBinderListener(OnAntiVirusBinderListener listener) {
		mListener = listener;
	}

	public void doPreExecute() {
		if (mListener != null) {
			mListener.onPreExecute();
		}
	}

	public void doProgressUpdate(VirusBean bean, int max) {
		if (mListener != null) {
			mListener.onProgressUpdate(bean, max);
		}
	}
	

	public void doPostExecute() {
		if (mListener != null) {
			mListener.onPostExecute();
		}
	}

	public interface OnAntiVirusBinderListener {

		/**
		 * 扫描开始前的回调
		 */
		void onPreExecute();

		/**
		 * 更新进度的回调
		 * 
		 * @param bean 
		 * @param max 进度的最大值
		 */
		void onProgressUpdate(VirusBean bean, int max);
		
		/**
		 * 扫描完后后的回调
		 */
		void onPostExecute();

	}
}

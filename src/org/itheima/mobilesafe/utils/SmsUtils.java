package org.itheima.mobilesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.itheima.mobilesafe.bean.SmsBean;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SmsUtils {
	private static final String TAG = "SmsUtils";

	public static void smsBackup(final Context context,
			final OnSmsBackupListener listener) {
		new AsyncTask<Void, Integer, Exception>() {

			@Override
			protected void onPreExecute() {
				if (listener != null) {
					listener.onPreExecute();
				}
			};

			@Override
			protected Exception doInBackground(Void... params) {
				List<SmsBean> list = new ArrayList<SmsBean>();
				// 获得内容解析者
				ContentResolver resolver = context.getContentResolver();

				Uri uri = Uri.parse("content://sms");
				String[] projection = new String[] { "address", "date", "type",
						"read", "body" };
				String selection = null;
				String[] selectionArgs = null;
				String sortOrder = null;
				Cursor cursor = resolver.query(uri, projection, selection,
						selectionArgs, sortOrder);

				int progress = 0;
				int max = 0;
				if (cursor != null) {
					max = cursor.getCount();
					while (cursor.moveToNext()) {
						String address = cursor.getString(0);
						long date = cursor.getLong(1);
						int type = cursor.getInt(2);
						int read = cursor.getInt(3);
						String body = cursor.getString(4);

						SmsBean bean = new SmsBean();
						bean.address = address;
						bean.date = date;
						bean.type = type;
						bean.read = read;
						bean.body = body;

						list.add(bean);

						publishProgress(max, ++progress);

						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
							return e;
						}
					}
					cursor.close();
				}

				// 存储到本地
				Gson gson = new Gson();
				String json = gson.toJson(list);

				File file = new File(Environment.getExternalStorageDirectory(),
						"sms.json");
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(file);
					fos.write(json.getBytes());
				} catch (Exception e) {
					e.printStackTrace();
					return e;
				} finally {
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						fos = null;
					}
				}
				return null;
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				if (listener != null) {
					listener.onProgress(values[0], values[1]);
				}
			};

			protected void onPostExecute(Exception result) {
				if (listener != null) {
					if (result == null) {
						listener.onSeccuss();
					} else {
						listener.onError(result);
					}
				}
			};
		}.execute();
	}

	public interface OnSmsBackupListener {
		/**
		 * 短信备份操作的准备阶段，在主线程中执行
		 */
		void onPreExecute();

		/**
		 * 短信备份操作的进度,在主线程中执行
		 * 
		 * @param max
		 *            最大值
		 * @param progress
		 *            当前进度
		 */
		void onProgress(Integer max, Integer progress);

		/**
		 * 备份出错时的回调,在主线程中执行
		 * 
		 * @param result
		 *            错误报告
		 */
		void onError(Exception result);

		/**
		 * 备份成功时的回调,在主线程中执行
		 */
		void onSeccuss();
	}

	public static void smsRestore(final Context context,
			final OnSmsRestoreListener listener) {
		new AsyncTask<Void, Integer, Exception>() {
			protected void onPreExecute() {
				if (listener != null) {
					listener.onPreExecute();
				}
			};

			@Override
			protected Exception doInBackground(Void... params) {
				// 读取备份的文件
				File file = new File(Environment.getExternalStorageDirectory(),
						"sms.json");

				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(file));
					String json = br.readLine();

					Gson gson = new Gson();
					List<SmsBean> list = gson.fromJson(json,
							new TypeToken<List<SmsBean>>() {
							}.getType());

					// 把数据插入到短信的数据库
					ContentResolver resolver = context.getContentResolver();
					Uri url = Uri.parse("content://sms");
					for (int i = 0; i < list.size(); i++) {
						SmsBean bean = list.get(i);

						// 短信的数据库已存在的记录不需要重复写入
						String selection = " address=? AND date=? AND type=? AND read=? AND body=?";
						String[] selectionArgs = new String[] { bean.address,
								String.valueOf(bean.date),
								String.valueOf(bean.type),
								String.valueOf(bean.read), bean.body };
						Cursor cursor = resolver.query(url, null, selection,
								selectionArgs, null);
						if (cursor != null) {
							if (cursor.moveToNext()) {
								Log.d(TAG, "还原短信操作-->过滤出一条相同的");
								publishProgress(list.size(), i);
								continue;
							}
						}

						// 插入短信的数据库不存在的记录
						ContentValues values = new ContentValues();
						values.put("address", bean.address);
						values.put("date", bean.date);
						values.put("type", bean.type);
						values.put("read", bean.read);
						values.put("body", bean.body);
						resolver.insert(url, values);
						Log.d(TAG, "还原短信操作-->还原了一条短信");
						publishProgress(list.size(), i);

						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					return e;
				}
				return null;
			}

			protected void onProgressUpdate(Integer... values) {
				if (listener != null) {
					listener.onProgress(values[0], values[1]);
				}
			};

			protected void onPostExecute(Exception result) {
				if (listener != null) {
					if (result == null) {
						listener.onSeccuss();
					} else {
						listener.onError(result);
					}
				}
			};
		}.execute();
	}

	public interface OnSmsRestoreListener {
		/**
		 * 短信还原操作的准备阶段，在主线程中执行
		 */
		void onPreExecute();

		/**
		 * 短信还原操作的进度,在主线程中执行
		 * 
		 * @param max
		 *            最大值
		 * @param progress
		 *            当前进度
		 */
		void onProgress(Integer max, Integer progress);

		/**
		 * 还原出错时的回调,在主线程中执行
		 * 
		 * @param result
		 *            错误报告(IOExcetion或者FileNotFoundException
		 */
		void onError(Exception result);

		/**
		 * 还原成功时的回调,在主线程中执行
		 */
		void onSeccuss();
	}
}

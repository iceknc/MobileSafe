package org.itheima.mobilesafe.activity;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.utils.Constants;
import org.itheima.mobilesafe.utils.GZipUtils;
import org.itheima.mobilesafe.utils.PackageUtils;
import org.itheima.mobilesafe.utils.PreferenceUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class SplashActivity extends Activity {
	private TextView mTvVersion;
	private static final String TAG = "SplashActivity";
	private static final int SHOW_DIALOG = 0x0001;
	private static final int SHOW_ERROR = 0x0002;
	private static final int REQUEST_CODE_INSTALL = 0x0003;
	private String mDesc;
	private String mUrl;
	private int mNetVersion;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOW_DIALOG:
				showUpdateDialog();
				break;
			case SHOW_ERROR:
				// 显示错误提醒
				Toast.makeText(SplashActivity.this, msg.obj.toString(),
						Toast.LENGTH_SHORT).show();
				// 进入主页
				loadHome();
				break;
			default:
				loadHome();
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// 初始化View
		initView();
		// // 初始化数据
		initData();
	}

	/**
	 * 初始化View对象
	 */
	private void initView() {
		mTvVersion = (TextView) findViewById(R.id.splash_tv_version);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 设置版本号的显示
		mTvVersion.setText("版本号:" + PackageUtils.getVersionName(this));
		// 检测版本
		if (PreferenceUtils.getBoolean(SplashActivity.this,
				Constants.SETTING_AUTO_UPDATE)) {// 开启了自动更新
			new Thread(new CheckVersionTask()).start();
		} else {// 关闭了自动更新
			loadHome();
		}

		// 加载号码归属地的数据，加压zip文件
		copyNumberAddressDB();

		// 加载常用号码数据库
		copyCommonNumberDB();
	}

	/**
	 * 检查版本更新的内部类
	 * 
	 * @author Administrator
	 * 
	 */
	private class CheckVersionTask implements Runnable {
		@Override
		public void run() {
			InputStream is = null;
			ByteArrayOutputStream baos = null;
			try {
				// 指定服务器存放的更新信息
				URL url = new URL("http://188.188.1.30:8080/update.json");
				// 建立连接
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);

				conn.connect();

				int code = conn.getResponseCode();
				if (code == 200) {// 服务器成功响应
					is = conn.getInputStream();
					baos = new ByteArrayOutputStream();

					// 把数据写进baos里
					int len = -1;
					byte[] bys = new byte[1024];
					while ((len = is.read(bys)) != -1) {
						baos.write(bys, 0, len);
					}

					String json = baos.toString();
					Log.d(TAG, json);

					// 服务器的app版本
					JSONObject obj = new JSONObject(json);
					// 记录服务器的版本信息
					mNetVersion = obj.getInt("VersionCode");
					mDesc = obj.getString("Desc");
					mUrl = obj.getString("URL");
					// 当前手机的app版本
					int localVersion = PackageUtils
							.getVersionCode(SplashActivity.this);

					// 版本比对
					if (localVersion < mNetVersion) {
						// 弹出升级提示
						Message msg = mHandler.obtainMessage();
						msg.what = SHOW_DIALOG;
						msg.sendToTarget();
					} else {
						// 无需升级直接进主界面
						loadHome();
					}
				}
			} catch (MalformedURLException e) {
				Message msg = mHandler.obtainMessage();
				msg.what = SHOW_ERROR;
				msg.obj = "Error:-100";
				msg.sendToTarget();
				e.printStackTrace();
			} catch (IOException e) {
				Message msg = mHandler.obtainMessage();
				msg.what = SHOW_ERROR;
				msg.obj = "Error:-101";
				msg.sendToTarget();
				e.printStackTrace();
			} catch (JSONException e) {
				Message msg = mHandler.obtainMessage();
				msg.what = SHOW_ERROR;
				msg.obj = "Error:-102";
				msg.sendToTarget();
				e.printStackTrace();
			} finally {
				closeStream(is);
			}
		}
	}

	/**
	 * 关闭流对象
	 * 
	 * @param stream
	 *            实现了Closeable接口的对象
	 */
	private void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			stream = null;
		}
	}

	/**
	 * 加载主界面，延迟1500毫秒
	 */
	private void loadHome() {
		// 延迟加载，让用户多看一眼Logo
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this,
						HomeActivity.class);
				startActivity(intent);
				finish();
			}
		}, 1500);

	}

	/**
	 * 显示版本更新提示
	 */
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// 设置点击dialog区域外无响应
		builder.setCancelable(false);

		builder.setTitle("版本更新提醒");
		builder.setMessage(mDesc);
		builder.setPositiveButton("立即更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 下载新版本,带进度条
				downloadNewVersion();
			}
		});
		builder.setNegativeButton("稍后再说", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 不更新直接进入主界面
				loadHome();
			}
		});
		// 子线程中运行
		builder.show();
	}

	/**
	 * 下载新版本
	 */
	private void downloadNewVersion() {
		// 设置进度条
		ProgressDialog pd = new ProgressDialog(SplashActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setCancelable(false);

		// 下载新版本
		new Thread(new DownloadNewVersionTask(pd)).start();
		pd.show();
	}

	/**
	 * 下载新版本的内部类
	 * 
	 * @author Administrator
	 * 
	 */
	private class DownloadNewVersionTask implements Runnable {
		private ProgressDialog pd;

		public DownloadNewVersionTask(ProgressDialog pd) {
			this.pd = pd;
		}

		@Override
		public void run() {

			InputStream is = null;
			FileOutputStream fos = null;
			try {
				URL url = new URL(mUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				conn.connect();

				is = conn.getInputStream();
				// 设置进度条最大值
				pd.setMax(conn.getContentLength());
				// 获取升级文件的文件名
				String apkName = mUrl.substring(mUrl.lastIndexOf("/"));
				File file = new File(Environment.getExternalStorageDirectory()
						.getPath() + apkName);
				fos = new FileOutputStream(file);

				int len = -1;
				int progress = 0;
				byte[] bys = new byte[1024];
				while ((len = is.read(bys)) != -1) {
					fos.write(bys, 0, len);
					fos.flush();
					progress += len;
					pd.setProgress(progress);

					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// 下载完成，关闭progressdialog并提示用户安装
				pd.dismiss();

				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setDataAndType(Uri.fromFile(file),
						"application/vnd.android.package-archive");
				startActivityForResult(intent, REQUEST_CODE_INSTALL);
			} catch (MalformedURLException e) {
				Message msg = mHandler.obtainMessage();
				msg.what = SHOW_ERROR;
				msg.obj = "Error:-100";
				msg.sendToTarget();
				pd.dismiss();
				e.printStackTrace();
			} catch (IOException e) {
				Message msg = mHandler.obtainMessage();
				msg.what = SHOW_ERROR;
				msg.obj = "Error:-101";
				msg.sendToTarget();
				pd.dismiss();
				e.printStackTrace();
			} finally {
				closeStream(is);
				closeStream(fos);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE_INSTALL) {
			switch (resultCode) {
			case Activity.RESULT_OK:
				Log.d(TAG, "确定");
				break;
			case Activity.RESULT_CANCELED:
				Log.d(TAG, "取消");
				loadHome();
				break;
			default:
				break;
			}
		}
	}

	private void copyNumberAddressDB() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				File file = new File(getFilesDir(), "address.db");
				// 检查是否加压过
				if (file.exists()) {
					return;
				}

				AssetManager assets = getAssets();
				try {
					FileOutputStream fos = new FileOutputStream(file);
					InputStream inputStream = assets.open("address.zip");

					GZipUtils.unZip(inputStream, fos);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void copyCommonNumberDB() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				File file = new File(getFilesDir(), "commonnum.db");
				if (file.exists()) {
					return;
				}
				// 不存在文件就copy
				AssetManager assets = getAssets();
				InputStream is = null;
				FileOutputStream fos = null;
				try {
					is = assets.open("commonnum.db");
					fos = new FileOutputStream(file);

					byte[] bys = new byte[1024];
					int len = -1;
					while ((len = is.read(bys)) != -1) {
						fos.write(bys, 0, len);
						fos.flush();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					close(is);
					close(fos);
				}
			}
		}).start();
	}

	private void close(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			io = null;
		}
	}
}

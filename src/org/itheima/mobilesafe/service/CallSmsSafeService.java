package org.itheima.mobilesafe.service;

import java.lang.reflect.Method;

import org.itheima.mobilesafe.bean.BlackBean;
import org.itheima.mobilesafe.db.BlackDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

public class CallSmsSafeService extends Service {
	private static final String TAG = "CallSmsSafeService";

	private BlackDao mDao;
	private SmsReceiver mReceiver;

	private TelephonyManager mTm;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mDao = new BlackDao(this);

		// 拦截短信
		mReceiver = new SmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(mReceiver, filter);

		// 拦截电话
		mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// 注册电话状态的监听
		mTm.listen(mCallListener, PhoneStateListener.LISTEN_CALL_STATE);
		Log.d(TAG, "骚扰拦截服务开启");
	}

	private PhoneStateListener mCallListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, final String incomingNumber) {
			// state 当前电话状态
			// incomingNumber 拨入的号码
			// * @see TelephonyManager#CALL_STATE_IDLE:闲置状态
			// * @see TelephonyManager#CALL_STATE_RINGING:响铃状态
			// * @see TelephonyManager#CALL_STATE_OFFHOOK:摘机状态

			if (state == TelephonyManager.CALL_STATE_RINGING) {
				int type = mDao.findType(incomingNumber);
				if (type == BlackBean.TYPE_CALL || type == BlackBean.TYPE_ALL) {
					Log.d(TAG, "拦截了来自:" + incomingNumber + " 的电话");

					Class<?> clazz;
					try {
						// 获得ServiceManager类
						clazz = Class.forName("android.os.ServiceManager");
						// 获得getService方法
						Method method = clazz.getDeclaredMethod("getService",
								String.class);
						IBinder binder = (IBinder) method.invoke(null,
								Context.TELEPHONY_SERVICE);

						// 获得对象
						ITelephony iTelephony = ITelephony.Stub
								.asInterface(binder);
						// 挂断电话
						iTelephony.endCall();

						// 删除电话日志
						final ContentResolver cr = getContentResolver();
						final Uri url = CallLog.Calls.CONTENT_URI;

						cr.registerContentObserver(url, true,
								new ContentObserver(new Handler()) {
									public void onChange(boolean selfChange) {
										String where = CallLog.Calls.NUMBER
												+ "=?";
										String[] selectionArgs = new String[] { incomingNumber };
										cr.delete(url, where, selectionArgs);

										cr.unregisterContentObserver(this);
									};
								});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
	};

	@Override
	public void onDestroy() {
		super.onDestroy();

		// 取消短信拦截拦截
		unregisterReceiver(mReceiver);
		Log.d(TAG, "骚扰拦截服务停止");
	}

	private class SmsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objs = (Object[]) intent.getExtras().get("pdus");

			for (Object obj : objs) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
				String address = sms.getOriginatingAddress();
				String body = sms.getMessageBody();

				int type = mDao.findType(address);
				if (type == BlackBean.TYPE_SMS || type == BlackBean.TYPE_ALL) {
					// 拦截短信
					abortBroadcast();

					Log.d(TAG, "拦截了来自:" + address + "的短信，内容是:" + body);
					// TODO:后续要把信息保存在数据库里
				}
			}
		}
	}
}

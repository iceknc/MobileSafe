package org.itheima.mobilesafe.service;

import org.itheima.mobilesafe.db.NumberAddressDao;
import org.itheima.mobilesafe.view.AddressToast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NumberAddressService extends Service {
	private TelephonyManager mTm;
	private CallInListener mCallInListener;
	private CallOutListener mCallOutListener;
	private AddressToast mToast;

	private static final String TAG = "NumberAddressService";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "号码归属地服务开启");

		mToast = new AddressToast(this);

		mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		// 监听电话拨入状态
		mCallInListener = new CallInListener();
		mTm.listen(mCallInListener, PhoneStateListener.LISTEN_CALL_STATE);

		// 监听电话外拨状态
		mCallOutListener = new CallOutListener();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(mCallOutListener, filter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(TAG, "号码归属地服务关闭");

		// 解除监听
		mTm.listen(mCallInListener, PhoneStateListener.LISTEN_NONE);
		unregisterReceiver(mCallOutListener);
	}

	private class CallInListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// 响铃状态显示号码归属地
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				String address = NumberAddressDao.findAddress(
						NumberAddressService.this, incomingNumber);

				mToast.show(address);
			} else if (state == TelephonyManager.CALL_STATE_IDLE) {
				mToast.hide();
			}
		}
	}

	private class CallOutListener extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			String address = NumberAddressDao.findAddress(
					NumberAddressService.this, number);

			mToast.show(address);
		}

	}
}

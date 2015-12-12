package org.itheima.mobilesafe.receiver;

import org.itheima.mobilesafe.R;
import org.itheima.mobilesafe.utils.Constants;
import org.itheima.mobilesafe.utils.PreferenceUtils;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] objs = (Object[]) intent.getExtras().get("pdus");

		for (Object obj : objs) {
			// obj --> byte[]
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);

			String body = sms.getMessageBody();
			String address = sms.getOriginatingAddress();

			String saveNumber = PreferenceUtils.getString(context,
					Constants.SJFD_SAFE_NUMBER);

			// 判断消息是否是安全号码发来的
			if (address.equals(saveNumber)) {
				if (SmsBody.location.equals(body)) {
					Log.d(TAG, "接收到GPS指令");
					// TODO:
				} else if (SmsBody.wipedata.equals(body)) {
					Log.d(TAG, "接收到擦除数据指令");
					// TODO:
				} else if (SmsBody.lockscreen.equals(body)) {
					Log.d(TAG, "接收到关闭屏幕指令");
					DevicePolicyManager dpm = (DevicePolicyManager) context
							.getSystemService(Context.DEVICE_POLICY_SERVICE);
					// 锁屏
					dpm.lockNow();
					// 切断短信
					abortBroadcast();
				} else if (body.startsWith(SmsBody.lockscreen)) {
					Log.d(TAG, "接收到关闭屏幕并设置新密码的指令");
					DevicePolicyManager dpm = (DevicePolicyManager) context
							.getSystemService(Context.DEVICE_POLICY_SERVICE);
					dpm.lockNow();
					// 取出信息里的密码
					String pwd = body.replace(SmsBody.lockscreen, "");
					// 设置密码
					dpm.resetPassword(pwd,
							DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
					// 切断信息
					abortBroadcast();
				} else if (SmsBody.alarm.equals(body)) {
					Log.d(TAG, "接收到播放报警音乐指令");

					MediaPlayer mp = MediaPlayer.create(context, R.raw.alarm);
					mp.setLooping(true);
					mp.setVolume(1, 1);
					mp.start();

					// 切断信息
					abortBroadcast();
				}
			}
		}
	}

	private interface SmsBody {
		String location = "#*location*#";
		String wipedata = "#*wipedata*#";
		String lockscreen = "#*lockscreen*#";
		String alarm = "#*alarm*#";
	}

}

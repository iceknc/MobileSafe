package org.itheima.mobilesafe.db;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class NumberAddressDao {

	/**
	 * 查询号码归属地
	 * 
	 * @param context
	 * @param number
	 *            电话号码
	 * @return
	 */
	public static String findAddress(Context context, String number) {
		File file = new File(context.getFilesDir(), "address.db");
		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(),
				null, SQLiteDatabase.OPEN_READONLY);

		String address = "";
		boolean isPhone = number.matches("^1[34578]\\d{5,9}$");
		if (isPhone) {
			String prefix = number.substring(0, 7);
			String sql = "select cardtype from info where mobileprefix=?";
			Cursor cursor = db.rawQuery(sql, new String[] { prefix });
			if (cursor != null) {
				if (cursor.moveToNext()) {
					address = cursor.getString(0);
				}
				cursor.close();
			}
		} else {
			switch (number.length()) {
			case 3:
				address = "警报电话";
				break;
			case 4:
				address = "模拟器&短号";
				break;
			case 5:
				address = "服务号码";
				break;
			case 6:
				address = "短号";
				break;
			case 7:
			case 8:
				address = "本地座机";
				break;
			case 10:
			case 11:
			case 12:
				String prefix = number.substring(0, 3);
				String sql = "select city from info where area=?";
				Cursor cursor = db.rawQuery(sql, new String[] { prefix });
				if (cursor != null) {
					if (cursor.moveToNext()) {
						address = cursor.getString(0);
					}
					cursor.close();
				}

				if (TextUtils.isEmpty(address)) {
					prefix = number.substring(0, 4);
					sql = "select city from info where area=?";
					cursor = db.rawQuery(sql, new String[] { prefix });
					if (cursor != null) {
						if (cursor.moveToNext()) {
							address = cursor.getString(0);
						}
						cursor.close();
					}
				}
				break;
			default:
				break;
			}
		}
		db.close();
		if(TextUtils.isEmpty(address)){
			address = "未知号码";
		}
		return address;
	}
}

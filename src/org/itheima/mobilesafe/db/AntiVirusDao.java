package org.itheima.mobilesafe.db;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntiVirusDao {

	public static boolean isVirus(Context context, String md5) {
		String path = new File(context.getFilesDir(), "antivirus.db")
				.getAbsolutePath();

		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);

		String sql = "select count(1) from datable where md5=?";

		Cursor cursor = db.rawQuery(sql, new String[] { md5 });
		int count = 0;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				count = cursor.getInt(0);
			}
			cursor.close();
		}

		db.close();

		return count > 0;
	}

	/**
	 * 0404D4CF63FF7CA4C56BD7700C49536D
	 * 
	 * @param context
	 * @param md5
	 */
	public static void add(Context context, String md5) {

		String path = new File(context.getFilesDir(), "antivirus.db")
				.getAbsolutePath();

		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);

		ContentValues values = new ContentValues();
		values.put("md5", md5);
		values.put("type", 6);
		values.put("name", "超级病毒");
		values.put("desc", "恶意后台扣费,病毒木马程序");
		db.insert("datable", null, values);

		db.close();

	}

}

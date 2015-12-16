package org.itheima.mobilesafe.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class AppLockDao {
	private AppLockDBOpenhelper mHelper;
	private ContentResolver mCr;

	public AppLockDao(Context context) {
		mHelper = new AppLockDBOpenhelper(context);
		mCr = context.getContentResolver();
	}

	/**
	 * 添加
	 * 
	 * @param packageName
	 *            包名
	 * @return 是否添加成功
	 */
	public boolean add(String packageName) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(AppLockDB.TableAppLock.COLUMN_PACKAGE_NAME, packageName);

		long id = db.insert(AppLockDB.TableAppLock.TABLE_NAME, null, values);

		db.close();

		mCr.notifyChange(Uri.parse("content://applock"), null);

		return id != -1;
	}

	/**
	 * 删除
	 * 
	 * @param packageName
	 *            包名
	 * @return 是否删除成功
	 */
	public boolean delete(String packageName) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		String whereClause = AppLockDB.TableAppLock.COLUMN_PACKAGE_NAME + "=?";
		String[] whereArgs = new String[] { packageName };
		int delete = db.delete(AppLockDB.TableAppLock.TABLE_NAME, whereClause,
				whereArgs);

		db.close();

		mCr.notifyChange(Uri.parse("content://applock"), null);

		return delete > 0;
	}

	/**
	 * 判断是否上锁
	 * 
	 * @param packageName
	 * @return
	 */
	public boolean isLock(String packageName) {
		SQLiteDatabase db = mHelper.getReadableDatabase();

		String sql = "select count(1) from " //
				+ AppLockDB.TableAppLock.TABLE_NAME //
				+ " where " //
				+ AppLockDB.TableAppLock.COLUMN_PACKAGE_NAME //
				+ "=?";
		Cursor cursor = db.rawQuery(sql, new String[] { packageName });

		int count = 0;
		if (cursor != null) {
			while (cursor.moveToNext()) {
				count = cursor.getInt(0);
			}
			cursor.close();
		}
		db.close();

		return count > 0;
	}

	/**
	 * 获得全部上锁的包名
	 * 
	 * @param packageName
	 * @return
	 */
	public List<String> findAll() {
		List<String> list = new ArrayList<String>();

		SQLiteDatabase db = mHelper.getReadableDatabase();

		String sql = "select " //
				+ AppLockDB.TableAppLock.COLUMN_PACKAGE_NAME//
				+ " from " //
				+ AppLockDB.TableAppLock.TABLE_NAME;//
		Cursor cursor = db.rawQuery(sql, null);

		if (cursor != null) {
			while (cursor.moveToNext()) {
				list.add(cursor.getString(0));
			}
			cursor.close();
		}
		db.close();

		return list;
	}
}

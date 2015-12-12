package org.itheima.mobilesafe.db;

import java.util.ArrayList;
import java.util.List;

import org.itheima.mobilesafe.bean.BlackBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BlackDao {
	private BlackDBOpenHelper mHelper;

	public BlackDao(Context context) {
		mHelper = new BlackDBOpenHelper(context);
	}

	/**
	 * 添加的方法
	 * 
	 * @param number
	 *            号码
	 * @param type
	 *            拦截类型
	 * @return 添加是否成功
	 */
	public boolean add(String number, int type) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(BlackDB.TableBlack.COLUMN_NUMBER, number);
		values.put(BlackDB.TableBlack.COLUMN_TYPE, type);

		long insert = db.insert(BlackDB.TableBlack.TABLE_NAME, null, values);

		db.close();

		return insert != -1;
	}

	/**
	 * 更新的方法
	 * 
	 * @param number
	 *            号码
	 * @param type
	 *            拦截类型
	 * @return 是否更新成功
	 */
	public boolean update(String number, int type) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(BlackDB.TableBlack.COLUMN_TYPE, type);

		String whereClause = BlackDB.TableBlack.COLUMN_NUMBER + "=?";
		String[] whereArgs = new String[] { number };

		int update = db.update(BlackDB.TableBlack.TABLE_NAME, values,
				whereClause, whereArgs);

		db.close();

		return update > 0;
	}

	/**
	 * 删除的方法
	 * 
	 * @param number
	 *            号码
	 * @return 是否删除成功
	 */
	public boolean delete(String number) {
		SQLiteDatabase db = mHelper.getWritableDatabase();

		String whereClause = BlackDB.TableBlack.COLUMN_NUMBER + "=?";
		String[] whereArgs = new String[] { number };

		int delete = db.delete(BlackDB.TableBlack.TABLE_NAME, whereClause,
				whereArgs);

		db.close();
		return delete > 0;
	}

	/**
	 * 查询全部记录
	 * 
	 * @return 数据表中的全部数据对象
	 */
	public List<BlackBean> queryAll() {
		List<BlackBean> list = new ArrayList<BlackBean>();

		SQLiteDatabase db = mHelper.getReadableDatabase();

		Cursor cursor = db.rawQuery(BlackDB.TableBlack.QUERY_ALL_SQL, null);
		if (cursor != null) {
			while (cursor.moveToNext()) {
				String number = cursor.getString(cursor
						.getColumnIndex(BlackDB.TableBlack.COLUMN_NUMBER));
				int type = cursor.getInt(cursor
						.getColumnIndex(BlackDB.TableBlack.COLUMN_TYPE));

				BlackBean bean = new BlackBean();
				bean.number = number;
				bean.type = type;

				list.add(bean);
			}
			cursor.close();
		}
		db.close();
		return list;
	}

	/**
	 * 分页查询
	 * 
	 * @param pageSize
	 *            每页的数量
	 * @param index
	 *            开始查询的下标
	 * @return 一页对象
	 */
	public List<BlackBean> queryPart(int pageSize, int index) {
		List<BlackBean> list = new ArrayList<BlackBean>();

		SQLiteDatabase db = mHelper.getReadableDatabase();
		// 查询语句
		String sql = "select " //
				+ BlackDB.TableBlack.COLUMN_NUMBER //
				+ "," //
				+ BlackDB.TableBlack.COLUMN_TYPE //
				+ " from " //
				+ BlackDB.TableBlack.TABLE_NAME //
				+ " limit " //
				+ pageSize //
				+ " offset " //
				+ index;

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor != null) {
			while (cursor.moveToNext()) {
				String number = cursor.getString(0);
				int type = cursor.getInt(1);

				BlackBean bean = new BlackBean();
				bean.number = number;
				bean.type = type;

				list.add(bean);
			}
			cursor.close();
		}

		db.close();
		return list;
	}

	/**
	 * 根据号码查询类型
	 * 
	 * @param number
	 *            号码
	 * @return 该号码对应的拦截类型，若返回-1则表示不拦截
	 */
	public int findType(String number) {
		SQLiteDatabase db = mHelper.getReadableDatabase();

		String sql = "select " //
				+ BlackDB.TableBlack.COLUMN_TYPE //
				+ " from " //
				+ BlackDB.TableBlack.TABLE_NAME //
				+ " where " //
				+ BlackDB.TableBlack.COLUMN_NUMBER //
				+ "=?";
		Cursor cursor = db.rawQuery(sql, new String[] { number });

		int type = BlackBean.TYPE_NONE;
		if (cursor != null) {
			if (cursor.moveToNext()) {
				type = cursor.getInt(0);
			}
		}
		cursor.close();
		db.close();
		return type;
	}
}

package org.itheima.mobilesafe.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.itheima.mobilesafe.bean.ChildDataBean;
import org.itheima.mobilesafe.bean.GroupDataBean;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumberDao {
	public static List<GroupDataBean> getDatas(Context context) {
		List<GroupDataBean> groupDatas = new ArrayList<GroupDataBean>();
		File file = new File(context.getFilesDir(), "commonnum.db");

		SQLiteDatabase db = SQLiteDatabase.openDatabase(file.getAbsolutePath(),
				null, SQLiteDatabase.OPEN_READONLY);

		Cursor groupCursor = db.rawQuery(CommonNumberDB.groupDataSQL, null);
		if (groupCursor != null) {
			while (groupCursor.moveToNext()) {
				String title = groupCursor.getString(0);
				String idx = groupCursor.getString(1);

				GroupDataBean groupBean = new GroupDataBean();
				groupBean.title = title;

				groupBean.list = new ArrayList<ChildDataBean>();

				// 查询child数据
				Cursor childCursor = db.rawQuery(CommonNumberDB.childDataSQL
						+ idx, null);
				if (childCursor != null) {
					while (childCursor.moveToNext()) {
						String name = childCursor.getString(0);
						String number = childCursor.getString(1);

						ChildDataBean childBean = new ChildDataBean();

						childBean.name = name;
						childBean.number = number;

						groupBean.list.add(childBean);
					}
					childCursor.close();
				}
				groupDatas.add(groupBean);
			}
			groupCursor.close();
		}
		db.close();
		return groupDatas;
	}
}

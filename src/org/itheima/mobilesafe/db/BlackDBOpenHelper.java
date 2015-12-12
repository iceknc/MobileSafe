package org.itheima.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackDBOpenHelper extends SQLiteOpenHelper {

	public BlackDBOpenHelper(Context context) {
		super(context, BlackDB.NAME, null, BlackDB.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(BlackDB.TableBlack.CREATE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}

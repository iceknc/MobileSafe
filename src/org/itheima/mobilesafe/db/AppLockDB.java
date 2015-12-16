package org.itheima.mobilesafe.db;

public interface AppLockDB {
	String NAME = "applock.db";

	int VERSION = 1;

	public interface TableAppLock {
		String TABLE_NAME = "applock";

		String COLUMN_ID = "_id";

		String COLUMN_PACKAGE_NAME = "package_name";

		String CREATE_TABLE_SQL = "create table " //
				+ TABLE_NAME //
				+ "(" //
				+ COLUMN_ID //
				+ " integer primary key autoincrement,"//
				+ COLUMN_PACKAGE_NAME //
				+ " text unique)";
	}
}

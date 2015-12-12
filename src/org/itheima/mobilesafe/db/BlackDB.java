package org.itheima.mobilesafe.db;

/**
 * 黑名单的数据库
 * 
 * @author Administrator
 * 
 */
public interface BlackDB {
	String NAME = "black.db";
	int VERSION = 1;

	public interface TableBlack {
		/**
		 * 表名
		 */
		String TABLE_NAME = "black";
		/**
		 * 主键
		 */
		String COLUMN_ID = "_id";
		/**
		 * 号码
		 */
		String COLUMN_NUMBER = "number";
		/**
		 * 拦截模式
		 */
		String COLUMN_TYPE = "type";

		/**
		 * 建表的SQL
		 */
		String CREATE_TABLE_SQL = "create table " //
				+ TABLE_NAME //
				+ "(" //
				+ COLUMN_ID //
				+ " integer primary key autoincrement,"//
				+ COLUMN_NUMBER //
				+ " text uniqie," //
				+ COLUMN_TYPE //
				+ " integer)";

		// 查询全部数据的SQL
		String QUERY_ALL_SQL = "select "//
				+ COLUMN_NUMBER//
				+ ","//
				+ COLUMN_TYPE//
				+ " from "//
				+ TABLE_NAME;
	}
}

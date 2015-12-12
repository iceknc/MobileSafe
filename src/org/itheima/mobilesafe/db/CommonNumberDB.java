package org.itheima.mobilesafe.db;

public interface CommonNumberDB {
	/**
	 * 常用号码的groupData表查询SQL
	 */
	public String groupDataSQL = "select name,idx from classlist";
	
	/**
	 * 常用号码的childData表查询SQL，要加上第"n"个表
	 */
	public String childDataSQL = "select name,number from table";
}

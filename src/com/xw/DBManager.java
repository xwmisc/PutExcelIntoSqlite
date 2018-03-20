package com.xw;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DBManager {
	Connection m_Connection;
	Statement m_Statement;
	HashMap<String, HashSet<String>> columnCache;
	public static boolean LOG_STATE = false;

	public static void main(String[] args) {
		try {
			DBManager dbm = new DBManager("test.db");
//			dbm.cleanTable(Config.TABLE_2);
//			ResultSet rSet = dbm.m_Statement.executeQuery("select name from sqlite_master where type='table'");
//
//			while (rSet.next()) {
//				System.out.println(rSet.getString(1));
//			}
//			if(1==1)return;
			
			System.out.println("=====before");
			dbm.test("test");
			System.out.println("=====put");
			HashMap<String, String> kv = new HashMap<>();
			for (int i = 0; i < 12; i++) {
				for (int j = 0; j < 6; j++)
					kv.put("数据" + j, "value-" + j);
				dbm.put("test", kv);
			}
			dbm.test("test");
			System.out.println("=====modify");
			HashMap<String, String> condition = new HashMap<>();
			condition.put("数据1", "value-1");
			kv = new HashMap<>();
			kv.put("数据1", "value-x");
			dbm.modify("test", condition, kv);
			dbm.test("test");
			System.out.println("=====query");
			condition = new HashMap<>();
			condition.put("数据2", "value-2");
			for (HashMap<String, String> j : dbm.query("test", condition, new String[] { "数据1" }))
				for (String k : j.keySet())
					System.out.println("row:" + j.toString() + "|key:" + k + "|value:" + j.get(k));
			System.out.println("=====delete");
			condition = new HashMap<>();
			condition.put("数据2", "value-2");
			dbm.delete("test", condition);
			dbm.test("test");
			System.out.println("=====cleanTable");
			dbm.cleanTable("test");
			dbm.test("test");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	boolean hasColumn(String tableName, String columnName) throws SQLException {
		ResultSet rSet = m_Statement.executeQuery("select * from " + tableName + " limit 0");
		try {
			rSet.findColumn(columnName);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	void addColumn(String tableName, String columnName) throws Exception {
		String sql = "alter table " + tableName + " add column '" + columnName + "' text";
		if(LOG_STATE)System.out.println("sql " + sql);
		m_Statement.executeUpdate(sql);
		Config.getInstance().addColumnConfig(tableName, columnName);
	}

	void initColumnCache() throws Exception {
		columnCache = new HashMap<>();
		columnCache.put("test", Config.getInstance().getColumnConfig("test"));
		columnCache.put(Config.TABLE_1, Config.getInstance().getColumnConfig(Config.TABLE_1));
		columnCache.put(Config.TABLE_2, Config.getInstance().getColumnConfig(Config.TABLE_2));
	}

	String jointSQL(HashMap<String, String> hashmap) {
		String sql = "";
		if (null != hashmap) {
			for (String i : hashmap.keySet()) {
				sql = sql + "" + i + "='" + hashmap.get(i) + "' and ";
			}
		}
		return sql.substring(0, sql.length() - "and ".length());
	}

	public DBManager(String FileName) throws Exception {
		Class.forName("org.sqlite.JDBC");
		m_Connection = DriverManager.getConnection("jdbc:sqlite:" + FileName);
		m_Statement = m_Connection.createStatement();
		cleanTable("test");
		initColumnCache();

	}

	public void put(String tableName, HashMap<String, String> kv) throws Exception {
		String sql1 = "insert into " + tableName + "(";
		String sql2 = ") values(";
		String sql3 = ")";

		for (String i : kv.keySet()) {
			if (columnCache.get(tableName).add(i))
				addColumn(tableName, i);
			sql1 = sql1 + i + ",";
			sql2 = sql2 + "'" + kv.get(i) + "',";
		}
		if(LOG_STATE)System.out.println("sql " + sql1.substring(0, sql1.length() - 1) + sql2.substring(0, sql2.length() - 1) + sql3);
		m_Statement.executeUpdate(sql1.substring(0, sql1.length() - 1) + sql2.substring(0, sql2.length() - 1) + sql3);
	}

	public void modify(String tableName, HashMap<String, String> condition, HashMap<String, String> kv)
			throws Exception {
		String sql = "update " + tableName + " set " + jointSQL(kv) + " where " + jointSQL(condition);
		if(LOG_STATE)System.out.println("sql " + sql);
		m_Statement.executeUpdate(sql);
	}

	public void delete(String tableName, HashMap<String, String> condition) throws SQLException {
		String sql = "delete from " + tableName + " where " + jointSQL(condition);
		if(LOG_STATE)System.out.println("sql " + sql);
		m_Statement.executeUpdate(sql);
	}

	public ArrayList<HashMap<String, String>> query(String tableName, HashMap<String, String> condition,
			String[] columnName) throws SQLException {
		String sql = "select * from " + tableName + ((null == condition) ? "" : " where " + jointSQL(condition));

		ArrayList<HashMap<String, String>> result = new ArrayList<>();
		if(LOG_STATE)System.out.println("sql " + sql);
		ResultSet rSet = m_Statement.executeQuery(sql);
		while (rSet.next()) {
			HashMap<String, String> aRow = new HashMap<>();
			for (String i : columnName) {
				aRow.put(i, rSet.getString(i));
			}
			result.add(aRow);
		}
		rSet.close();
		return result;
	}

	public void cleanTable(String tableName) throws Exception {
		m_Statement.executeUpdate("drop table if exists " + tableName);
		m_Statement.executeUpdate("create table " + tableName + "(id INTEGER PRIMARY KEY autoincrement)");
		Config.getInstance().cleanColumnConfig(tableName);
		initColumnCache();
	}

	public void closeDB() throws SQLException {
		m_Connection.close();// 关闭数据库连接
	}

	public void test(String tableName) throws Exception {
		System.out.println("=======" + tableName + "=======");
		ResultSet rSet = m_Statement.executeQuery("select * from " + tableName);
		int i = 1;
		try {
			while (true) {
				rSet.getString(i);
				i += 1;
			}
		} catch (SQLException e) {
		}
		if (i == 1) {
			System.out.println("no data");
			return;
		}
		while (rSet.next()) {
			for (int j = 1; j < i; j++) {
				System.out.print(rSet.getString(j) + "|");
			}
			System.out.println("");
		}

		System.out.println("=======end=======");
	}

}

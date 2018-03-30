package com.xw;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.xw.exception.DBException;

public class DBManager {
	static DBManager m_DBManager = null;
	Connection m_Connection;
	Statement m_Statement;
	HashMap<String, HashSet<String>> columnCache;
	public static boolean LOG_STATE = false;

	public static void main(String[] args) {
		try {
			DBManager dbm = new DBManager("test.db");

			System.out.println("=====before");
			dbm.test("test");

			// put test
			System.out.println("=====put");
			HashMap<String, String> kv = new HashMap<>();
			for (int i = 0; i < 12; i++) {
				for (int j = 0; j < 6; j++)
					kv.put("数据" + j, "value-" + j);
				dbm.put("test", kv);
			}
			dbm.addColumn("test", "columnName");
			dbm.addColumn("test", "columnName");

			// put time-consuming test
			System.out.println("=====x");
			Date date = new Date();
			String sql1 = "insert into test(数据3,数据4) values('sdasdas','ffffff');";
			sql1 = sql1 + sql1;
			sql1 = sql1 + sql1;
			sql1 = sql1 + sql1;
			sql1 = sql1 + sql1;
			sql1 = sql1 + sql1;
			System.out.println("sql|" + sql1);
			System.out.println("xx1|" + (new Date().getTime() - date.getTime()));
			dbm.m_Statement.executeUpdate(sql1);
			dbm.m_Connection.commit();
			System.out.println("xx2|" + (new Date().getTime() - date.getTime()));
			System.out.println("=====xend");
			dbm.test("test");

			// modify test
			System.out.println("=====modify");
			HashMap<String, String> condition = new HashMap<>();
			condition.put("数据1", "value-1");
			kv = new HashMap<>();
			kv.put("数据1", "value-x");
			dbm.modify("test", condition, kv);
			dbm.test("test");

			// query test
			System.out.println("=====query");
			condition = new HashMap<>();
			condition.put("数据2", "value-2");
			for (HashMap<String, String> j : dbm.query("test", condition, new String[] { "数据1" }))
				for (String k : j.keySet())
					System.out.println("row:" + j.toString() + "|key:" + k + "|value:" + j.get(k));

			// delete test
			System.out.println("=====delete");
			condition = new HashMap<>();
			condition.put("数据2", "value-2");
			dbm.delete("test", condition);
			dbm.test("test");

			// cleanTable test
			System.out.println("=====cleanTable");
			dbm.cleanTable("test");
			dbm.test("test");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	boolean hasColumn(String tableName, String columnName) {
		ResultSet rSet = null;
		try {
			rSet = m_Statement.executeQuery("select * from " + tableName + " limit 0");
			rSet.findColumn(columnName);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	void addColumn(String tableName, String columnName) throws IOException {
		String sql = "alter table " + tableName + " add column '" + columnName + "' text";
		if (LOG_STATE)
			System.out.println("sql " + sql);
		boolean stateOK = false;
		try {
			m_Statement.executeUpdate(sql);
			stateOK = true;
		} catch (SQLException e) {
			stateOK = false;
		}
		if (stateOK)
			Config.getInstance().addColumnConfig(tableName, columnName);
	}

	void initColumnCache() {
		columnCache = new HashMap<>();
		columnCache.put("test", Config.getInstance().getColumnConfig("test"));
		columnCache.put(Config.TABLE_1, Config.getInstance().getColumnConfig(Config.TABLE_1));
		columnCache.put(Config.TABLE_2, Config.getInstance().getColumnConfig(Config.TABLE_2));
		columnCache.put(Config.TABLE_3, Config.getInstance().getColumnConfig(Config.TABLE_3));
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

	public static DBManager getInstance() throws DBException, IOException {
		if (null == m_DBManager) {
			m_DBManager = new DBManager("test");
		}
		return m_DBManager;
	}

	private DBManager(String FileName) throws DBException, IOException {
		try {
			Class.forName("org.sqlite.JDBC");
			m_Connection = DriverManager.getConnection("jdbc:sqlite:" + FileName);
			m_Connection.setAutoCommit(false);
			m_Statement = m_Connection.createStatement();
			cleanTable("test");
			initColumnCache();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e.getMessage());
		}

	}

	public void put(String tableName, HashMap<String, String> kv) throws DBException, IOException {

		String sql1 = "insert into " + tableName + "(";
		String sql2 = ") values(";
		String sql3 = ")";

		for (String i : kv.keySet()) {
			if (columnCache.get(tableName).add(i))
				addColumn(tableName, i);
			sql1 = sql1 + i + ",";
			sql2 = sql2 + "'" + kv.get(i) + "',";
		}
		String sql = sql1.substring(0, sql1.length() - 1) + sql2.substring(0, sql2.length() - 1) + sql3;
		if (LOG_STATE)
			System.out.println("sql " + sql);
		try {
			m_Statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e.getMessage());
		}
	}

	public void modify(String tableName, HashMap<String, String> condition, HashMap<String, String> kv)
			throws DBException {
		String sql = "update " + tableName + " set " + jointSQL(kv) + " where " + jointSQL(condition);
		if (LOG_STATE)
			System.out.println("sql " + sql);
		try {
			m_Statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e.getMessage());
		}
	}

	public void delete(String tableName, HashMap<String, String> condition) throws DBException {
		String sql = "delete from " + tableName + " where " + jointSQL(condition);
		if (LOG_STATE)
			System.out.println("sql " + sql);
		try {
			m_Statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e.getMessage());
		}
	}

	public ArrayList<HashMap<String, String>> query(String tableName, HashMap<String, String> condition,
			String[] columnName) throws DBException {
		String sql = "select * from " + tableName + ((null == condition) ? "" : " where " + jointSQL(condition));

		ArrayList<HashMap<String, String>> result = new ArrayList<>();
		if (LOG_STATE)
			System.out.println("sql " + sql);
		ResultSet rSet;
		try {
			rSet = m_Statement.executeQuery(sql);
			while (rSet.next()) {
				HashMap<String, String> aRow = new HashMap<>();
				for (String i : columnName) {
					aRow.put(i, rSet.getString(i));
				}
				result.add(aRow);
			}
			rSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e.getMessage());
		}
		return result;
	}

	public HashSet<String> getColumnNames(String tableName) {
		return columnCache.get(tableName);
	}

	public void cleanTable(String tableName) throws IOException, DBException {
		try {
			m_Statement.executeUpdate("drop table if exists " + tableName);
			m_Statement.executeUpdate("create table " + tableName + "(id INTEGER PRIMARY KEY autoincrement)");
			Config.getInstance().cleanColumnConfig(tableName);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e.getMessage());
		}
		initColumnCache();
	}

	public void closeDB() throws DBException {
		try {
			m_Connection.commit();// 提交
			m_Connection.close();// 关闭数据库连接
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e.getMessage());
		}
	}

	public void commit() throws DBException {
		try {
			m_Connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e.getMessage());
		} // 提交
	}

	public void test(String tableName) throws DBException {
		try {
			System.out.println("=======" + tableName + "=======");
			ResultSet rSet;
			rSet = m_Statement.executeQuery("select * from " + tableName);
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
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DBException(e.getMessage());
		}
	}

}

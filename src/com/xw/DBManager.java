package com.xw;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

import jxl.*;
import org.sqlite.JDBC;

public class DBManager {
	Connection m_Connection;
	Statement m_Statement;
	HashSet<String> column = new HashSet<>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			ExcelAPI excel = new ExcelAPI(new File("E:\\sw-default\\eclipse\\myworkspace\\ConvetExcelToSqlite\\test.xls"));
			DBManager dbm = new DBManager();
			dbm.connectDB();//connect to db
			
			HashMap<String,String> kv = new HashMap<>();
			kv.put("aa", "bb");
			dbm.put(kv);//put data from HashMap
			
			HashMap<String,String> condition = new HashMap<>();
			condition.put("aa", "bb");//find record with aa='bb'
			HashMap<String,String> result = new HashMap<>();
			result.put("id", "");//find attribute 'id'
			dbm.query(kv,result);//find record attribute 'id' with condition aa='bb'

			for(String i : result.keySet()) {
				System.out.println("record "+i+": "+result.get(i));
			}
			
			dbm.closeDB();//close db
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void connectDB() throws Exception {
		Class.forName("org.sqlite.JDBC");
		m_Connection = DriverManager.getConnection("jdbc:sqlite:test.db");
		m_Statement=m_Connection.createStatement();
		m_Statement.executeUpdate("drop table if exists test");
		m_Statement.executeUpdate("create table test(id INTEGER PRIMARY KEY autoincrement)");
	}
	
	public void closeDB() throws SQLException {
		m_Connection.close();//关闭数据库连接
	}

	public void put(HashMap<String,String> kv) throws Exception {
		String sql1 = "insert into test(";
		String sql2 = ") values('";
		String sql3 = "')";
		for(String i : kv.keySet()) {
			if(column.add(i)) addColumn(i);
			System.out.println("putKV "+i+":"+kv.get(i));
			sql1=sql1+i+",";
			sql2=sql2+kv.get(i)+",";
		}
		m_Statement.executeUpdate(
				sql1.substring(0, sql1.length()-1)+
									sql2.substring(0, sql2.length()-1)+
									sql3);
	} 
	
	public void modify(HashMap<String,String> condition,HashMap<String,String> kv) throws Exception {
		
		String sql = "";
		for(String i : condition.keySet()) {
			sql=sql+i+"="+kv.get(i) +" and";
		}
		ResultSet rSet = m_Statement.executeQuery("select * from test where "+
				sql.substring(0, sql.length()-"and".length()));
		while(rSet.next()) {
			rSet.getInt(0);
			for(String i : kv.keySet()) {
				m_Statement.executeUpdate("update test set '"+i+"' = '"+kv.get(i)+"'");
			}
		}
	} 
	
	public void delete(HashMap<String,String> kv) throws SQLException {
		String sql = "";
		for(String i : kv.keySet()) {
			sql=sql+i+"="+kv.get(i) +" and";
		}
		m_Statement.executeQuery("delete from test where "+
				sql.substring(0, sql.length()-"and".length()));
	}
	
	
	void addColumn(String col) throws SQLException {
		m_Statement.executeUpdate("alter table test add column "+col+" text");
		System.out.println("addColumn "+col);
	}

	public HashMap<String,String> query(HashMap<String,String> condition,HashMap<String,String> kv) throws SQLException{
		String sql = "";
		for(String i : condition.keySet()) {
			sql=sql+i+"='"+condition.get(i) +"' and";
		}
		
		ResultSet rSet;

		rSet = sql.equals("") ?
				m_Statement.executeQuery("select * from test") :
				m_Statement.executeQuery("select * from test where "+
				sql.substring(0, sql.length()-"and".length()));
		while (rSet.next()) {
			for(String i : kv.keySet()) {
				kv.replace(i, rSet.getString(i));
			}
		}
		rSet.close();
		return kv;
	}
	
//	public void put(String key,String attribute,String value) throws Exception {
//		if(keyCache.add(key)) {
//			ResultSet rSet = m_Statement.executeQuery("select * from test where key = '"+key+"'");
//			if(!rSet.next()) {
//				//insert key
//				m_Statement.executeUpdate("insert into test(key) values('"+key+"')");
//			}
//			rSet.close();
//		}
//		//update value
//		m_Statement.executeUpdate("update test set '"+attribute+"' = '"+value+"'");
//	}
	
//	public ResultSet query(String condition) throws Exception {
//		ResultSet rSet = m_Statement.executeQuery("select * from test where "+condition);
//		rSet.
//		return rSet;
//	}
	

}

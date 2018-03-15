package com.xw;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jxl.*;
import org.sqlite.JDBC;

public class MainClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			ExcelAPI excel = new ExcelAPI(new File("E:\\sw-default\\eclipse\\myworkspace\\ConvetExcelToSqlite\\test.xls"));
			
			Connection connection = connectDB();
			Statement statement=connection.createStatement();
			statement.executeUpdate("drop table if exists test");
			statement.executeUpdate("create table test(key text,attr1 text,attr2 text,attr3 text,attr4 text)");
			
			for(int i=0;!excel.read(i, 0).equals("");i++) {
				statement.executeUpdate("insert into test values('"+
						excel.read(i, 0)+"','"+
						excel.read(i, 1)+"','"+
						excel.read(i, 2)+"','"+
						excel.read(i, 3)+"','"+
						excel.read(i, 4)+"')");
			}
			
			 
			ResultSet rSet=statement.executeQuery("select*from test");//搜索数据库，将搜索的放入数据集ResultSet中
			while (rSet.next()) {
			    System.out.println("key："+rSet.getString(1));
			for(int i=1;i<=3;i++) {
			    System.out.println("attr"+i+"："+rSet.getString(i+1));
				 }
			}
			rSet.close();//关闭数据集
			connection.close();//关闭数据库连接
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static Connection connectDB() throws Exception {
		Class.forName("org.sqlite.JDBC");
		return DriverManager.getConnection("jdbc:sqlite:test.db");
	}

}

package com.xw;

import java.io.File;
import java.io.IOException;

import jxl.*;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExcelAPI {
	Workbook m_Workbook;
	Sheet m_Sheet;

	public ExcelAPI(File file,int sheetIndex) throws Exception {
		
		m_Workbook = Workbook.getWorkbook(file);
		m_Sheet =  m_Workbook.getSheet(sheetIndex);
	}
	public ExcelAPI(File file) throws Exception {
		this(file, 0);
	}

	//public void write(int row,int col,String text) throws Exception {
		//m_Sheet.addCell(new Label(col,row,text));
	//}
	public String read(int row,int col) {
		if(col>=m_Sheet.getColumns())return "";
		if(row>=m_Sheet.getRows())return "";
		return m_Sheet.getCell(col,row).getContents();
	}
}

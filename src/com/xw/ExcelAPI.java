package com.xw;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

import jxl.*;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.NumberRecord;
import jxl.write.biff.RowsExceededException;

public class ExcelAPI {
	Workbook m_Workbook;
	WritableWorkbook m_WritableWorkbook;
	WritableSheet m_WritableSheet;
	File tempFile;

	public static void main(String[] arg) {
		try {
			ExcelAPI excel = new ExcelAPI("E:\\360Download\\0301.xls");
			System.out.println("=====SheetList");
			for (String i : excel.getSheetList())
				System.out.println(i);
			System.out.println("=====openSheet");
			excel.openSheet(excel.getSheetList()[0]);
			System.out.println("=====Read");
			System.out.println("3x1|" + excel.read(3, 1));
			System.out.println("=====ReadFormat");
			System.out.println("3x1|" + excel.read(3, 1, true));
			System.out.println("=====Write");
			excel.write(3, 1, "100");
			System.out.println("3x1|" + excel.read(3, 1, true));
			System.out.println("=====save");
			excel.save();
			System.out.println("=====close");
			excel.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ExcelAPI(String filePath) throws Exception {
		File file = new File(filePath);
		if(!file.exists()){
			m_WritableWorkbook=Workbook.createWorkbook(file);
			m_WritableSheet = m_WritableWorkbook.createSheet("Sheet1", 0);
			return;
		}
		m_Workbook = Workbook.getWorkbook(file);
		tempFile = new File(filePath + "~");
		m_WritableWorkbook = Workbook.createWorkbook(tempFile, m_Workbook);
	}

	public void openSheet(String name) {
		m_WritableSheet = m_WritableWorkbook.getSheet(name);
	}

	public String[] getSheetList() {
		return m_WritableWorkbook.getSheetNames();
	}

	public void write(int row, int col, String text) throws Exception {
		m_WritableSheet.addCell(new Label(col, row, text));
	}

	public void save() throws IOException {
		m_WritableWorkbook.write();
	}

	public void close() throws Exception {
		m_WritableWorkbook.close();
		if(null!=m_Workbook)m_Workbook.close();
		if(null!=tempFile && tempFile.exists())tempFile.deleteOnExit();
	}

	//调整数值类型的单元格格式,不要逗号
	public String read(int row, int col, boolean formatValue) throws IOException {
		if (col >= m_WritableSheet.getColumns() || row >= m_WritableSheet.getRows())
			return "";

		if (formatValue) {
			try {
				WritableCell cell = m_WritableSheet.getWritableCell(col, row);
				BigDecimal big = new BigDecimal(((NumberCell) cell).getValue());
				return big.toString();
			} catch (ClassCastException e) {
				return m_WritableSheet.getWritableCell(col, row).getContents();
			}
		}
		return m_WritableSheet.getCell(col, row).getContents();
	}

	public String read(int row, int col) throws IOException {
		return read(row, col, false);
	}
}

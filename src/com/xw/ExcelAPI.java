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

	public static final String DIV = "|";
	public static final String NEW_LINE = "\n";

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
			excel.writeFormat(3, 1, "xxx" + DIV + "200" + DIV + NEW_LINE + "asf" + DIV + "w" + DIV + DIV + "2");
			excel.write(0, 0, "100" + DIV + "200" + DIV + NEW_LINE + "asf" + DIV + "w" + DIV + DIV + "2");
			System.out.println("0x0|" + excel.read(0, 0, false));
			System.out.println("3x1|" + excel.read(3, 1, false));
			excel.save();
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
		if (!file.exists()) {
			m_WritableWorkbook = Workbook.createWorkbook(file);
			m_WritableSheet = m_WritableWorkbook.createSheet("Sheet1", 0);
			return;
		}
		m_Workbook = Workbook.getWorkbook(file);
		tempFile = new File(filePath + "~");
		tempFile.createNewFile();
		m_WritableWorkbook = Workbook.createWorkbook(tempFile, m_Workbook);
	}

	public void openSheet(String name) {
		m_WritableSheet = m_WritableWorkbook.getSheet(name);
	}

	public String[] getSheetList() {
		return m_WritableWorkbook.getSheetNames();
	}

	public void write(int row, int col, String text) throws Exception {
		//System.out.println("write " + row + " " + col + text);
		if (col >= m_WritableSheet.getColumns() || row >= m_WritableSheet.getRows()) {
			m_WritableSheet.addCell(new Label(col, row, text));
		} else if (m_WritableSheet.getWritableCell(col, row).getType() == CellType.LABEL) {
			((Label) m_WritableSheet.getWritableCell(col, row)).setString(text);
		}
	}

	public void writeFormat(int base_row, int base_col, String text) throws Exception {
		// System.out.println("writeFormat " + text);
		int row = 0;
		int col = 0;
		String[] row_list = text.split(NEW_LINE);
		for (String a_row : row_list) {
			// System.out.println("row_list " + row_list);
			String[] cell_list = a_row.split("\\" + DIV);
			col = 0;
			for (String a_cell : cell_list) {
				// System.out.println("cell_list " + cell_list);
				write(base_row + row, base_col + col, a_cell);
				col++;
			}
			row++;
		}
	}

	public void save() throws IOException {
		m_WritableWorkbook.write();
	}

	public void close() throws Exception {

		m_WritableWorkbook.close();
		if (null != m_Workbook)
			m_Workbook.close();
		if (null != tempFile && tempFile.exists())
			tempFile.deleteOnExit();
	}

	// 调整数值类型的单元格格式,不要逗号
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

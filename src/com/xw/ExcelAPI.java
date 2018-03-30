package com.xw;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xw.exception.ExcelException;

import jxl.*;
import jxl.format.CellFormat;
import jxl.read.biff.BiffException;
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
	public static final String NEW_LINE = "newline";

	public static void main(String[] arg) {
		try {
			ExcelAPI excel = new ExcelAPI("E:\\360Download\\record.xls");
			System.out.println("=====SheetList");
			for (String i : excel.getSheetList())
				System.out.println(i);
			System.out.println("=====openSheet");
			excel.openSheet(excel.getSheetList()[0]);
			System.out.println("=====Read");
			System.out.println("3x1|" + excel.read(3, 1));
			System.out.println("=====ReadDate");
			try {
				System.out.println("0x0|" + excel.readDate(0, 0));
			} catch (ClassCastException e) {
				System.out.println("=====ReadDateFix"
						+ new Date((long) ((excel.readNumber(0, 0) - 25568 - 1) * 1000l * 60 * 60 * 24)).toGMTString());
				// Matcher m = Pattern.compile("([0-9]+)月([0-9]+)日").matcher(excel.read(0, 0));
				// System.out.println("=====ReadDateFix"+excel.read(0, 0));
				// System.out.println(m.group(1)+"月"+m.group(2)+"日");

			}
			System.out.println("=====ReadNumber");
			System.out.println("3x1|" + excel.readNumber(3, 1));
			System.out.println("=====Write");
			excel.writeFormat(1, 1,
					"ben|0226|刷货开工费|-10000000|0|刷货差额|刷货退回|0|-4577150|刷货返点|刷货费用|刷货入库|0|-965360|0303|刷货开工费|刷货差额|刷货退回|刷货返点|刷货费用|刷货入库|0205|刷货开工费|刷货差额|刷货退回|刷货返点|刷货费用|刷货入库|0227|刷货开工费|刷货差额|刷货退回|刷货返点|刷货费用|刷货入库|0202|刷货开工费|刷货差额|刷货退回|刷货返点|刷货费用|刷货入库|0224|刷货开工费|刷货差额|刷货退回|刷货返点|刷货费用|刷货入库|0301|刷货开工费|刷货差额|刷货退回|刷货返点|刷货费用|刷货入库|0203|刷货开工费|-19900000|0|刷货差额|刷货退回|0|-887250|刷货返点|刷货费用|刷货入库|0|-3555744|0225|刷货开工费|-50000000|0|刷货差额|刷货退回|0|-2710600|刷货返点|刷货费用|");
			System.out.println("1x1|" + excel.read(1, 1));
			System.out.println("1x3|" + excel.read(1, 3));
			excel.save();
			System.out.println("1x4|" + excel.readNumber(1, 4));
			System.out.println("=====save");
			excel.save();
			System.out.println("=====close");
			excel.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ExcelAPI(String filePath, String[] newSheetName) throws IOException, ExcelException {

		if (null == filePath)
			throw new IllegalArgumentException("filename is null");
		File file = new File(filePath);

		if (!file.exists()) {
			m_WritableWorkbook = Workbook.createWorkbook(file);
			if (null == newSheetName)
				throw new IllegalArgumentException("newSheetName is null");

			for (int i = 0; i < newSheetName.length; i++)
				m_WritableWorkbook.createSheet(newSheetName[i], i);

			openSheet(newSheetName[0]);
		} else {
			try {
				m_Workbook = Workbook.getWorkbook(file);
			} catch (BiffException e) {
				e.printStackTrace();
				throw new ExcelException(e.getMessage());
			}
			tempFile = new File(filePath + "~");
			m_WritableWorkbook = Workbook.createWorkbook(tempFile, m_Workbook);
		}
	}

	public ExcelAPI(String filePath) throws IOException, ExcelException {
		this(filePath, new String[] { "sheet1" });
	}

	public void openSheet(String name) {
		m_WritableSheet = m_WritableWorkbook.getSheet(name);
	}

	public String[] getSheetList() {
		return m_WritableWorkbook.getSheetNames();
	}

	public void write(int row, int col, String text) throws ExcelException {
		try {
			m_WritableSheet.addCell(new Label(col, row, text));
		} catch (WriteException e) {
			e.printStackTrace();
			throw new ExcelException(e.getMessage());
		}

		if (m_WritableSheet.getWritableCell(col, row).getType() == CellType.LABEL) {
			((Label) m_WritableSheet.getWritableCell(col, row)).setString(text);
		}
	}

	public void writeFormat(int base_row, int base_col, String text) throws ExcelException {
		// System.out.println("writeFormat " + text);
		int row = 0;
		int col = 0;
		String[] row_list = text.split(NEW_LINE);
		for (String a_row : row_list) {
			String[] cell_list = a_row.split("\\" + DIV);
			col = 0;
			for (String a_cell : cell_list) {
				write(base_row + row, base_col + col, a_cell);
				col++;
			}
			row++;
		}
	}

	public void save() throws IOException {
		m_WritableWorkbook.write();
	}

	public void close() throws ExcelException, IOException {
		try {
			m_WritableWorkbook.close();
		} catch (WriteException e) {
			e.printStackTrace();
			throw new ExcelException(e.getMessage());
		}
		if (null != m_Workbook)
			m_Workbook.close();
		if (null != tempFile && tempFile.exists())
			tempFile.delete();
	}

	public void freezeRow() {
		m_WritableSheet.getSettings().setVerticalFreeze(1);
	}

	public String read(int row, int col) throws IOException {
		if (col >= m_WritableSheet.getColumns() || row >= m_WritableSheet.getRows())
			return "";

		return m_WritableSheet.getCell(col, row).getContents();
	}

	public double readNumber(int row, int col) throws IOException, ExcelException {
		if (col >= m_WritableSheet.getColumns() || row >= m_WritableSheet.getRows())
			return 0;

		WritableCell cell = m_WritableSheet.getWritableCell(col, row);
		double num;
		try {
			num = ((NumberCell) cell).getValue();
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ExcelException(e.getMessage());
		}
		return num;
	}

	public Date readDate(int row, int col) throws IOException, ExcelException {
		if (col >= m_WritableSheet.getColumns() || row >= m_WritableSheet.getRows())
			return null;
		// Calendar calendar = Calendar.getInstance();
		// System.out.println("22|"+calendar.get(Calendar.DATE));

		WritableCell cell = m_WritableSheet.getWritableCell(col, row);
		Date date;
		if (cell.getClass().equals(Number.class)) {
			date = new Date((long) ((readNumber(row, col) - 25568 - 1) * 1000l * 60 * 60 * 24));
		} else {
			date = ((DateCell) cell).getDate();
		}
		return date;
	}
}

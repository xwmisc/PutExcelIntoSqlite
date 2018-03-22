package com.xw;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MainClass {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			final String PATH_RECORD = "C:\\test\\result.xls";
			final String PATH_ACCOUNT_FLODER = "C:\\workspace\\github\\PutExcelIntoSqlite\\对账2月3月";
			final String PATH_DISCOUNT = "C:\\workspace\\github\\PutExcelIntoSqlite\\对账2月3月\\往来对账数据-2018_03_22-14_35_01.xls";

			File record_file = new File(PATH_RECORD);
			if (record_file.exists())
				record_file.delete();

			ExcelAPI record = new ExcelAPI(record_file.getAbsolutePath());
			String[] sheetlist = record.getSheetList();
			record.openSheet(record.getSheetList()[0]);
			record.writeFormat(0, 0, "姓名" + ExcelAPI.DIV + "日期" + ExcelAPI.DIV + "款项类型" + ExcelAPI.DIV + "应收增加差额"
					+ ExcelAPI.DIV + "应收减少差额");			
			
			record.openSheet(record.getSheetList()[1]);
			record.writeFormat(0, 0, "日期" + ExcelAPI.DIV + "姓名" + ExcelAPI.DIV + "应收增加合计" + ExcelAPI.DIV + "营收减少合计");		
			
			
			String excel_format = "";
			int row = 1;
			int row_res2 = 1;

			DBManager dbm = new DBManager("test.db");
			dbm.cleanTable(Config.TABLE_1);
			dbm.cleanTable(Config.TABLE_2);
			for (File file : new File(PATH_ACCOUNT_FLODER).listFiles()) {
				if (file.getName().matches("[0-9]{4}折扣表.xls$")) {
					System.out.println(file.getAbsolutePath());
					addRecord1(dbm, file.getAbsolutePath());
				}
			}

			dbm.test(Config.TABLE_1);
			addRecord2(dbm, PATH_DISCOUNT);
			dbm.test(Config.TABLE_2);

			String[] type = new String[] { "刷货开工费", "刷货差额", "刷货退回", "刷货返点", "刷货费用", "刷货入库" };
			String[] item = new String[] { "姓名", "款项类型", "日期", "应收增加", "应收减少" };
			// for(String i: Config.getInstance().getStaffSet())

			HashMap<String, BigDecimal> count1 = new HashMap<>();
			count1.put("应收增加", new BigDecimal(0));
			count1.put("应收减少", new BigDecimal(0));
			HashMap<String, BigDecimal> count2 = new HashMap<>();
			count2.put("应收增加", new BigDecimal(0));
			count2.put("应收减少", new BigDecimal(0));

			HashSet<String> day_set = new HashSet<>();
			for (HashMap<String, String> day : dbm.query(Config.TABLE_1, null, new String[] { "日期" }))
				day_set.add(day.get("日期"));
			// for (HashMap<String, String> day : dbm.query(Config.TABLE_2, null, new
			// String[] { "日期" }))
			// day_set.add(day.get("日期"));
			
			HashMap<String, String> condition = new HashMap<>();
			for (String each_staff : Config.getInstance().getStaffSet()) {
				condition.put("姓名", each_staff);
				for (String each_day : day_set) {
					condition.put("日期", each_day);
					double receivable_incr = 0, receivable_decr = 0 ;
					for (String each_type : type) {
						condition.put("款项类型", each_type);
						excel_format = "";
						excel_format = excel_format + each_staff + ExcelAPI.DIV;
						excel_format = excel_format + each_day + ExcelAPI.DIV;
						excel_format = excel_format + each_type + ExcelAPI.DIV;

						count1.put("应收增加", new BigDecimal(0));
						count1.put("应收减少", new BigDecimal(0)); 
						count2.put("应收增加", new BigDecimal(0));
						count2.put("应收减少", new BigDecimal(0));
						ArrayList<HashMap<String, String>> jjjj = dbm.query(Config.TABLE_1, condition, item);
						for (HashMap<String, String> i : dbm.query(Config.TABLE_1, condition, item)) {
							double num;
							String value;
							value = i.get("应收增加");
							num = Double.valueOf(null == value || value.equals("") ? "0" : value);
							receivable_incr += num;
							count1.put("应收增加", count1.get("应收增加").add(new BigDecimal(num)));
							value = i.get("应收减少");
							num = Double.valueOf(null == value || value.equals("") ? "0" : value);
							count1.put("应收减少", count1.get("应收减少").add(new BigDecimal(num)));
							receivable_decr += num;
							
							
						}
						

						
						for (HashMap<String, String> i : dbm.query(Config.TABLE_2, condition, item)) {
							double num;
							String value;
							value = i.get("应收增加");
							num = Double.valueOf(null == value || value.equals("") ? "0" : value);
							count2.put("应收增加", count2.get("应收增加").add(new BigDecimal(num)));
							value = i.get("应收减少");
							num = Double.valueOf(null == value || value.equals("") ? "0" : value);
							count2.put("应收减少", count2.get("应收减少").add(new BigDecimal(num)));
						}
						if (!count1.get("应收增加").equals(count2.get("应收增加"))
								|| !count1.get("应收减少").equals(count2.get("应收减少"))) {
							record.openSheet(record.getSheetList()[0]);
							excel_format = excel_format + count1.get("应收增加").subtract(count2.get("应收增加")).toString()
									+ ExcelAPI.DIV;
							excel_format = excel_format + count1.get("应收减少").subtract(count2.get("应收减少")).toString()
									+ ExcelAPI.DIV;
							record.writeFormat(row++, 0, excel_format);
						}

						// System.out.println("|日期|" + each_day + "|姓名|" + each_staff + "|款项类型|" +
						// each_type + "|应收增加|"
						// + count1.get("应收增加").toString());
						// System.out.println("|日期|" + each_day + "|姓名|" + each_staff + "|款项类型|" +
						// each_type + "|应收减少|"
						// + count1.get("应收减少").toString());

					}
					if(receivable_incr != receivable_decr) {
						excel_format = "";
						excel_format = excel_format + each_day + ExcelAPI.DIV;
						excel_format = excel_format + each_staff + ExcelAPI.DIV;

						record.openSheet(record.getSheetList()[1]);
						
						excel_format = excel_format + Double.toString(receivable_incr) + ExcelAPI.DIV;
						excel_format = excel_format + Double.toString(receivable_decr) + ExcelAPI.DIV;

						record.writeFormat(row_res2++, 0, excel_format);
					}
					
					
					
				}
			}
			record.save();
			record.close();
			dbm.closeDB();

		} catch (

		Exception e) {
			e.printStackTrace();
		}

	}

	public static void addRecord1(DBManager dbm, String fileName) throws Exception {
 		ExcelAPI excel = new ExcelAPI(fileName);
		
		for(String sheet_index: excel.getSheetList()) {
		excel.openSheet(sheet_index);
		int base_row = 0;
		int base_column = 0;
		if (!Config.getInstance().getStaffSet().contains(excel.read(base_row, base_column + 1)))
			continue;
//			throw new Exception("载入错误 " + fileName);

		for (int i = 0; i < 6; i++) {
			String text;
			HashMap<String, String> kv = new HashMap<>();
			kv.put("姓名", excel.read(base_row, base_column + 1).trim());
			kv.put("日期", excel.read(base_row + 1, base_column + 1).trim());
			text = excel.read(base_row + 3 + i, base_column).trim();
			kv.put("款项类型", text);
			if (text.equals("刷货差额")) {
				text = excel.read(base_row + 3 + i, base_column + 1, true).trim();
				text = text.equals("") ? "0" : text;
				if (text.charAt(0) == '-') {
					kv.put("应收增加", "0");
					kv.put("应收减少", text);
				} else {
					kv.put("应收增加", text);
					kv.put("应收减少", "0");
				}
			}

			text = excel.read(base_row + 3 + i, base_column + 1, true).trim();
			kv.put("应收增加", text.equals("") ? "0" : text);
			text = excel.read(base_row + 3 + i, base_column + 2, true).trim();
			kv.put("应收减少", text.equals("") ? "0" : text);
			dbm.put(Config.TABLE_1, kv);
		}
		}
		excel.close();
	}

	public static void addRecord2(DBManager dbm, String fileName) throws Exception {
		ExcelAPI excel = new ExcelAPI(fileName);
		excel.openSheet(excel.getSheetList()[0]);
		int i = 1;
		int row = 9 + i - 1;
		while (true) {
			try {
				if (!(Integer.valueOf(excel.read(row, 0)) == i)) {
					// System.out.println("break i:" + i + "|" + excel.read(row, 0) + "|" +
					// excel.read(row, 10));
					break;
				}
			} catch (NumberFormatException e) {
				// System.out.println("break i:" + i + "|" + excel.read(row, 0) + "|" +
				// excel.read(row, 10));
				break;
			}
			HashMap<String, String> kv = new HashMap<>();
			// 合法性验证
			String remark = FJ.convert(excel.read(row, 10).trim(), 0);
			boolean valid = false;
			for (String j : Config.getInstance().getStaffSet()) {
				if (remark.matches("[0-9]{4} (刷货).{2,4} (" + j + ")")) {
					valid = true;
					break;
				}
			}
			if (valid) {
				String sp_remark[] = remark.split(" ");
				kv.put("日期", sp_remark[0]);
				kv.put("款项类型", sp_remark[1]);
				kv.put("姓名", sp_remark[2]);
				kv.put("单据编号", excel.read(row, 2));
				kv.put("应收增加", excel.read(row, 4));
				kv.put("应收减少", excel.read(row, 5));
				kv.put("备注", excel.read(row, 10));
				// System.out.println("ValidData:|" + "i:" + i + "|" + excel.read(row, 0) + "|"
				// + remark);
				dbm.put(Config.TABLE_2, kv);
			} else {
				// System.out.println("IgnoreError|" + "i:" + i + "|" + excel.read(row, 0) + "|"
				// + remark);
			}
			i++;
			row = 9 + i - 1;
		}
		excel.close();
	}

}

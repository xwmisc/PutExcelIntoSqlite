package com.xw;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class MainClass {
	static String PATH_RECORD = "E:\\360Download\\record.xls";
	static String PATH_ACCOUNT_FLODER = "C:\\Users\\acer-pc\\Documents\\WeChat Files\\wxid_qyi4s5vkakv222\\Files\\对账2月3月";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			GUI window = new GUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void account() {
		try {

			Date date = new Date();
			System.out.println("xt0|" + (new Date().getTime() - date.getTime()));
			File result_file = new File(PATH_RECORD);
			if (result_file.exists())
				result_file.delete();

			ExcelAPI result = new ExcelAPI(result_file.getAbsolutePath());
			result.openSheet(result.getSheetList()[0]);

			result.writeFormat(0, 0, "姓名" + ExcelAPI.DIV + "日期" + ExcelAPI.DIV + "款项类型" + ExcelAPI.DIV + "应收增加差额"
					+ ExcelAPI.DIV + "应收减少差额");
			String excel_format = "";
			int row = 1;

			DBManager dbm = new DBManager("test.db");
			dbm.cleanTable(Config.TABLE_1);
			dbm.cleanTable(Config.TABLE_2);

			System.out.println("xt1|" + (new Date().getTime() - date.getTime()));
			for (File file : new File(PATH_ACCOUNT_FLODER).listFiles()) {
				if (file.getName().matches("[0-9]{4}折扣表.xls$")) {
					System.out.println("折扣表文件" + file.getAbsolutePath());
					addRecord1(dbm, file.getAbsolutePath());
				}
				if (file.getName().matches("往来对账数据.*.xls$")) {
					System.out.println("往来对账数据文件" + file.getAbsolutePath());
					addRecord2(dbm, file.getAbsolutePath());
				}
			}
			dbm.commit();// 提交事务
			System.out.println("xt2|" + (new Date().getTime() - date.getTime()));

			dbm.test(Config.TABLE_1);
			dbm.test(Config.TABLE_2);

			String[] type = new String[] { "刷货开工费", "刷货差额", "刷货退回", "刷货返点", "刷货费用", "刷货入库" };
			String[] item = new String[] { "姓名", "款项类型", "日期", "应收增加", "应收减少" };

			HashMap<String, BigDecimal> count1 = new HashMap<>();
			count1.put("应收增加", new BigDecimal(0));
			count1.put("应收减少", new BigDecimal(0));
			HashMap<String, BigDecimal> count2 = new HashMap<>();
			count2.put("应收增加", new BigDecimal(0));
			count2.put("应收减少", new BigDecimal(0));

			HashSet<String> day_set = new HashSet<>();
			for (HashMap<String, String> day : dbm.query(Config.TABLE_1, null, new String[] { "日期" }))
				day_set.add(day.get("日期"));

			System.out.println("xt4|" + (new Date().getTime() - date.getTime()));
			HashMap<String, String> condition = new HashMap<>();
			for (String each_staff : Config.getInstance().getStaffSet()) {
				condition.put("姓名", each_staff);
				for (String each_day : day_set) {
					condition.put("日期", each_day);
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
						for (HashMap<String, String> i : dbm.query(Config.TABLE_1, condition, item)) {
							double num;
							String value;
							value = i.get("应收增加");
							num = Double.valueOf(null == value || value.equals("") ? "0" : value);
							count1.put("应收增加", count1.get("应收增加").add(new BigDecimal(num)));
							value = i.get("应收减少");
							num = Double.valueOf(null == value || value.equals("") ? "0" : value);
							count1.put("应收减少", count1.get("应收减少").add(new BigDecimal(num)));
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
						if (each_staff.equals("姜渊") && each_type.equals("刷货入库") && each_day.equals("0202")) {
							System.out.println(count1.get("应收增加"));
							System.out.println(count1.get("应收减少"));
							System.out.println(count2.get("应收增加"));
							System.out.println(count2.get("应收减少"));
						}
						if (!count1.get("应收增加").equals(count2.get("应收增加"))
								|| !count1.get("应收减少").equals(count2.get("应收减少"))) {
							excel_format = excel_format + count1.get("应收增加").subtract(count2.get("应收增加")).toString()
									+ ExcelAPI.DIV;
							excel_format = excel_format + count1.get("应收减少").subtract(count2.get("应收减少")).toString()
									+ ExcelAPI.DIV;
							result.writeFormat(row++, 0, excel_format);
						}
					}

				}
			}
			System.out.println("xt5|" + (new Date().getTime() - date.getTime()));
			result.save();
			result.close();
			dbm.closeDB();
			System.out.println("xt6|" + (new Date().getTime() - date.getTime()));

		} catch (

		Exception e) {
			e.printStackTrace();
		}
	}

	public static void addRecord1(DBManager dbm, String fileName) throws Exception {
//		System.out.println("addRecord1");

		ExcelAPI excel = new ExcelAPI(fileName);

		for (String sheet : excel.getSheetList()) {
			if (!Config.getInstance().getStaffSet().contains(sheet))
				continue;
			excel.openSheet(sheet);
			int base_row = 0;
			int base_column = 0;
			if (!Config.getInstance().getStaffSet().contains(excel.read(base_row, base_column + 1)))
				throw new Exception("载入错误 " + fileName + "|" + base_row + "|" + (base_column + 1) + "|"
						+ excel.read(base_row, base_column + 1));

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

					if (text.startsWith("-")) {
						kv.put("应收增加", "0");
						kv.put("应收减少", text.substring(1));
					} else {
						kv.put("应收增加", text);
						kv.put("应收减少", "0");
					}
				} else {
					text = excel.read(base_row + 3 + i, base_column + 1, true).trim();
					kv.put("应收增加", text.equals("") ? "0" : text);
					text = excel.read(base_row + 3 + i, base_column + 2, true).trim();
					kv.put("应收减少", text.equals("") ? "0" : text);
				}
				dbm.put(Config.TABLE_1, kv);
			}
		}
		excel.close();
	}

	public static void addRecord2(DBManager dbm, String fileName) throws Exception {
		System.out.println("addRecord2");

		ExcelAPI excel = new ExcelAPI(fileName);
		excel.openSheet(excel.getSheetList()[0]);
		int i = 1;
		int row = 9 + i - 1;
		while (true) {
			try {
				if (!(Integer.valueOf(excel.read(row, 0)) == i)) {
					break;
				}
			} catch (NumberFormatException e) {
				break;
			}
			HashMap<String, String> kv = new HashMap<>();
			// 合法性验证
			String remark = FJ.convert(excel.read(row, 10).trim(), 0);
			boolean valid = false;
			for (String j : Config.getInstance().getStaffSet()) {
				if (remark.matches("[0-9]{4} (刷货).{2,4} (" + j + ").*")) {
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

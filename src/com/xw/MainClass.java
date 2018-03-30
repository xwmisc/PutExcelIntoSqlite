package com.xw;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Supplier;

import com.xw.exception.DBException;
import com.xw.exception.ExcelException;

public class MainClass {
	static String PATH_REPORT = "E:\\360Download\\report.xls";
	static String PATH_ACCOUNT_FLODER = "C:\\Users\\acer-pc\\Documents\\WeChat Files\\wxid_qyi4s5vkakv222\\Files\\对账2月3月";
	private static DecimalFormat dFormat = new DecimalFormat("#.################");

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			GUI window = GUI.getInstance();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void account() {
		try {

			Date date = new Date();
			System.out.println("xt0|" + (new Date().getTime() - date.getTime()));
			File report_file = new File(PATH_REPORT);
			if (report_file.exists())
				report_file.delete();

			ExcelAPI report = new ExcelAPI(report_file.getAbsolutePath(), new String[] { "对账结果", "应收检查", "返点检查" });

			report.openSheet("对账结果");
			report.writeFormat(0, 0,
					"姓名" + ExcelAPI.DIV + "日期" + ExcelAPI.DIV + "款项类型" + ExcelAPI.DIV + "应收增加差额" + ExcelAPI.DIV
							+ "应收减少差额" + ExcelAPI.DIV + "折扣表应收增加" + ExcelAPI.DIV + "折扣表应收减少" + ExcelAPI.DIV + "对账表应收增加"
							+ ExcelAPI.DIV + "对账表应收减少");
			report.freezeRow();
			report.openSheet("应收检查");
			report.writeFormat(0, 0, "日期" + ExcelAPI.DIV + "姓名" + ExcelAPI.DIV + "应收增加合计" + ExcelAPI.DIV + "应收减少合计");
			report.freezeRow();

			String excel_format = "";
			int row = 1;
			int row_res2 = 1;

			DBManager dbm = DBManager.getInstance();

			dbm.cleanTable(Config.TABLE_1);
			dbm.cleanTable(Config.TABLE_2);
			dbm.cleanTable(Config.TABLE_3);

			System.out.println("xt1|" + (new Date().getTime() - date.getTime()));
			for (File file : new File(PATH_ACCOUNT_FLODER).listFiles()) {
				if (file.getName().matches("[0-9]{4}折扣表.xls$")) {
					System.out.println("折扣表" + file.getAbsolutePath());
					addRecord1(dbm, file.getAbsolutePath());
				}
				if (file.getName().matches("往来对账数据.*.xls$")) {
					System.out.println("对账表" + file.getAbsolutePath());
					addRecord2(dbm, file.getAbsolutePath());
				}
				if (file.getName().matches("返点收入余额表.*.xls$")) {
					System.out.println("返点收入余额表" + file.getAbsolutePath());
					addRecord3(dbm, file.getAbsolutePath());
				}
			}
			dbm.commit();// 提交事务
			System.out.println("xt2|" + (new Date().getTime() - date.getTime()));

			dbm.test(Config.TABLE_1);
			dbm.test(Config.TABLE_2);
			dbm.test(Config.TABLE_3);

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
					double receivable_incr = 0;
					double receivable_decr = 0;

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
							receivable_incr += num;
							count1.put("应收增加", count1.get("应收增加").add(new BigDecimal(num)));
							value = i.get("应收减少");
							num = Double.valueOf(null == value || value.equals("") ? "0" : value);
							receivable_decr += num;
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
						// if (each_staff.equals("姜渊") && each_type.equals("刷货入库") &&
						// each_day.equals("0202")) {
						// System.out.println(count1.get("应收增加"));
						// System.out.println(count1.get("应收减少"));
						// System.out.println(count2.get("应收增加"));
						// System.out.println(count2.get("应收减少"));
						// }
						BigDecimal sum = (count1.get("应收增加").subtract(count1.get("应收减少")))
								.subtract(count2.get("应收增加").subtract(count2.get("应收减少")));
						int compare_result = sum.compareTo(BigDecimal.ZERO);
						if (compare_result != 0) {
							excel_format = excel_format + count1.get("应收增加").subtract(count2.get("应收增加")).toString()
									+ ExcelAPI.DIV;
							excel_format = excel_format + count1.get("应收减少").subtract(count2.get("应收减少")).toString()
									+ ExcelAPI.DIV;
							excel_format = excel_format + count1.get("应收增加").toString() + ExcelAPI.DIV;
							excel_format = excel_format + count1.get("应收减少").toString() + ExcelAPI.DIV;
							excel_format = excel_format + count2.get("应收增加").toString() + ExcelAPI.DIV;
							excel_format = excel_format + count2.get("应收减少").toString() + ExcelAPI.DIV;
							report.openSheet("对账结果");
							report.writeFormat(row++, 0, excel_format);
						}
					}

					if (receivable_incr != receivable_decr) {
						excel_format = "";
						excel_format = excel_format + each_day + ExcelAPI.DIV;
						excel_format = excel_format + each_staff + ExcelAPI.DIV;
						excel_format = excel_format + Double.toString(receivable_incr) + ExcelAPI.DIV;
						excel_format = excel_format + Double.toString(receivable_decr) + ExcelAPI.DIV;

						report.openSheet("应收检查");
						report.writeFormat(row_res2++, 0, excel_format);
					}

				}
			}

			// 返点检查
			HashMap<String, String> point_condition = new HashMap<>();

			day_set = new HashSet<>();
			for (HashMap<String, String> day : dbm.query(Config.TABLE_1, null, new String[] { "日期" }))
				day_set.add(day.get("日期"));
			for (HashMap<String, String> day : dbm.query(Config.TABLE_3, null, new String[] { "日期" }))
				day_set.add(day.get("日期"));

			int row_point = 1;
			excel_format = "";
			excel_format = excel_format + "日期" + ExcelAPI.DIV;
			excel_format = excel_format + "返点差额" + ExcelAPI.DIV;
			excel_format = excel_format + "返点表总额" + ExcelAPI.DIV;
			excel_format = excel_format + "折扣表总额" + ExcelAPI.DIV;
			report.openSheet("返点检查");
			report.writeFormat(0, 0, excel_format);

			for (String each_day : day_set) {
				point_condition.clear();
				point_condition.put("日期", each_day);
				ArrayList<HashMap<String, String>> record_point = null;
				ArrayList<HashMap<String, String>> account_point = null;
				try {
					record_point = dbm.query(Config.TABLE_3, point_condition, new String[] { "当日总刷货返点" });

					point_condition.put("款项类型", "刷货返点");
					account_point = dbm.query(Config.TABLE_1, point_condition, new String[] { "应收减少" });
				} catch (DBException e) {
					e.show();
				}
				double point1 = Optional.ofNullable(record_point)
						.map(point -> Double.parseDouble(point.get(0).get("当日总刷货返点"))).orElse(0d);
				double point2 = account_point.stream().mapToDouble(point -> Double.parseDouble(point.get("应收减少")))
						.sum();
				if (point1 != point2) {
					excel_format = "";
					excel_format = excel_format + each_day + ExcelAPI.DIV;
					excel_format = excel_format + dFormat.format(point2 - point1) + ExcelAPI.DIV;
					excel_format = excel_format + dFormat.format(point1) + ExcelAPI.DIV;
					excel_format = excel_format + dFormat.format(point2) + ExcelAPI.DIV;
					report.openSheet("返点检查");
					report.writeFormat(row_point++, 0, excel_format);
				}
			}

			System.out.println("xt5|" + (new Date().getTime() - date.getTime()));
			report.save();
			report.close();
//			dbm.closeDB();
			System.out.println("xt6|" + (new Date().getTime() - date.getTime()));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void addRecord1(DBManager dbm, String fileName) throws Exception {
		// System.out.println("addRecord1");

		ExcelAPI excel = new ExcelAPI(fileName);

		for (String sheet : excel.getSheetList()) {
			if (!Config.getInstance().getStaffSet().contains(sheet))
				continue;
			excel.openSheet(sheet);
			int base_row = 0;
			int base_column = 0;
			if (!Config.getInstance().getStaffSet().contains(excel.read(base_row, base_column + 1)))
				continue;
			// throw new Exception("载入错误 " + fileName + "|" + base_row + "|" + (base_column
			// + 1) + "|"
			// + excel.read(base_row, base_column + 1));

			for (int i = 0; i < 6; i++) {
				String text;
				HashMap<String, String> kv = new HashMap<>();
				kv.put("姓名", excel.read(base_row, base_column + 1).trim());
				kv.put("日期", excel.read(base_row + 1, base_column + 1).trim());
				text = excel.read(base_row + 3 + i, base_column).trim();
				kv.put("款项类型", text);
				if (text.equals("刷货差额")) {
					try {
						text = dFormat.format(excel.readNumber(base_row + 3 + i, base_column + 1));
					} catch (ClassCastException e) {
						text = "0";
					}

					if (text.startsWith("-")) {
						kv.put("应收增加", "0");
						kv.put("应收减少", text.substring(1));
					} else {
						kv.put("应收增加", text);
						kv.put("应收减少", "0");
					}
				} else {
					try {
						text = dFormat.format(excel.readNumber(base_row + 3 + i, base_column + 1));
					} catch (ClassCastException e) {
						text = "0";
					}
					kv.put("应收增加", text.equals("") ? "0" : text);
					try {
						text = dFormat.format(excel.readNumber(base_row + 3 + i, base_column + 2));
					} catch (ClassCastException e) {
						text = "0";
					}
					kv.put("应收减少", text.equals("") ? "0" : text);
				}
				dbm.put(Config.TABLE_1, kv);
			}
		}
		excel.close();
	}

	public static void addRecord2(DBManager dbm, String fileName) throws ExcelException, IOException, DBException  {
		System.out.println("addRecord2");

		ExcelAPI excel = new ExcelAPI(fileName);
		excel.openSheet(excel.getSheetList()[0]);
		int i = 1;
		int row = 9 + i - 1;
		while (true) {
			try {
				if (!(excel.readNumber(row, 0) == i)) {
					break;
				}
			} catch (ClassCastException e) {
				break;
			}
			HashMap<String, String> kv = new HashMap<>();
			// 合法性验证
			String remark = FJ.convert(excel.read(row, 10).trim(), 0);
			String staff_name = null;
			for (String j : Config.getInstance().getStaffSet()) {
				if (remark.matches("[0-9]{4} (刷货).{2,4} (" + j + ").*")) {
					staff_name = j;
					break;
				}
			}
			if (null != staff_name) {
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

	public static void addRecord3(DBManager dbm, String fileName) throws ExcelException, IOException, DBException{
		System.out.println("addRecord3");

		ExcelAPI excel = new ExcelAPI(fileName);
		excel.openSheet("2018");
		int base_row = 4;
		int row = 0;
		Date date = null;
		while (true) {
			try {
				date = excel.readDate(row + base_row, 0);
				if (null == date) {
					System.out.println("inValidData:|" + date);
					break;
				}
			} catch (ClassCastException e) {
				break;
			}
			System.out.println("ValidData:|" + date);
			HashMap<String, String> kv = new HashMap<>();
			String date_text = ((date.getMonth() + 1 < 10) ? "0" : "") + (date.getMonth() + 1)
					+ ((date.getDate() < 10) ? "0" : "") + date.getDate();
			System.out.println("ValidData:|" + date_text);
			kv.put("日期", date_text);
			String point_text = "0";
			try {
				point_text = dFormat.format((excel.readNumber(row + base_row, 1)));
			} catch (ClassCastException e) {
				System.out.println("ValidData:|" + "row:" + (row + base_row) + "|" + excel.read(row + base_row, 1) + "|"
						+ point_text);
				point_text = "0";
			}
			System.out.println("ValidData:|" + point_text);
			kv.put("当日总刷货返点", point_text);

			dbm.put(Config.TABLE_3, kv);
			row++;
		}
		excel.close();
	}

}

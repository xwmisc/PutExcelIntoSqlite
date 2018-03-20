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

			DBManager dbm = new DBManager("test.db");
			dbm.cleanTable(Config.TABLE_1);
			dbm.cleanTable(Config.TABLE_2);
			addRecord1(dbm, "E:\\360Download\\0301折扣表 (1).xls");
			dbm.test(Config.TABLE_1);
			addRecord2(dbm, "E:\\360Download\\往来对账数据 - 副本.xls");
			dbm.test(Config.TABLE_2);

			String[] type = new String[] { "刷货开工费", "刷货差额", "刷货退回", "刷货返点", "刷货费用", "刷货入库" };
			String[] item = new String[] { "姓名", "款项类型", "日期", "应收增加", "应收减少" };
			// for(String i: Config.getInstance().getStaffSet())

			HashMap<String, BigDecimal> count = new HashMap<>();
			count.put("应收增加", new BigDecimal(0));
			count.put("应收减少", new BigDecimal(0));

			HashSet<String> day_set = new HashSet<>();
			for (HashMap<String, String> day : dbm.query(Config.TABLE_1, null, new String[] { "日期" }))
				day_set.add(day.get("日期"));
			for (HashMap<String, String> day : dbm.query(Config.TABLE_2, null, new String[] { "日期" }))
				day_set.add(day.get("日期"));

			ExcelAPI record = new ExcelAPI("E:\\360Download\\record.xls");
			int col=0;
			int row=0;
			
			HashMap<String, String> condition = new HashMap<>();
			for (String each_staff : Config.getInstance().getStaffSet()) {
				condition.put("姓名", each_staff);
				for (String each_day : day_set) {
					condition.put("日期", each_day);
					for (String each_type : type) {
						condition.put("款项类型", each_type);

						count.put("应收增加", new BigDecimal(0));
						count.put("应收减少", new BigDecimal(0));
						for (HashMap<String, String> i : dbm.query(Config.TABLE_1, condition, item)) {
							double num;
							String value;
							value= i.get("应收增加");
							num = Double.valueOf(null==value||value.equals("")?"0":value);
							count.put("应收增加", count.get("应收增加").add(new BigDecimal(num)));
							value = i.get("应收减少");
							num = Double.valueOf(null==value||value.equals("")?"0":value);
							count.put("应收减少", count.get("应收减少").add(new BigDecimal(num)));
						}
						for (HashMap<String, String> i : dbm.query(Config.TABLE_2, condition, item)) {
							// System.out.println("!"+i.get("应收增加"));
							// System.out.println(count.get("应收增加"));
							// System.out.println(i.get("应收增加"));
							// System.out.println(BigDecimal.valueOf(Double.valueOf(i.get("应收增加"))).toString());
							// System.out.println("!"+i.get("应收增加"));
							// System.out.println("!"+Double.valueOf(i.get("应收增加")));
							// System.out.println("!"+new
							// BigDecimal(Double.valueOf(i.get("应收增加"))).toString());
							double num;
							String value;
							value= i.get("应收增加");
							num = Double.valueOf(null==value||value.equals("")?"0":value);
							count.put("应收增加", count.get("应收增加").subtract(new BigDecimal(num)));
							value = i.get("应收减少");
							num = Double.valueOf(null==value||value.equals("")?"0":value);
							count.put("应收减少", count.get("应收减少").subtract(new BigDecimal(num)));
						}
						System.out.println("|日期|" + each_day + "|姓名|" + each_staff + "|款项类型|" + each_type + "|应收增加|"
								+ count.get("应收增加").toString());
						System.out.println("|日期|" + each_day + "|姓名|" + each_staff + "|款项类型|" + each_type + "|应收减少|"
								+ count.get("应收减少").toString());

					}
					
					
				}
			}
			dbm.closeDB();

		}catch(

	Exception e)
	{
		e.printStackTrace();
	}

	}

	public static void addRecord1(DBManager dbm, String fileName) throws Exception {
		ExcelAPI excel = new ExcelAPI(fileName);
		excel.openSheet(excel.getSheetList()[0]);
		int base_row = 0;
		int base_column = 0;
		if (!Config.getInstance().getStaffSet().contains(excel.read(base_row, base_column + 1)))
			throw new Exception("载入错误 " + fileName);

		for (int i = 0; i < 6; i++) {
			HashMap<String, String> kv = new HashMap<>();
			kv.put("姓名", excel.read(base_row, base_column + 1).trim());
			kv.put("日期", excel.read(base_row + 1, base_column + 1).trim());
			kv.put("款项类型", excel.read(base_row + 3 + i, base_column).trim());
			kv.put("应收增加", excel.read(base_row + 3 + i, base_column + 1, true).trim());
			kv.put("应收减少", excel.read(base_row + 3 + i, base_column + 2, true).trim());
			dbm.put(Config.TABLE_1, kv);
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

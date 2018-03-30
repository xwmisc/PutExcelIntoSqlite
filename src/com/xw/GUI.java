package com.xw;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.xw.exception.DBException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;

import java.sql.SQLException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Combo;

public class GUI {

	private static GUI m_instance = null;
	protected Shell shell;
	private Text text01;
	private Text text02;
	private Table table;
	private TableManager table_manager;
	private Combo combo;

	public static GUI getInstance() {
		if(m_instance==null) {
			m_instance = new GUI();
		}
		return m_instance;
	}
	
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GUI window = GUI.getInstance();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showErrDialog(String message) {
		int style = SWT.APPLICATION_MODAL | SWT.ERROR ;  
        MessageBox messageBox = new MessageBox(shell, style);  
		messageBox.setText("Error Occurred!");  
		messageBox.setMessage(message);  
		messageBox.open();
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(839, 891);
		shell.setText("SWT Application");

		text01 = new Text(shell, SWT.BORDER | SWT.H_SCROLL | SWT.CANCEL);
		text01.setText("C:\\Users\\acer-pc\\Documents\\WeChat Files\\wxid_qyi4s5vkakv222\\Files\\对账2月3月(1)");
		text01.setToolTipText("对账单文件夹 填入或拖入文件夹");
		text01.setBounds(10, 63, 412, 47);

		DropTarget dropTarget = new DropTarget(text01, DND.DROP_NONE);
		Transfer[] transfer = new Transfer[] { FileTransfer.getInstance() };
		dropTarget.setTransfer(transfer);
		dropTarget.addDropListener(new DropTargetListener() {

			@Override
			public void dragEnter(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dragLeave(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dragOperationChanged(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dragOver(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drop(DropTargetEvent arg0) {
				// TODO Auto-generated method stub
				String[] files = (String[]) arg0.data;
				text01.setText(files[0]);
			}

			@Override
			public void dropAccept(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		text02 = new Text(shell, SWT.BORDER | SWT.H_SCROLL | SWT.CANCEL);
		text02.setText("C:\\Users\\acer-pc\\Documents\\WeChat Files\\wxid_qyi4s5vkakv222\\Files\\对账2月3月(1)");
		text02.setToolTipText("对账单文件夹 填入或拖入文件夹");
		text02.setBounds(10, 142, 412, 47);

		DropTarget dropTarget02 = new DropTarget(text02, DND.DROP_NONE);
		Transfer[] transfer02 = new Transfer[] { FileTransfer.getInstance() };
		dropTarget02.setTransfer(transfer02);
		dropTarget02.addDropListener(new DropTargetListener() {

			@Override
			public void dragEnter(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dragLeave(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dragOperationChanged(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void dragOver(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void drop(DropTargetEvent arg0) {
				// TODO Auto-generated method stub
				String[] files = (String[]) arg0.data;
				text02.setText(files[0]);
			}

			@Override
			public void dropAccept(DropTargetEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(10, 10, 224, 20);
		lblNewLabel.setText("通过填写或拖拽来指定文件夹");

		Button button = new Button(shell, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				String account_path = text01.getText();
				String record_path = text02.getText();

				if (account_path.equals("") || record_path.equals(""))
					return;

				MainClass.PATH_ACCOUNT_FLODER = account_path;

				if (record_path.endsWith("\\")) {
					record_path = record_path + "result.xls";
				} else if (!record_path.endsWith(".xls")) {
					record_path = record_path + "\\result.xls";
				}
				MainClass.PATH_REPORT = record_path;
				System.out.println("PATH_ACCOUNT_FLODER " + MainClass.PATH_ACCOUNT_FLODER);
				System.out.println("PATH_RECORD " + MainClass.PATH_REPORT);

				button.setEnabled(false);
				run(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						MainClass.account();
					}
				}, new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						button.setEnabled(true);
					}
				});

			}
		});
		button.setBounds(438, 142, 135, 47);
		button.setText("开始");

		Label label = new Label(shell, SWT.NONE);
		label.setText("对账文件夹：");
		label.setBounds(10, 36, 224, 20);

		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setText("结果集存放文件夹：");
		label_1.setBounds(10, 116, 224, 20);

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		table.setBounds(10, 234, 801, 609);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		table.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = table.getSelectionIndex();
			}
		});
		table_manager = new TableManager(table);

		combo = new Combo(shell, SWT.READ_ONLY);
		combo.setItems(new String[] { "table_1", "table_2", "table_3" });
		combo.setBounds(10, 200, 108, 54);
		combo.select(0);

		Button btnSearch = new Button(shell, SWT.NONE);
		btnSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				table_manager.show();
			}
		});
		btnSearch.setBounds(124, 198, 108, 30);
		btnSearch.setText("检索");


	}

	void run(final Runnable r1, final Runnable r2) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				r1.run();
				shell.getDisplay().syncExec(r2);
			}
		}).start();
	}

	class TableManager {
		HashSet<String> columns = new HashSet<>();
		Table m_table;
		DataSource data_source;
		HashMap<TableItem, HashMap<String, String>> m_items = new HashMap<>();

		public TableManager(Table targetTable) {
			m_table = targetTable;
			try {
				data_source = new DataSource();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}

		public void addColumns(String... columnName) {
			for (String each : columnName) {
				if (!columns.add(each))
					continue;
				final TableColumn tblclmnNewColumn = new TableColumn(m_table, SWT.NONE);
				tblclmnNewColumn
						.setWidth(each.getBytes().length * 100 / 16 < 100 ? 100 : each.getBytes().length * 100 / 16);
				tblclmnNewColumn.setText(each);
				tblclmnNewColumn.addSelectionListener(new SelectionListener() {
					TableColumn column = tblclmnNewColumn;

					@Override
					public void widgetSelected(SelectionEvent arg0) {
						System.out.println("!" + column.getText());
						boolean isAscend = true; // 按照升序排序
						Collator comparator = Collator.getInstance(Locale.getDefault());
						int columnIndex = table.indexOf(column);
						TableItem[] items = table.getItems();
						// 使用冒泡法进行排序
						for (int i = 1; i < items.length; i++) {
							String str2value = items[i].getText(columnIndex);
							if (str2value.equalsIgnoreCase("")) {
								// 当遇到表格中的空项目时，就停止往下检索排序项目
								break;
							}
							for (int j = 0; j < i; j++) {
								String str1value = items[j].getText(columnIndex);
								boolean isLessThan = comparator.compare(str2value, str1value) < 0;
								if ((isAscend && isLessThan) || (!isAscend && !isLessThan)) {
									String[] values = getTableItemText(table, items[i]);
									Object obj = items[i].getData();
									items[i].dispose();
									TableItem item = new TableItem(table, SWT.NONE, j);
									item.setText(values);
									item.setData(obj);
									items = table.getItems();
									break;
								}
							}
						}
						table.setSortColumn(column);
						table.setSortDirection((isAscend ? SWT.UP : SWT.DOWN));
						isAscend = !isAscend;
					}

					public String[] getTableItemText(Table table, TableItem item) {
						int count = table.getColumnCount();
						String[] strs = new String[count];
						for (int i = 0; i < count; i++) {
							strs[i] = item.getText(i);
						}
						return strs;
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent arg0) {

					}
				});
			}
		}

		public void addItems(HashMap<String, String> item) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			item.keySet().stream().forEach(key -> {
				addColumns(key);
				for (TableColumn column : table.getColumns()) {
					if (column.getText().equals(key))
						tableItem.setText(table.indexOf(column), item.get(key));
				}
			});

		}

		public void clearAll() {
			columns = new HashSet<>();

			table.removeAll();
			for (TableColumn column : table.getColumns()) {
				column.dispose();
			}
		}

		public void show() {
			clearAll();
			System.out.println(data_source == null);
			System.out.println(combo == null);
			data_source.getData(combo.getText()).forEach(data -> {
				addItems(data);
			});
		}
	}

	class DataSource implements GUIContract.Model {
		DBManager dbm;

		public DataSource() throws Exception {
			dbm = DBManager.getInstance();
		}

		@Override
		public Stream<HashMap<String, String>> getData(String tableName) {

			return getData(tableName, null);
		}

		@Override
		public Stream<HashMap<String, String>> getData(String tableName, HashMap<String, String> condition) {
			try {
				HashSet<String> column_set = dbm.getColumnNames(tableName);
				String[] column_name = new String[column_set.size()];
				int i = 0;
				for (String each : column_set) {
					column_name[i++] = each;
				}
				return dbm.query(tableName, condition, column_name).stream();
			} catch (DBException e) {
				e.show();
			}
			return null;
		}

		@Override
		public Stream<HashMap<String, String>> deleteData(String tableName,
				Stream<HashMap<String, String>> dataSource) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}

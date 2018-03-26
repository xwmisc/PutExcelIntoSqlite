package com.xw;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import java.util.function.Consumer;
import org.eclipse.swt.events.SelectionEvent;

public class GUI {

	protected Shell shell;
	private Text text01;
	private Text text02;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			GUI window = new GUI();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		shell.setSize(450, 320);
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
				MainClass.PATH_RECORD = record_path;
				System.out.println("PATH_ACCOUNT_FLODER " + MainClass.PATH_ACCOUNT_FLODER);
				System.out.println("PATH_RECORD " + MainClass.PATH_RECORD);
				

				button.setEnabled(false);
				run(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						MainClass.account();
					}
				},new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						button.setEnabled(true);
					}
				});
				
				
			}
		});
		button.setBounds(10, 195, 135, 47);
		button.setText("开始");

		Label label = new Label(shell, SWT.NONE);
		label.setText("对账文件夹：");
		label.setBounds(10, 36, 224, 20);

		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setText("结果集存放文件夹：");
		label_1.setBounds(10, 116, 224, 20);
	}
	
	void run(final Runnable r1,final Runnable r2) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				r1.run();
				shell.getDisplay().syncExec(r2);
			}
		}).start();
	}
}

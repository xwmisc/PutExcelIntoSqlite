package com.xw;

import java.util.HashMap;
import java.util.stream.Stream;

import org.eclipse.swt.widgets.Table;

public interface GUIContract {

	interface Presenter {
		void loadAllData();
	}

	interface Model {

		Stream<HashMap<String, String>> getData(String tableName);

		Stream<HashMap<String, String>> getData(String tableName, HashMap<String, String> condition);

		Stream<HashMap<String, String>> deleteData(String tableName, Stream<HashMap<String, String>> dataSource);
	}
}

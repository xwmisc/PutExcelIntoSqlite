package com.xw.exception;

import com.xw.GUI;

public class BaseException extends Exception {

	public BaseException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args) {

	}
	public void show() {
		GUI.getInstance().showErrDialog(this.getMessage());
	}

}

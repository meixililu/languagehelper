package com.messi.languagehelper.dao;

public class BudejieBody {
	private BDJPagebean pagebean;

	private int ret_code;

	public void setPagebean(BDJPagebean pagebean) {
		this.pagebean = pagebean;
	}

	public BDJPagebean getPagebean() {
		return this.pagebean;
	}

	public void setRet_code(int ret_code) {
		this.ret_code = ret_code;
	}

	public int getRet_code() {
		return this.ret_code;
	}

}
package com.messi.languagehelper.dao;

import java.util.List;

public class WordDetailListBody {
	
	private int ret_code;

	private List<WordDetailListItem> list;

	public int getRet_code() {
		return ret_code;
	}

	public void setRet_code(int ret_code) {
		this.ret_code = ret_code;
	}

	public List<WordDetailListItem> getList() {
		return list;
	}

	public void setList(List<WordDetailListItem> list) {
		this.list = list;
	}

	
}
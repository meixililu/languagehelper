package com.messi.languagehelper.dao;

import java.util.List;

public class BDJPagebean {
	private int allNum;

	private int allPages;

	private List<BDJContent> contentlist;

	private int currentPage;

	private int maxResult;

	public void setAllNum(int allNum) {
		this.allNum = allNum;
	}

	public int getAllNum() {
		return this.allNum;
	}

	public void setAllPages(int allPages) {
		this.allPages = allPages;
	}

	public int getAllPages() {
		return this.allPages;
	}

	public void setContentlist(List<BDJContent> contentlist) {
		this.contentlist = contentlist;
	}

	public List<BDJContent> getContentlist() {
		return this.contentlist;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getCurrentPage() {
		return this.currentPage;
	}

	public void setMaxResult(int maxResult) {
		this.maxResult = maxResult;
	}

	public int getMaxResult() {
		return this.maxResult;
	}

}

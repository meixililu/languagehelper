package com.messi.languagehelper.dao;

import java.util.List;

public class JokeBody {
	private int allNum;

	private int allPages;

	private List<JokeContent> contentlist;

	private int currentPage;

	private int maxResult;

	private int ret_code;

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

	public void setContentlist(List<JokeContent> contentlist) {
		this.contentlist = contentlist;
	}

	public List<JokeContent> getContentlist() {
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

	public void setRet_code(int ret_code) {
		this.ret_code = ret_code;
	}

	public int getRet_code() {
		return this.ret_code;
	}

}
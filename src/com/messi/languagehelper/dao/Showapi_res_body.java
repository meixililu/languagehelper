package com.messi.languagehelper.dao;

import java.util.List;

public class Showapi_res_body {
	private Basic basic;

	private String query;

	private int ret_code;

	private String[] translation;

	private List<Web> web;

	public void setBasic(Basic basic) {
		this.basic = basic;
	}

	public Basic getBasic() {
		return this.basic;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQuery() {
		return this.query;
	}

	public void setRet_code(int ret_code) {
		this.ret_code = ret_code;
	}

	public int getRet_code() {
		return this.ret_code;
	}

	public String[] getTranslations() {
		return translation;
	}

	public void setTranslations(String[] translations) {
		this.translation = translations;
	}

	public List<Web> getWebs() {
		return web;
	}

	public void setWebs(List<Web> webs) {
		this.web = webs;
	}


}
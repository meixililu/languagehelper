package com.messi.languagehelper.dao;

import java.util.List;

public class BaiduOcrRoot {

	private String errNum;

	private List<RetData> retData;

	private String querySign;

	private String errMsg;

	public void setErrNum(String errNum) {
		this.errNum = errNum;
	}

	public String getErrNum() {
		return this.errNum;
	}

	public void setRetData(List<RetData> retData) {
		this.retData = retData;
	}

	public List<RetData> getRetData() {
		return this.retData;
	}

	public void setQuerySign(String querySign) {
		this.querySign = querySign;
	}

	public String getQuerySign() {
		return this.querySign;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getErrMsg() {
		return this.errMsg;
	}

}

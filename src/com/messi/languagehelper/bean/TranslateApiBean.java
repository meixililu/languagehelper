package com.messi.languagehelper.bean;

public class TranslateApiBean {

	private String name;
	
	private long status;//-1服务器规定不可用； 0本机最近一次解析失败； 1正常； 大于时间戳就是被屏蔽时间

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}
	
}

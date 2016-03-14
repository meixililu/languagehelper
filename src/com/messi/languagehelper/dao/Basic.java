package com.messi.languagehelper.dao;

public class Basic {
	
	private String[] explains;

	private String uk_phonetic;

	private String us_phonetic;
	
	private String phonetic;
	
	public String getPhonetic() {
		return phonetic;
	}

	public void setPhonetic(String phonetic) {
		this.phonetic = phonetic;
	}

	public String[] getExplains() {
		return explains;
	}

	public void setExplains(String[] explains) {
		this.explains = explains;
	}

	public void setUk_phonetic(String uk_phonetic) {
		this.uk_phonetic = uk_phonetic;
	}

	public String getUk_phonetic() {
		return this.uk_phonetic;
	}

	public void setUs_phonetic(String us_phonetic) {
		this.us_phonetic = us_phonetic;
	}

	public String getUs_phonetic() {
		return this.us_phonetic;
	}

}
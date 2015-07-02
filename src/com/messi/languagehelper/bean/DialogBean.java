package com.messi.languagehelper.bean;

public class DialogBean implements BaseBean{
	
	private String id;
	
	private CharSequence content;
	
	private CharSequence translate;
	
	private String role;
	
	private String score;
	
	private int color;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CharSequence getContent() {
		return content;
	}

	public void setContent(CharSequence content) {
		this.content = content;
	}

	public CharSequence getTranslate() {
		return translate;
	}

	public void setTranslate(CharSequence translate) {
		this.translate = translate;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
	
	
}

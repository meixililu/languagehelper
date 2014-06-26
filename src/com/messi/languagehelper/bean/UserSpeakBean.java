package com.messi.languagehelper.bean;

public class UserSpeakBean implements BaseBean{
	
	private String id;
	
	private CharSequence content;
	
	private String score;
	
	private int scoreInt;
	
	private int color;
	
	public int getScoreInt() {
		return scoreInt;
	}

	public void setScoreInt(int scoreInt) {
		this.scoreInt = scoreInt;
	}

	
	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public UserSpeakBean(){
	}

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

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}
	
}

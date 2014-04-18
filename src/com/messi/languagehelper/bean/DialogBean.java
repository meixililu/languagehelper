package com.messi.languagehelper.bean;

public class DialogBean implements BaseBean{
	
	private String id;
	
	private String question;
	
	private String answer;
	
	private String resultAudioPath;
	
	private String questionAudioPath;
	
	private String resultVoiceId;
	
	private String questionVoiceId;
	/**0未收藏，1收藏**/
	private String iscollected;
	/**浏览次数**/
	private int visit_times;
	
	private int speak_speed;
		
	public int getSpeak_speed() {
		return speak_speed;
	}

	public void setSpeak_speed(int speak_speed) {
		this.speak_speed = speak_speed;
	}

	public int getVisit_times() {
		return visit_times;
	}

	public void setVisit_times(int visit_times) {
		this.visit_times = visit_times;
	}

	public String getIscollected() {
		return iscollected;
	}

	public void setIscollected(String iscollected) {
		this.iscollected = iscollected;
	}

	public String getResultVoiceId() {
		return resultVoiceId;
	}

	public void setResultVoiceId(String resultVoiceId) {
		this.resultVoiceId = resultVoiceId;
	}

	public String getQuestionVoiceId() {
		return questionVoiceId;
	}

	public void setQuestionVoiceId(String questionVoiceId) {
		this.questionVoiceId = questionVoiceId;
	}

	public String getQuestionAudioPath() {
		return questionAudioPath;
	}

	public void setQuestionAudioPath(String questionAudioPath) {
		this.questionAudioPath = questionAudioPath;
	}

	public String getResultAudioPath() {
		return resultAudioPath;
	}

	public void setResultAudioPath(String resultAudioPath) {
		this.resultAudioPath = resultAudioPath;
	}

	public DialogBean() {}
	
	public DialogBean(String id, String answer, String question) {
		this.id = id;
		this.answer = answer;
		this.question = question;
	}
	
	public DialogBean(String answer, String question) {
		this.answer = answer;
		this.question = question;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Override
	public String toString() {
		return "DialogBean [id=" + id + ", question=" + question + ", answer="
				+ answer + ", resultAudioPath=" + resultAudioPath
				+ ", questionAudioPath=" + questionAudioPath
				+ ", resultVoiceId=" + resultVoiceId + ", questionVoiceId="
				+ questionVoiceId + ", iscollected=" + iscollected
				+ ", visit_times=" + visit_times + ", speak_speed="
				+ speak_speed + "]";
	}
}

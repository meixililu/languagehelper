package com.messi.languagehelper.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table MEANS.
 */
public class Means {

    private Long id;
    private String mean;
    private String resultVoiceId;
    private String resultAudioPath;

    public Means() {
    }

    public Means(Long id) {
        this.id = id;
    }

    public Means(Long id, String mean, String resultVoiceId, String resultAudioPath) {
        this.id = id;
        this.mean = mean;
        this.resultVoiceId = resultVoiceId;
        this.resultAudioPath = resultAudioPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMean() {
        return mean;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }

    public String getResultVoiceId() {
        return resultVoiceId;
    }

    public void setResultVoiceId(String resultVoiceId) {
        this.resultVoiceId = resultVoiceId;
    }

    public String getResultAudioPath() {
        return resultAudioPath;
    }

    public void setResultAudioPath(String resultAudioPath) {
        this.resultAudioPath = resultAudioPath;
    }

	@Override
	public String toString() {
		return "Means [id=" + id + ", mean=" + mean + ", resultVoiceId="
				+ resultVoiceId + ", resultAudioPath=" + resultAudioPath + "]";
	}

}
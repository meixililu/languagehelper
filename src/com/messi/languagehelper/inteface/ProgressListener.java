package com.messi.languagehelper.inteface;

public interface ProgressListener {

	void update(long bytesRead, long contentLength, boolean done);
	
}

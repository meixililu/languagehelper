package com.messi.languagehelper.task;

import android.content.Context;
import android.os.AsyncTask;
/**
 * @author Messi
 * 
 */
public class PublicTask extends AsyncTask<Void, Void, Object> {

	private PublicTaskListener mPublicTaskListener;
	private Context mContext;
	
	public PublicTask(Context mContext){
		this.mContext = mContext;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mPublicTaskListener != null) {
			mPublicTaskListener.onPreExecute();
		}
	}

	@Override
	protected Object doInBackground(Void... params) {
		Object resutl = new Object();
		if (mPublicTaskListener != null) {
			resutl = mPublicTaskListener.doInBackground();
		}
		return resutl;
	}
	
	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		if (mPublicTaskListener != null) {
			mPublicTaskListener.onFinish(result);
		}
	}
	
	public void setmPublicTaskListener(PublicTaskListener mPublicTaskListener) {
		this.mPublicTaskListener = mPublicTaskListener;
	}
	
	public interface PublicTaskListener{
		public void onPreExecute();
		public Object doInBackground();
		public void onFinish(Object resutl);
	}

}

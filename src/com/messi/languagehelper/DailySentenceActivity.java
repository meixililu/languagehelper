package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.messi.languagehelper.adapter.DailySentenceListsAdapter;
import com.messi.languagehelper.dao.EveryDaySentence;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.XFYSAD;

public class DailySentenceActivity extends BaseActivity implements OnClickListener {

	private ListView recent_used_lv;
	private LayoutInflater mInflater;
	private DailySentenceListsAdapter mAdapter;
	private List<EveryDaySentence> beans;
	private MediaPlayer mPlayer;
	
	private XFYSAD mXFYSAD;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.daily_sentence_activity);
		init();
		new GetDataTask().execute();
	}
	
	private void init() {
		try {
			getSupportActionBar().setTitle(getResources().getString(R.string.dailysentence));
			mPlayer = new MediaPlayer();
			mInflater = LayoutInflater.from(this);
			recent_used_lv = (ListView) findViewById(R.id.studycategory_lv);
			
			View headerView = mInflater.inflate(R.layout.xunfei_ysad_item, null);
			recent_used_lv.addHeaderView(headerView);
			
			beans = new ArrayList<EveryDaySentence>();
			mAdapter = new DailySentenceListsAdapter(this, mInflater, beans, mPlayer, mProgressbar);
			recent_used_lv.setAdapter(mAdapter);
			
			mXFYSAD = new XFYSAD(this, headerView, ADUtil.MRYJYSNRLAd);
			mXFYSAD.showAD();
			mAdapter.notifyDataSetChanged();
			mXFYSAD.startPlayImg();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	class GetDataTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressbar();
		}

		@Override
		protected Void doInBackground(Void... params) {
			List<EveryDaySentence> list = DataBaseUtil.getInstance().getDailySentenceList(Settings.offset);
			beans.clear();
			beans.addAll(list);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			hideProgressbar();
			mAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mPlayer != null) {   
			mPlayer.stop();  
			mPlayer.release();   
			mPlayer = null;   
        }   
		if(mXFYSAD != null){
    		mXFYSAD.canclePlayImg();
    	}
		LogUtil.DefalutLog("CollectedFragment-onDestroy");
	}

	@Override
	public void onClick(View v) {
		
	}
}

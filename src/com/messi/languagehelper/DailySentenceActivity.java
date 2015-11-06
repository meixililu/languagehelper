package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.adapter.DailySentenceListsAdapter;
import com.messi.languagehelper.dao.EveryDaySentence;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ViewUtil;
import com.squareup.picasso.Picasso;

public class DailySentenceActivity extends BaseActivity implements OnClickListener {

	private ListView recent_used_lv;
	private LayoutInflater mInflater;
	private DailySentenceListsAdapter mAdapter;
	private List<EveryDaySentence> beans;
	private MediaPlayer mPlayer;
	private Bundle bundle;
	private int maxNumber = 0;
	
	private IFLYNativeAd nativeAd;
	private FrameLayout ad_layout;
	private ImageView ad_img;
	private List<NativeADDataRef> adList;
	private int index;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.daily_sentence_activity);
		init();
		new GetDataTask().execute();
	}
	
	private void init() {
		getSupportActionBar().setTitle(getResources().getString(R.string.dailysentence));
		mPlayer = new MediaPlayer();
		mInflater = LayoutInflater.from(this);
		recent_used_lv = (ListView) findViewById(R.id.studycategory_lv);
		
		View headerView = mInflater.inflate(R.layout.xunfei_ysad_item, null);
		ad_layout = (FrameLayout) headerView.findViewById(R.id.ad_layout);
		ad_img = (ImageView) headerView.findViewById(R.id.ad_img);
		recent_used_lv.addHeaderView(headerView);
		
		beans = new ArrayList<EveryDaySentence>();
		mAdapter = new DailySentenceListsAdapter(this, mInflater, beans, mPlayer, mProgressbar);
		recent_used_lv.setAdapter(mAdapter);
		showAD();
	}
	
	private void showAD(){
		if(ADUtil.isShowAd(this)){
			nativeAd = new IFLYNativeAd(this, ADUtil.MRYJYSNRLAd,
					new IFLYNativeListener() {
				@Override
				public void onAdFailed(AdError arg0) {
					LogUtil.DefalutLog("onAdFailed---"+arg0.getErrorCode()+"---"+arg0.getErrorDescription());
				}
				@Override
				public void onADLoaded(List<NativeADDataRef> arg0) {
					if(arg0 != null && arg0.size() > 0){
						adList = arg0;
						setAdData();
					}
				}
			});
			nativeAd.loadAd(1);
		}
	}
	
	private void setAdData(){
			Picasso.with(DailySentenceActivity.this)
			.load(adList.get(index).getImage())
			.tag(DailySentenceActivity.this)
			.into(ad_img);
			adList.get(index).onExposured(ad_layout);
			ad_layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					adList.get(index).onClicked(v);
				}
			});
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
		LogUtil.DefalutLog("CollectedFragment-onDestroy");
	}

	@Override
	public void onClick(View v) {
		
	}
}

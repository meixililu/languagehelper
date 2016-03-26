package com.messi.languagehelper;

import com.avos.avoscloud.AVAnalytics;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.GytUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.XFYSAD;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import cn.contentx.ContExManager;

public class LeisureFragment extends BaseFragment implements OnClickListener {

	public static final long IntervalTime = 1000 * 20;
	private View view;
	private FrameLayout cailing_layout,app_layout,yuedu_layout,hotal_layout;
	private FrameLayout invest_layout, game_layout,baidu_layout;
	private FrameLayout news_layout;
	private RelativeLayout xx_ad_layout;
	public static LeisureFragment mMainFragment;
	private SharedPreferences mSharedPreferences;
	private XFYSAD mXFYSAD;
	private boolean misVisibleToUser;
	
	public static LeisureFragment getInstance(){
		if(mMainFragment == null){
			mMainFragment = new LeisureFragment();
		}
		return mMainFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.leisure_fragment, null);
		initViews();
		return view;
	}
	
	private void initViews(){
		mSharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
		cailing_layout = (FrameLayout) view.findViewById(R.id.cailing_layout);
		baidu_layout = (FrameLayout)view.findViewById(R.id.baidu_layout);
		game_layout = (FrameLayout)view.findViewById(R.id.game_layout);
		yuedu_layout = (FrameLayout)view.findViewById(R.id.yuedu_layout);
		hotal_layout = (FrameLayout)view.findViewById(R.id.hotal_layout);
		app_layout = (FrameLayout)view.findViewById(R.id.app_layout);
		invest_layout = (FrameLayout)view.findViewById(R.id.invest_layout);
		news_layout = (FrameLayout)view.findViewById(R.id.news_layout);
		xx_ad_layout = (RelativeLayout)view.findViewById(R.id.xx_ad_layout);
		mXFYSAD = new XFYSAD(getActivity(), xx_ad_layout, ADUtil.XiuxianYSNRLAd);
		mXFYSAD.setStopPlay(true);
		mXFYSAD.showAD();
		if(misVisibleToUser){
			mXFYSAD.startPlayImg();
		}
		cailing_layout.setOnClickListener(this);
		yuedu_layout.setOnClickListener(this);
		hotal_layout.setOnClickListener(this);
		app_layout.setOnClickListener(this);
		invest_layout.setOnClickListener(this);
		baidu_layout.setOnClickListener(this);
		game_layout.setOnClickListener(this);
		news_layout.setOnClickListener(this);
	}
	
	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.DefalutLog("LeisureFragment---setUserVisibleHint");
        misVisibleToUser = isVisibleToUser;
        if(isVisibleToUser){
        	if(mXFYSAD != null){
        		mXFYSAD.startPlayImg();
        	}
        }else{
        	if(mXFYSAD != null){
        		mXFYSAD.canclePlayImg();
        	}
        }
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(misVisibleToUser){
			LogUtil.DefalutLog("LeisureFragment---onResume");
			if(mXFYSAD != null){
				mXFYSAD.startPlayImg();
			}
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(misVisibleToUser){
			LogUtil.DefalutLog("LeisureFragment---onPause");
			if(mXFYSAD != null){
				mXFYSAD.canclePlayImg();
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.cailing_layout){
			toCailingActivity();
		}else if(v.getId() == R.id.yuedu_layout){
			toYueduActivity();
		}else if(v.getId() == R.id.app_layout){
			toAppActivity();
		}else if(v.getId() == R.id.invest_layout){
			toInvestorListActivity();
		}else if(v.getId() == R.id.game_layout){
			toGameCenterActivity();
		}else if(v.getId() == R.id.baidu_layout){
			toBaiduActivity();
		}else if(v.getId() == R.id.news_layout){
			ContExManager.initWithAPPId(getActivity(),"f9136944-bc17-4cb1-9b14-ece9de91b39d", "w1461Eub");
			GytUtil.showHtml(getActivity(), getActivity().getResources().getString(R.string.leisuer_news));
		}
	}
	
	private void toBaiduActivity(){
		Intent intent = new Intent(getActivity(),JokeActivity.class);
		getActivity().startActivity(intent);
	}
	
	private void toInvestorListActivity(){
		Intent intent = new Intent(getActivity(),WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, Settings.InvestListUrl);
		intent.putExtra(KeyUtil.ActionbarTitle, getActivity().getResources().getString(R.string.invest_activity_title));
		getActivity().startActivity(intent);
		AVAnalytics.onEvent(getActivity(), "leisure_pg_toinvestpg_btn");
	}
	
	private void toGameCenterActivity(){
		Intent intent = new Intent(getActivity(),WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, Settings.GameUrl);
		intent.putExtra(KeyUtil.ActionbarTitle, getActivity().getResources().getString(R.string.leisuer_game));
		getActivity().startActivity(intent);
		AVAnalytics.onEvent(getActivity(), "leisure_pg_togamepg_btn");
	}
	
	private void toCailingActivity(){
		Intent intent = new Intent(getActivity(),WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, Settings.CaiLingUrl);
		intent.putExtra(KeyUtil.ActionbarTitle, getActivity().getResources().getString(R.string.title_cailing));
		getActivity().startActivity(intent);
		AVAnalytics.onEvent(getActivity(), "leisure_pg_tocailingpg_btn");
	}
	
	private void toYueduActivity(){
		Intent intent = new Intent(getActivity(),WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, Settings.YueduUrl);
		intent.putExtra(KeyUtil.ActionbarTitle, getActivity().getResources().getString(R.string.title_reading));
		intent.putExtra(KeyUtil.IsReedPullDownRefresh, false);
		getActivity().startActivity(intent);
		AVAnalytics.onEvent(getActivity(), "leisure_pg_toyuedupg_btn");
	}
	
	private void toAppActivity(){
		Intent intent = new Intent(getActivity(),AppRecommendListActivity.class);
		getActivity().startActivity(intent);
		AVAnalytics.onEvent(getActivity(),"leisure_pg_toapprecommendpg_btn");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mXFYSAD != null){
    		mXFYSAD.canclePlayImg();
    		mXFYSAD = null;
    	}
	}
}

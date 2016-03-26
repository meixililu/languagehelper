package com.messi.languagehelper.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bumptech.glide.Glide;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.R;
import com.messi.languagehelper.adapter.ViewPagerAdapter;
import com.messi.languagehelper.views.ProportionalImageView;
import com.messi.languagehelper.views.WrapContentHeightViewPager;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class XFYSAD {
	
	public static boolean isReverse = false;
	private Context mContext;
	private View parentView;
	private IFLYNativeAd nativeAd;
	private List<NativeADDataRef> adList;
	private WrapContentHeightViewPager auto_view_pager;
	private LinearLayout viewpager_dot_layout;
	private LayoutInflater mInflater;
	private Handler mHandler;
	private ArrayList<View> views;
	private ViewPagerAdapter vpAdapter;
	private int Index;
	private String adId;
	private int retryTime;
	private boolean isStopPlay;
	private int addCount = ADUtil.adCount;
	private long lastLoadAdTime;
	private boolean isStartPlay;
	
	public XFYSAD(Context mContext,View parentView,String adId){
		this.mContext = mContext;
		this.parentView = parentView;
		this.adId = adId;
		mInflater = LayoutInflater.from(mContext);
		mHandler = new Handler();
		auto_view_pager = (WrapContentHeightViewPager)parentView.findViewById(R.id.auto_view_pager);
		viewpager_dot_layout = (LinearLayout)parentView.findViewById(R.id.viewpager_dot_layout);
		parentView.setVisibility(View.GONE);
		lastLoadAdTime = System.currentTimeMillis();
		
		views = new ArrayList<View>();
		vpAdapter = new ViewPagerAdapter(views);
		auto_view_pager.setAdapter(vpAdapter);
        auto_view_pager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			changeAd();
			mHandler.postDelayed(mRunnable, ADUtil.adInterval);
			LogUtil.DefalutLog("---mRunnable run---");
		}
	};
	
	private void changeAd(){
		if(auto_view_pager != null && adList != null){
			Index++;
			if(Index >= adList.size()){
				Index = 0;
			}
			auto_view_pager.setCurrentItem(Index);
		}
	}
	
	public void startPlayImg(){
		if(!isStartPlay){
			isStartPlay = true;
			if(mHandler != null){
				if(adList != null && adList.size() > 1){
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							changeAd();
						}
					}, 400);
					LogUtil.DefalutLog("---start changeAd---");
				}
				mHandler.postDelayed(mRunnable, ADUtil.adInterval);
				if(System.currentTimeMillis() - lastLoadAdTime > 1000*35){
					isStopPlay = true;
					lastLoadAdTime = System.currentTimeMillis();
					showAD();
				}
			}
		}
	}
	
	public void canclePlayImg(){
		if(mHandler != null){
			isStartPlay = false;
			mHandler.removeCallbacks(mRunnable);
			LogUtil.DefalutLog("---cancle changeAd---");
		}
	}
	
	public void showAD(){
		if(ADUtil.isShowAd(mContext)){
			loadData();
		}else{
			parentView.setVisibility(View.GONE);
		}
	}
	
	private void loadData(){
		LogUtil.DefalutLog("---load ad Data---");
		nativeAd = new IFLYNativeAd(mContext, adId, new IFLYNativeListener() {
			@Override
			public void onAdFailed(AdError arg0) {
				parentView.setVisibility(View.GONE);
				LogUtil.DefalutLog("onAdFailed---"+arg0.getErrorCode()+"---"+arg0.getErrorDescription());
				if(retryTime < 1){
					retryTime ++;
					if(adId.equals(ADUtil.XiuxianYSNRLAd)){
						adId = ADUtil.MRYJYSNRLAd;
					}else if(adId.equals(ADUtil.MRYJYSNRLAd)){
						adId = ADUtil.XiuxianYSNRLAd;
					}
					showAD();
				}
			}
			@Override
			public void onADLoaded(List<NativeADDataRef> arg0) {
				if(arg0 != null && arg0.size() > 0){
					adList = arg0;
					LogUtil.DefalutLog("---onADLoaded---");
					try {
						setAdData();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		nativeAd.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
		nativeAd.loadAd(addCount);
	}
	
	private void setAdData() throws Exception{
		parentView.setVisibility(View.VISIBLE);
		views.clear();
		if(isReverse){
			Collections.reverse(adList);
		}
		isReverse = !isReverse;
		for(int i=0; i < adList.size() ;i++){
			views.add(getAdItem(adList.get(i)));
		}
        if(adList.size() > 1){
        	viewpager_dot_layout.removeAllViews();
        	for(int i=0;i<adList.size();i++){
        		viewpager_dot_layout.addView( ViewUtil.getDot(mContext,i) );
        	}
        	viewpager_dot_layout.setVisibility(View.VISIBLE);
        	ViewUtil.changeState(viewpager_dot_layout, 0);
        }else{
        	viewpager_dot_layout.setVisibility(View.GONE);
        }
        adList.get(0).onExposured(auto_view_pager);
        auto_view_pager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				canclePlayImg();
				return false;
			}
		});
        vpAdapter.notifyDataSetChanged();
        if(!isStopPlay){
        	startPlayImg();
        }
	}
	
	public View getAdItem(final NativeADDataRef mNativeADDataRef) throws Exception{
		View convertView = mInflater.inflate(R.layout.ad_viewpager_item, null);
		FrameLayout cover = (FrameLayout) convertView.findViewById(R.id.ad_layout);
		ProportionalImageView ad_img = (ProportionalImageView) convertView.findViewById(R.id.ad_img);
		
		Glide.with(mContext)
		.load(mNativeADDataRef.getImage())
		.into(ad_img);
		cover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mNativeADDataRef.onClicked(v);
			}
		});
		return convertView;
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
        	Index = position;
        	if(adList != null && adList.size() > position){
        		adList.get(position).onExposured(auto_view_pager);
        		ViewUtil.changeState(viewpager_dot_layout, position);
        	}
        }
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override
        public void onPageScrollStateChanged(int arg0) {}
    }
	
	public boolean isStopPlay() {
		return isStopPlay;
	}

	public void setStopPlay(boolean isStopPlay) {
		this.isStopPlay = isStopPlay;
	}

	public int getAddCount() {
		return addCount;
	}

	public void setAddCount(int addCount) {
		this.addCount = addCount;
	}
	
	
}

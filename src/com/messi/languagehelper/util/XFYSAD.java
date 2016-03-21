package com.messi.languagehelper.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bumptech.glide.Glide;
import com.iflytek.voiceads.AdError;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
	private int Index;
	private String adId;
	private int retryTime;
	public static List<NativeADDataRef> adList_xx;
	public static List<NativeADDataRef> adList_mryj;
	private boolean isStopPlay;
	
	public XFYSAD(Context mContext,View parentView,String adId){
		this.mContext = mContext;
		this.parentView = parentView;
		this.adId = adId;
		mInflater = LayoutInflater.from(mContext);
		mHandler = new Handler();
		auto_view_pager = (WrapContentHeightViewPager)parentView.findViewById(R.id.auto_view_pager);
		viewpager_dot_layout = (LinearLayout)parentView.findViewById(R.id.viewpager_dot_layout);
		parentView.setVisibility(View.GONE);
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
		if(mHandler != null){
			if(adList != null && adList.size() > 0){
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						changeAd();
					}
				}, 400);
				mHandler.postDelayed(mRunnable, ADUtil.adInterval);
				LogUtil.DefalutLog("---startPlayImg---");
			}
		}
	}
	
	public void canclePlayImg(){
		if(mHandler != null){
			mHandler.removeCallbacks(mRunnable);
			LogUtil.DefalutLog("---canclePlayImg---");
		}
	}
	
	public void showAD(){
		if(ADUtil.isShowAd(mContext)){
			if(adId.equals(ADUtil.XiuxianYSNRLAd)){
				if(adList_xx != null){
					adList = adList_xx;
					delaySetData();
				}else if(adList_mryj != null){
					adList = adList_mryj;
					delaySetData();
				}else{
					loadData();
				}
			}else if(adId.equals(ADUtil.MRYJYSNRLAd)){
				if(adList_mryj != null){
					adList = adList_mryj;
					delaySetData();
				}else if(adList_xx != null){
					adList = adList_xx;
					delaySetData();
				}else{
					loadData();
				}
			}
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
					if(adId.equals(ADUtil.XiuxianYSNRLAd)){
						adList_xx = arg0;
					}else if(adId.equals(ADUtil.MRYJYSNRLAd)){
						adList_mryj = arg0;
					}
					adList = arg0;
					try {
						setAdData();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		nativeAd.loadAd(ADUtil.adCount);
	}
	
	private void delaySetData() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					setAdData();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 400);
	}
	
	private void setAdData() throws Exception{
		parentView.setVisibility(View.VISIBLE);
		if(isReverse){
			Collections.reverse(adList);
		}
		isReverse = !isReverse;
		ArrayList<View> views = new ArrayList<View>();
		for(int i=0; i < adList.size() ;i++){
			views.add(getAdItem(adList.get(i)));
		}
		ViewPagerAdapter vpAdapter = new ViewPagerAdapter(views);
		auto_view_pager.setAdapter(vpAdapter);
        auto_view_pager.setOnPageChangeListener(new MyOnPageChangeListener());
        for(int i=0;i<adList.size();i++){
        	viewpager_dot_layout.addView( ViewUtil.getDot(mContext,i) );
        }
        if(adList.size() < 1){
        	viewpager_dot_layout.setVisibility(View.GONE);
        }else{
        	viewpager_dot_layout.setVisibility(View.VISIBLE);
        }
        adList.get(0).onExposured(auto_view_pager);
        ViewUtil.changeState(viewpager_dot_layout, 0);
        auto_view_pager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				canclePlayImg();
				return false;
			}
		});
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
        	adList.get(position).onExposured(auto_view_pager);
        	ViewUtil.changeState(viewpager_dot_layout, position);
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
	
	public static void setAd(final Context context, final View view){
		if(adList_xx == null && adList_mryj == null){
			view.setVisibility(View.GONE);
			return;
		}else{
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					view.setVisibility(View.VISIBLE);
					FrameLayout ad_layout = (FrameLayout) view.findViewById(R.id.ad_layout);
					ProportionalImageView ad_img = (ProportionalImageView) view.findViewById(R.id.ad_img);
					final int index = (int) Math.round(Math.random()*(ADUtil.adCount-1));
					if(adList_xx != null && adList_xx.size() > 0){
						Glide.with(context)
						.load(adList_xx.get(index).getImage())
						.into(ad_img);
						adList_xx.get(index).onExposured(ad_layout);
						ad_layout.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								adList_xx.get(index).onClicked(v);
							}
						});
					}else if(adList_mryj != null && adList_mryj.size() > 0){
						Glide.with(context)
						.load(adList_mryj.get(index).getImage())
						.into(ad_img);
						adList_mryj.get(index).onExposured(ad_layout);
						ad_layout.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								adList_mryj.get(index).onClicked(v);
							}
						});
					}
				}
			}, 500);
		}
	}
}

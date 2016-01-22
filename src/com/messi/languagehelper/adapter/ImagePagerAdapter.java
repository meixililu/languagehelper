package com.messi.languagehelper.adapter;

/*
 * Copyright 2014 trinea.cn All right reserved. This software is the confidential and proprietary information of
 * trinea.cn ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with trinea.cn.
 */
import java.util.List;

import com.bumptech.glide.Glide;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.R;
import com.messi.languagehelper.views.ProportionalImageView;
import com.messi.languagehelper.views.RecyclingPagerAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;


/**
 * ImagePagerAdapter
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2014-2-23
 */
public class ImagePagerAdapter extends RecyclingPagerAdapter {

    private Context context;
    private LayoutInflater mInflater;
    private List<NativeADDataRef> adList;
    private int           size;
    private boolean       isInfiniteLoop;

    public ImagePagerAdapter(Context context, List<NativeADDataRef> adList) {
        this.context = context;
        this.adList = adList;
        this.size = adList.size();
        isInfiniteLoop = false;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // Infinite loop
        return isInfiniteLoop ? Integer.MAX_VALUE : adList.size();
    }

    /**
     * get really position
     * 
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return isInfiniteLoop ? position % size : position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup container) {
    	final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.ad_viewpager_item, null);
			holder = new ViewHolder();
			holder.cover = (FrameLayout) convertView.findViewById(R.id.ad_layout);
			holder.ad_img = (ProportionalImageView) convertView.findViewById(R.id.ad_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		Glide.with(context)
		.load(adList.get(position).getImage())
		.into(holder.ad_img);
		adList.get(position).onExposured(holder.cover);
		holder.cover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				adList.get(position).onClicked(v);
			}
		});
        return convertView;
    }

    private static class ViewHolder {
    	FrameLayout cover;
    	ProportionalImageView ad_img;
    }

    /**
     * @return the isInfiniteLoop
     */
    public boolean isInfiniteLoop() {
        return isInfiniteLoop;
    }

    /**
     * @param isInfiniteLoop the isInfiniteLoop to set
     */
    public ImagePagerAdapter setInfiniteLoop(boolean isInfiniteLoop) {
        this.isInfiniteLoop = isInfiniteLoop;
        return this;
    }
}
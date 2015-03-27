package com.messi.languagehelper.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.messi.languagehelper.R;
import com.messi.languagehelper.bean.UserSpeakBean;
import com.messi.languagehelper.util.LogUtil;

public class PracticePageListItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private ArrayList<UserSpeakBean> beans;

	public PracticePageListItemAdapter(Context mContext,ArrayList<UserSpeakBean> mUserSpeakBean) {
		context = mContext;
		this.mInflater = LayoutInflater.from(mContext);
		this.beans = mUserSpeakBean;
	}

	public int getCount() {
		return beans.size();
	}

	public Object getItem(int position) {
		return beans.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LogUtil.DefalutLog("CollectedListItemAdapter---getView");
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.practice_activity_lv_item, null);
			holder = new ViewHolder();
			holder.user_speak_content = (TextView) convertView.findViewById(R.id.user_speak_content);
			holder.user_speak_score = (TextView) convertView.findViewById(R.id.user_speak_score);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final UserSpeakBean mBean = beans.get(position);
		holder.user_speak_content.setText(mBean.getContent());
		holder.user_speak_score.setText(mBean.getScore());
		int color = context.getResources().getColor(mBean.getColor());
		holder.user_speak_score.setTextColor(color);
		holder.user_speak_score.setShadowLayer(1f, 1f, 1f, color);
		return convertView;
	}
	
	static class ViewHolder {
		TextView user_speak_content;
		TextView user_speak_score;
	}
	

}

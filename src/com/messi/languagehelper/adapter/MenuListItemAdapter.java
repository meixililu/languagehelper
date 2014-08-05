package com.messi.languagehelper.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.messi.languagehelper.R;
import com.messi.languagehelper.util.LogUtil;

public class MenuListItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private String[] mPlanetTitles;

	public MenuListItemAdapter(Context mContext,String[] mPlanetTitles) {
		context = mContext;
		this.mInflater = LayoutInflater.from(mContext);
		this.mPlanetTitles = mPlanetTitles;
	}

	public int getCount() {
		return mPlanetTitles.length;
	}

	public Object getItem(int position) {
		return mPlanetTitles[position];
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LogUtil.DefalutLog("CollectedListItemAdapter---getView");
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.menu_lv_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.unread_dot = (ImageView) convertView.findViewById(R.id.unread_dot);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(mPlanetTitles[position]);
//		holder.unread_dot;
		return convertView;
	}
	
	static class ViewHolder {
		TextView title;
		ImageView unread_dot;
	}
	

}

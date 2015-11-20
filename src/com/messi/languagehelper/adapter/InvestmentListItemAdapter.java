package com.messi.languagehelper.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.messi.languagehelper.PracticeDetailActivity;
import com.messi.languagehelper.R;
import com.messi.languagehelper.util.ColorUtil;
import com.messi.languagehelper.util.KeyUtil;

public class InvestmentListItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private String[] studylist_part;
	private int[] colors;
	private String level;

	public InvestmentListItemAdapter(Context mContext, String[] mPlanetTitles, String level) {
		context = mContext;
		this.level = level;
		this.mInflater = LayoutInflater.from(mContext);
		this.studylist_part = mPlanetTitles;
		colors = new int[studylist_part.length];
		ColorUtil.setColor(studylist_part.length, colors);
	}

	public int getCount() {
		return studylist_part.length;
	}

	public Object getItem(int position) {
		return studylist_part[position];
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.studylist_item, null);
			holder = new ViewHolder();
			holder.cover = (View) convertView.findViewById(R.id.layout_cover);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		try{
			holder.cover.setBackgroundColor(context.getResources().getColor(colors[position]));
		}catch(Exception e){
			holder.cover.setBackgroundColor(context.getResources().getColor(R.color.style1_color1));
			e.printStackTrace();
		}
		holder.name.setText(studylist_part[position]);
		return convertView;
	}

	static class ViewHolder {
		View cover;
		TextView name;
	}

	public void onItemClick(int position) {
		try {
			Intent intent = new Intent(context,PracticeDetailActivity.class);
			intent.putExtra(KeyUtil.PracticeContentKey, position);
			intent.putExtra(KeyUtil.LevelKey, level);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}

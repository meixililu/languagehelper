package com.messi.languagehelper.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.R;
import com.messi.languagehelper.StudyActivity;
import com.messi.languagehelper.util.ColorUtil;
import com.messi.languagehelper.util.KeyUtil;

public class StudyListItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private String[] studylist_part;
	private int[] colors;
	private String level;

	public StudyListItemAdapter(Context mContext, String[] mPlanetTitles, String level) {
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
		holder.cover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onItemClick(position);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		View cover;
		TextView name;
	}

	public void onItemClick(int position) {
		try {
			Intent intent = new Intent(context,StudyActivity.class);
			intent.putExtra(KeyUtil.PracticeContentKey, position);
			intent.putExtra(KeyUtil.LevelKey, level);
			context.startActivity(intent);
			StatService.onEvent(context, "19_to_practice_page", "进入口语学习页面", 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}

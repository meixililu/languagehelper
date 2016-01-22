package com.messi.languagehelper.adapter;

import com.messi.languagehelper.R;
import com.messi.languagehelper.WordStudyDetailActivity;
import com.messi.languagehelper.dao.WordListItem;
import com.messi.languagehelper.util.KeyUtil;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class WordStudyUnitListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private WordListItem avObjects;
	
	public WordStudyUnitListAdapter(Context mContext, WordListItem avObjects) {
		context = mContext;
		this.mInflater = LayoutInflater.from(mContext);
		this.avObjects = avObjects;
	}

	public int getCount() {
		return avObjects.getCourse_num();
	}

	public String getItem(int position) {
		return String.valueOf(position+1);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.word_study_unit_list_item, null);
			holder = new ViewHolder();
			holder.cover = (View) convertView.findViewById(R.id.layout_cover);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText( getItem(position) );
		holder.cover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onItemClick(getItem(position));
			}
		});
		return convertView;
	}

	static class ViewHolder {
		View cover;
		TextView name;
	}

	private void onItemClick(String course_id){
		Intent intent = new Intent(context,WordStudyDetailActivity.class);
		intent.putExtra(KeyUtil.ClassName, avObjects.getTitle());
		intent.putExtra(KeyUtil.CourseId, Integer.parseInt(course_id));
		intent.putExtra(KeyUtil.CourseNum, avObjects.getCourse_num());
		intent.putExtra(KeyUtil.ClassId, avObjects.getClass_id());
		context.startActivity(intent);
	}
	

}

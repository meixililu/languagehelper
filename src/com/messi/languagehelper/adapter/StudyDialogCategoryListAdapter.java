package com.messi.languagehelper.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.messi.languagehelper.R;
import com.messi.languagehelper.StudyDialogActivity;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.ToastUtil;

public class StudyDialogCategoryListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private String[] studyCategoryList;

	public StudyDialogCategoryListAdapter(Context mContext, String[] mPlanetTitles) {
		context = mContext;
		this.mInflater = LayoutInflater.from(mContext);
		this.studyCategoryList = mPlanetTitles;
	}

	public int getCount() {
		return studyCategoryList.length;
	}

	public Object getItem(int position) {
		return studyCategoryList[position];
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
		holder.name.setText(studyCategoryList[position]);
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

	private void onItemClick(int position){
		if(position == 0){
			Intent intent = new Intent(context,StudyDialogActivity.class);
			intent.putExtra(KeyUtil.LevelKey, "dialog");
			context.startActivity(intent);
		}else{
			ToastUtil.diaplayMesLong(context, "开发中，敬请期待！");
		}
	}
	

}

package com.messi.languagehelper.adapter;

import java.util.List;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVObject;
import com.messi.languagehelper.PracticeDetailActivity;
import com.messi.languagehelper.R;
import com.messi.languagehelper.util.AVOUtil;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PracticeCategoryListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private List<AVObject> avObjects;
	private String PCCode;

	public PracticeCategoryListAdapter(Context mContext, List<AVObject> avObjects, String level) {
		context = mContext;
		this.PCCode = level;
		this.mInflater = LayoutInflater.from(mContext);
		this.avObjects = avObjects;
	}

	public int getCount() {
		return avObjects.size();
	}

	public Object getItem(int position) {
		return avObjects.get(position);
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
		final AVObject mAVObject = avObjects.get(position);
		holder.name.setText( mAVObject.getString(AVOUtil.PracticeCategoryList.PCLName) );
		holder.cover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onItemClick(mAVObject);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		View cover;
		TextView name;
	}

	public void onItemClick(AVObject mAVObject) {
		try {
			Intent intent = new Intent(context,PracticeDetailActivity.class);
			intent.putExtra(AVOUtil.PracticeCategoryList.PCLCode, mAVObject.getString(AVOUtil.PracticeCategoryList.PCLCode));
			intent.putExtra(AVOUtil.PracticeCategory.PCCode, PCCode);
			context.startActivity(intent);
			AVAnalytics.onEvent(context, "study_list_to_practice_page");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}

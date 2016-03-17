package com.messi.languagehelper.adapter;

import java.util.List;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVObject;
import com.messi.languagehelper.BaseApplication;
import com.messi.languagehelper.EvaluationDetailActivity;
import com.messi.languagehelper.R;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.KeyUtil;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EvaluationCategoryListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private List<AVObject> avObjects;
	private String ECCode;

	public EvaluationCategoryListAdapter(Context mContext, List<AVObject> avObjects, String level) {
		context = mContext;
		this.ECCode = level;
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
			convertView = mInflater.inflate(R.layout.evaluation_list_item, null);
			holder = new ViewHolder();
			holder.cover = (View) convertView.findViewById(R.id.layout_cover);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.des = (TextView) convertView.findViewById(R.id.des);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final AVObject mAVObject = avObjects.get(position);
		String temp = mAVObject.getString(AVOUtil.EvaluationDetail.EDContent);
		if(temp.contains("#")){
			String[] strs = temp.split("#");
			holder.name.setText( strs[0] );
			holder.des.setText( strs[1] );
		}else{
			holder.name.setText( temp );
			holder.des.setVisibility(View.GONE);
		}
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
		TextView des;
	}

	public void onItemClick(int position) {
		try {
			Intent intent = new Intent(context,EvaluationDetailActivity.class);
			intent.putExtra(KeyUtil.PositionKey, position);
			BaseApplication.dataMap.put(KeyUtil.DataMapKey, avObjects);
			context.startActivity(intent);
			AVAnalytics.onEvent(context, "to_evaluation_detail");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}

package com.messi.languagehelper.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.baidu.mobstat.StatService;
import com.messi.languagehelper.EvaluationDetailActivity;
import com.messi.languagehelper.R;
import com.messi.languagehelper.util.AVOUtil;

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
			convertView = mInflater.inflate(R.layout.studylist_item, null);
			holder = new ViewHolder();
			holder.cover = (View) convertView.findViewById(R.id.layout_cover);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final AVObject mAVObject = avObjects.get(position);
		holder.name.setText( mAVObject.getString(AVOUtil.EvaluationCategoryList.ECLName) );
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
			Intent intent = new Intent(context,EvaluationDetailActivity.class);
			intent.putExtra(AVOUtil.EvaluationCategoryList.ECLCode, mAVObject.getString(AVOUtil.EvaluationCategoryList.ECLCode));
			intent.putExtra(AVOUtil.EvaluationCategory.ECCode, ECCode);
			context.startActivity(intent);
			StatService.onEvent(context, "to_evaluation_detail", "口语评测进入详情页面", 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}

package com.messi.languagehelper.adapter;

import java.util.List;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.bumptech.glide.Glide;
import com.messi.languagehelper.R;
import com.messi.languagehelper.RecommendActivity;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.views.ProportionalImageView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

public class AppRecommendListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private List<AVObject> avObjects;
	
	public AppRecommendListAdapter(Context mContext, List<AVObject> avObjects) {
		context = mContext;
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
			convertView = mInflater.inflate(R.layout.app_recommend_list_item, null);
			holder = new ViewHolder();
			holder.cover = (FrameLayout) convertView.findViewById(R.id.layout_cover);
			holder.list_item_img = (ProportionalImageView) convertView.findViewById(R.id.list_item_img);
			holder.item_name = (TextView) convertView.findViewById(R.id.item_name);
			holder.item_msg = (TextView) convertView.findViewById(R.id.item_msg);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final AVObject mAVObject = avObjects.get(position);
		try {
			AVFile avFile = mAVObject.getAVFile(AVOUtil.AppRecommendList.AppTypeIcon);
			Glide.with(context)
			.load(avFile.getUrl())
			.into(holder.list_item_img);
			holder.item_name.setText( mAVObject.getString(AVOUtil.AppRecommendList.AppTypeName) );
			holder.item_msg.setText( mAVObject.getString(AVOUtil.AppRecommendList.AppTypeDescribe) );
			holder.cover.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					onItemClick(mAVObject);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	static class ViewHolder {
		FrameLayout cover;
		ProportionalImageView list_item_img;
		TextView item_name;
		TextView item_msg;
	}

	private void onItemClick(AVObject mAVObject){
		Intent intent = new Intent(context,RecommendActivity.class);
		intent.putExtra(KeyUtil.ActionbarTitle, mAVObject.getString(AVOUtil.AppRecommendList.AppTypeName));
		intent.putExtra(AVOUtil.AppRecommendList.AppTypeCode, mAVObject.getString(AVOUtil.AppRecommendList.AppTypeCode));
		context.startActivity(intent);
	}
	

}

package com.messi.languagehelper.adapter;

import java.util.List;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVObject;
import com.messi.languagehelper.R;
import com.messi.languagehelper.WebViewActivity;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.KeyUtil;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

public class EnglishWebsiteListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private List<AVObject> avObjects;
	
	public EnglishWebsiteListAdapter(Context mContext, List<AVObject> avObjects) {
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
			convertView = mInflater.inflate(R.layout.english_website_list_item, null);
			holder = new ViewHolder();
			holder.cover = (FrameLayout) convertView.findViewById(R.id.english_website_layout2);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.describe = (TextView) convertView.findViewById(R.id.describe);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final AVObject mAVObject = avObjects.get(position);
		holder.title.setText( mAVObject.getString(AVOUtil.EnglishWebsite.Title) );
		holder.describe.setText( mAVObject.getString(AVOUtil.EnglishWebsite.Describe) );
		holder.cover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onItemClick(mAVObject);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		FrameLayout cover;
		TextView title;
		TextView describe;
	}
	
	private void onItemClick(AVObject mAVObject){
		Intent intent = new Intent(context,WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, mAVObject.getString(AVOUtil.EnglishWebsite.Url));
		intent.putExtra(KeyUtil.ActionbarTitle, mAVObject.getString(AVOUtil.EnglishWebsite.Title));
		intent.putExtra(KeyUtil.ShareUrlMsg, mAVObject.getString(AVOUtil.EnglishWebsite.ShareMsg));
		context.startActivity(intent);
		AVAnalytics.onEvent(context, "website_view_detail");
	}
	

}

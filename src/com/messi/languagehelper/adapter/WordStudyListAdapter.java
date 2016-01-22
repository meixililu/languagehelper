package com.messi.languagehelper.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.messi.languagehelper.BaseApplication;
import com.messi.languagehelper.R;
import com.messi.languagehelper.WordBookListActivity;
import com.messi.languagehelper.dao.WordListType;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.ScreenUtil;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WordStudyListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private List<WordListType> avObjects;
	
	public WordStudyListAdapter(Context mContext, List<WordListType> avObjects) {
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
			convertView = mInflater.inflate(R.layout.word_study_root_list_item, null);
			holder = new ViewHolder();
			holder.cover = (View) convertView.findViewById(R.id.layout_cover);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.list_item_img = (ImageView) convertView.findViewById(R.id.list_item_img);
			holder.list_item_bg = (LinearLayout) convertView.findViewById(R.id.list_item_bg);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final WordListType mAVObject = avObjects.get(position);
		holder.name.setText( mAVObject.getTitle() );
		
		if(TextUtils.isEmpty(mAVObject.getImg_url())){
			holder.list_item_bg.setBackgroundColor(context.getResources().getColor(R.color.white));
			holder.name.setTextColor(context.getResources().getColor(R.color.text_black));
			holder.list_item_img.setVisibility(View.GONE);;
			holder.name.setPadding(ScreenUtil.dip2px(context, 15), ScreenUtil.dip2px(context, 30), 0, ScreenUtil.dip2px(context, 30));
		}else{
			holder.list_item_bg.setBackgroundColor(context.getResources().getColor(R.color.alpha_5));
			holder.name.setTextColor(context.getResources().getColor(R.color.white));
			holder.list_item_img.setVisibility(View.VISIBLE);
			holder.name.setPadding(ScreenUtil.dip2px(context, 15), ScreenUtil.dip2px(context, 10), 0, ScreenUtil.dip2px(context, 10));
			Glide.with(context)
			.load(mAVObject.getImg_url())
			.into(holder.list_item_img);
		}
		
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
		LinearLayout list_item_bg;
		ImageView list_item_img;
	}

	private void onItemClick(WordListType mAVObject){
		BaseApplication.dataMap.put(KeyUtil.DataMapKey, mAVObject.getItemList());
		Intent intent = new Intent(context,WordBookListActivity.class);
		intent.putExtra(KeyUtil.ActionbarTitle, mAVObject.getTitle());
		context.startActivity(intent);
	}
	

}

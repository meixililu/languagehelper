package com.messi.languagehelper.adapter;

import java.util.List;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.bumptech.glide.Glide;
import com.messi.languagehelper.BaseApplication;
import com.messi.languagehelper.CompositionDetailActivity;
import com.messi.languagehelper.R;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.KeyUtil;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CompositionListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private List<AVObject> avObjects;
	
	public CompositionListAdapter(Context mContext, List<AVObject> avObjects) {
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
			convertView = mInflater.inflate(R.layout.composition_list_item, null);
			holder = new ViewHolder();
			holder.layout_cover = (FrameLayout) convertView.findViewById(R.id.layout_cover);
			holder.list_item_img_parent = (LinearLayout) convertView.findViewById(R.id.list_item_img_parent);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.smark = (TextView) convertView.findViewById(R.id.smark);
			holder.list_item_img = (ImageView) convertView.findViewById(R.id.list_item_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final AVObject mAVObject = avObjects.get(position);
		holder.title.setText( mAVObject.getString(AVOUtil.Composition.title) );
		holder.smark.setText( mAVObject.getString(AVOUtil.Composition.type_name) );
		String img_url = "";
		if(mAVObject.getString(AVOUtil.Composition.img_type).equals("url")){
			img_url = mAVObject.getString(AVOUtil.Composition.img_url);
		}else{
			AVFile mAVFile = mAVObject.getAVFile(AVOUtil.Composition.img);
			img_url = mAVFile.getUrl();
		}
		
		if(!TextUtils.isEmpty(img_url)){
			holder.list_item_img_parent.setVisibility(View.VISIBLE);
			holder.list_item_img.setVisibility(View.VISIBLE);
			Glide.with(context)
			.load(img_url)
			.into(holder.list_item_img);
		}else{
			holder.list_item_img_parent.setVisibility(View.GONE);
			holder.list_item_img.setVisibility(View.GONE);
		}
		
		holder.layout_cover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toDetailActivity(position);
			}
		});
		
		return convertView;
	}
	
	private void toDetailActivity(int position){
		BaseApplication.dataMap.put(KeyUtil.DataMapKey, avObjects);
		Intent intent = new Intent(context,CompositionDetailActivity.class);
		intent.putExtra(KeyUtil.IndexKey, position);
		context.startActivity(intent);
	}

	static class ViewHolder {
		FrameLayout layout_cover;
		TextView title;
		TextView smark;
		LinearLayout list_item_img_parent;
		ImageView list_item_img;
	}

}

package com.messi.languagehelper.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.messi.languagehelper.R;
import com.messi.languagehelper.dao.JokeContent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JokePictureListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private List<JokeContent> avObjects;
	
	public JokePictureListAdapter(Context mContext, List<JokeContent> avObjects) {
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
			convertView = mInflater.inflate(R.layout.joke_picture_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.list_item_img = (ImageView) convertView.findViewById(R.id.list_item_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final JokeContent mAVObject = avObjects.get(position);
		holder.name.setText( mAVObject.getTitle() );
		Glide.with(context)
		.load(mAVObject.getImg())
		.into(holder.list_item_img);
		return convertView;
	}

	static class ViewHolder {
		TextView name;
		ImageView list_item_img;
	}

}

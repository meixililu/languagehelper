package com.messi.languagehelper.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.messi.languagehelper.R;
import com.messi.languagehelper.dao.BDJContent;
import com.messi.languagehelper.dao.JokeContent;
import com.messi.languagehelper.views.ProportionalImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class JokeTextListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private List<JokeContent> avObjects;
	
	public JokeTextListAdapter(Context mContext, List<JokeContent> avObjects) {
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
			convertView = mInflater.inflate(R.layout.joke_text_list_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.text = (TextView) convertView.findViewById(R.id.text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final JokeContent mAVObject = avObjects.get(position);
		holder.title.setText( mAVObject.getTitle() );
		holder.text.setText( mAVObject.getText() );
		return convertView;
	}

	static class ViewHolder {
		TextView title;
		TextView text;
	}

}

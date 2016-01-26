package com.messi.languagehelper.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.messi.languagehelper.R;
import com.messi.languagehelper.dao.BDJContent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JokeListAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private List<BDJContent> avObjects;
	
	public JokeListAdapter(Context mContext, List<BDJContent> avObjects) {
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
			convertView = mInflater.inflate(R.layout.joke_bdj_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.des = (TextView) convertView.findViewById(R.id.des);
			holder.list_item_img = (ImageView) convertView.findViewById(R.id.list_item_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final BDJContent mAVObject = avObjects.get(position);
		holder.name.setText( mAVObject.getName() );
		holder.des.setText( mAVObject.getText() );
		
		if(mAVObject.getType().equals("10")){
			holder.list_item_img.setVisibility(View.VISIBLE);
			Glide.with(context)
			.load(mAVObject.getImage3())
			.into(holder.list_item_img);
		}else{
			holder.list_item_img.setVisibility(View.GONE);
			
		}
		return convertView;
	}

	static class ViewHolder {
		TextView name;
		TextView des;
		ImageView list_item_img;
	}

	private void onItemClick(BDJContent mAVObject){
//		BaseApplication.dataMap.put(KeyUtil.DataMapKey, mAVObject.getItemList());
//		Intent intent = new Intent(context,WordBookListActivity.class);
//		intent.putExtra(KeyUtil.ActionbarTitle, mAVObject.getTitle());
//		context.startActivity(intent);
	}
	

}

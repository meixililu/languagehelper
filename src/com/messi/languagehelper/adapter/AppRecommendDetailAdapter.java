package com.messi.languagehelper.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.gc.materialdesign.views.ButtonFlat;
import com.messi.languagehelper.R;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.AppDownloadUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.squareup.picasso.Picasso;

public class AppRecommendDetailAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private List<AVObject> avObjects;
	
	public AppRecommendDetailAdapter(Context mContext, List<AVObject> avObjects) {
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
			convertView = mInflater.inflate(R.layout.app_recommend_detail_list_item, null);
			holder = new ViewHolder();
			holder.cover = (FrameLayout) convertView.findViewById(R.id.layout_cover);
			holder.list_item_img = (ImageView) convertView.findViewById(R.id.list_item_img);
			holder.item_name = (TextView) convertView.findViewById(R.id.item_name);
			holder.item_msg = (TextView) convertView.findViewById(R.id.item_msg);
			holder.item_size = (TextView) convertView.findViewById(R.id.item_size);
			holder.download_btn = (ButtonFlat) convertView.findViewById(R.id.download_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final AVObject mAVObject = avObjects.get(position);
		try {
			AVFile avFile = mAVObject.getAVFile(AVOUtil.AppRecommendDetail.APPIcon);
			Picasso.with(context)
			.load(avFile.getUrl())
			.tag(context)
			.into(holder.list_item_img);
			holder.item_name.setText( mAVObject.getString(AVOUtil.AppRecommendDetail.AppName) );
			holder.item_size.setText( mAVObject.getString(AVOUtil.AppRecommendDetail.AppSize) );
			holder.item_msg.setText( mAVObject.getString(AVOUtil.AppRecommendDetail.AppDescribe) );
			holder.download_btn.setOnClickListener(new OnClickListener() {
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
		ImageView list_item_img;
		TextView item_name;
		TextView item_msg;
		TextView item_size;
		ButtonFlat download_btn;
	}

	private void onItemClick(AVObject mAVObject){
		String downloadType = mAVObject.getString(AVOUtil.AppRecommendDetail.DownloadType);
		String apkUrl = "";
		if(downloadType.equals("apk")){
			AVFile avFile = mAVObject.getAVFile(AVOUtil.AppRecommendDetail.Apk);
			apkUrl = avFile.getUrl();
		}else{
			apkUrl = mAVObject.getString(AVOUtil.AppRecommendDetail.APPUrl);
		}
		LogUtil.DefalutLog("apkUrl:"+apkUrl);
		new AppDownloadUtil(context,apkUrl,
				mAVObject.getString(AVOUtil.AppRecommendDetail.AppName),
				mAVObject.getObjectId(),
				SDCardUtil.apkPath
				).DownloadFile();
	}
	

}

package com.messi.languagehelper.util;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messi.languagehelper.R;

public class TableViewUtil {

	public static void showTableView(Context mContext, ArrayList<View> tableViews, LinearLayout tableLv){
		int len = tableViews.size();
		int row = len/3;
		if(len % 3 > 0){
			row += 1;
		}
		for(int n=0; n<row; n++){
			LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			mParams.bottomMargin = ScreenUtil.dip2px(mContext, 5);
			mParams.leftMargin = ScreenUtil.dip2px(mContext, 6);
			mParams.rightMargin = ScreenUtil.dip2px(mContext, 6);
			View mView = getLotteryTableLine(mContext,tableViews,n,len);
			mView.setLayoutParams(mParams);
			tableLv.addView( mView );
		}
		
		
	}
	
	public static View getLotteryTableLine(Context mContext, ArrayList<View> tableViews,int row,int size){
		LinearLayout mLayout = new LinearLayout(mContext);
		mLayout.setWeightSum(3f);
		for (int i = 0; i < 3; i++) {
			int index = row * 3 + i;
			LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT, 1);
			if(i == 1){
				mParams.leftMargin = ScreenUtil.dip2px(mContext, 5);
				mParams.rightMargin = ScreenUtil.dip2px(mContext, 5);
			}
			View itemView = null;
			if(index < size){
				itemView = tableViews.get(index);
			}else{
				itemView = new LinearLayout(mContext);
			}
			mLayout.addView(itemView, mParams);
		}
		return mLayout;
	}
	
	public static View getTableChargeItem(final Context mContext,LayoutInflater mInflater) {
		View convertView = mInflater.inflate(R.layout.recommend_activity, null);
		try{
			ImageView item_icon = (ImageView) convertView.findViewById(R.id.item_icon);
			TextView item_name = (TextView) convertView.findViewById(R.id.item_name);
			TextView item_msg = (TextView) convertView.findViewById(R.id.item_msg);
			
			item_name.setText("充值中心");
			item_msg.setText("方便快捷");
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
				}
			});
		}catch (Exception e) {
			LogUtil.ExceptionLog("getLotteryItemView");
			e.printStackTrace();
		}
		return convertView;
	}
}

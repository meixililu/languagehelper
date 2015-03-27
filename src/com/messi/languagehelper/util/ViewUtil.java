package com.messi.languagehelper.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messi.languagehelper.R;

public class ViewUtil {

	public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.tabs_heigh);
    }
    
	public static TextView getImageView(Context mContext, int color, int margin){
		TextView img = new TextView(mContext);
		img.setBackgroundColor(mContext.getResources().getColor(color));
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(0,ScreenUtil.dip2px(mContext, 12));
		mParams.rightMargin = ScreenUtil.dip2px(mContext, 10);
		mParams.weight = 1;
		img.setLayoutParams(mParams);
		return img;
	}
	
	public static void addIndicator(int size, LinearLayout view, Context mContext){
		try {
			for(int i=0; i<size; i++){
				TextView img = getImageView(mContext,R.color.green_light, 5);
				view.addView(img);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setPracticeIndicator(Context mContext, LinearLayout parent,int position){
		try {
			int size = parent.getChildCount();
			if(position < size){
				View view = parent.getChildAt(position);
				view.setBackgroundColor(mContext.getResources().getColor(R.color.green));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void addDot(Context mContext,int size,LinearLayout viewpager_dot_layout){
		viewpager_dot_layout.removeAllViews();
		for (int i = 0; i < size; i++) {
			viewpager_dot_layout.addView(ViewUtil.getDot(mContext, i));
		}
	}
	
	/**自己画选中的圆点
	 * @param mContext
	 * @return
	 */
	public static ImageView getDot(Context mContext,int index){
		ImageView img = new ImageView(mContext);
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(ScreenUtil.dip2px(mContext, 7),ScreenUtil.dip2px(mContext, 7));
		mParams.leftMargin = ScreenUtil.dip2px(mContext, 3);
		mParams.rightMargin = ScreenUtil.dip2px(mContext, 3);
		img.setLayoutParams(mParams);
		img.setBackgroundResource(R.drawable.dot_selector);
		if(index == 0){
			img.setEnabled(true);
		}else{
			img.setEnabled(false);
		}
		return img;
	}
	
}

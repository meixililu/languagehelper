package com.messi.languagehelper.util;

import com.messi.languagehelper.R;
import com.messi.languagehelper.views.ProportionalImageView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.AbsListView.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewUtil {
	
	public static ImageView getLine(Context context){
		ImageView mImageView = new ImageView(context);
		mImageView.setBackgroundColor(context.getResources().getColor(R.color.lightgrey));
		ViewGroup.LayoutParams mParams = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,1);
		mImageView.setLayoutParams(mParams);
		return mImageView;
	}
	
	public static ImageView initImageView(Context context){
		ImageView mImageView = new ImageView(context);
		mImageView.setScaleType(ScaleType.CENTER_CROP);
		return mImageView;
	}
	
	public static ProportionalImageView getAutoWidthImage(Context mContext){
		ProportionalImageView img = new ProportionalImageView(mContext);
		ViewGroup.LayoutParams mParams = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		img.setLayoutParams(mParams);
		return img;
	}

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
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				ScreenUtil.dip2px(mContext, 7),ScreenUtil.dip2px(mContext, 7));
		mParams.leftMargin = ScreenUtil.dip2px(mContext, 2);
		mParams.rightMargin = ScreenUtil.dip2px(mContext, 2);
		img.setLayoutParams(mParams);
		img.setBackgroundResource(R.drawable.dot_selector);
		if(index == 0){
			img.setEnabled(true);
		}else{
			img.setEnabled(false);
		}
		return img;
	}
	
	public static void changeState(LinearLayout viewpager_dot_layout, int pos) {
		int size = viewpager_dot_layout.getChildCount();
		for (int i = 0; i < size; i++) {
			View mView = viewpager_dot_layout.getChildAt(i);
			if (i == pos) {
				mView.setEnabled(true);
			} else {
				mView.setEnabled(false);
			}
		}
	}
	
	public static View getListFooterView(Context mContext){
		View mView = new View(mContext);
		LayoutParams mparam = new LayoutParams(LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(mContext, 70));
		mView.setLayoutParams(mparam);
		return mView;
	}
	
	public static Bitmap getBitmapFromViewFull(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) 
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else 
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }
	
	public static Bitmap getBitmapFromViewSmall(View view) {
		view.setDrawingCacheEnabled(true);
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}
	
}

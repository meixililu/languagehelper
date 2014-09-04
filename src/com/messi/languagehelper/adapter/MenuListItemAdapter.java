package com.messi.languagehelper.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.AboutActivity;
import com.messi.languagehelper.HelpActivity;
import com.messi.languagehelper.R;
import com.messi.languagehelper.RecommendActivity;
import com.messi.languagehelper.SettingActivity;
import com.messi.languagehelper.WebViewActivity;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.Settings;

public class MenuListItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private String[] mPlanetTitles;
	private SharedPreferences mSharedPreferences;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

	public MenuListItemAdapter(Context mContext, String[] mPlanetTitles,DrawerLayout mDrawerLayout,
			ListView mDrawerList) {
		context = mContext;
		this.mInflater = LayoutInflater.from(mContext);
		this.mDrawerLayout = mDrawerLayout;
		this.mDrawerList = mDrawerList;
		this.mPlanetTitles = mPlanetTitles;
		mSharedPreferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
	}

	public int getCount() {
		return mPlanetTitles.length;
	}

	public Object getItem(int position) {
		return mPlanetTitles[position];
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LogUtil.DefalutLog("CollectedListItemAdapter---getView");
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.menu_lv_item, null);
			holder = new ViewHolder();
			holder.cover = (View) convertView.findViewById(R.id.cover);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.unread_dot = (ImageView) convertView.findViewById(R.id.unread_dot);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(mPlanetTitles[position]);
		if(position == 0){
			if(!mSharedPreferences.getBoolean(KeyUtil.IsShowSettingNewAdd, false)){
				 holder.unread_dot.setVisibility(View.VISIBLE);
			}
		}
		
		holder.cover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onItemClick(position,holder.unread_dot);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		View cover;
		TextView title;
		ImageView unread_dot;
	}

	public void onItemClick(int position,View unreadView) {
		try {
			unreadView.setVisibility(View.GONE);
			Intent intent = new Intent();
			if (position == 0) {
				intent.setClass(context, SettingActivity.class);
				if(!mSharedPreferences.getBoolean(KeyUtil.IsShowSettingNewAdd, false)){
					 Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.IsShowSettingNewAdd, true);
				}
				StatService.onEvent(context, "1.6_settingbtn","应用设置按钮", 1);
			} else if (position == 1) {
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=com.messi.languagehelper"));
				StatService.onEvent(context, "1.6_commend","吐槽评价按钮", 1);
			} else if (position == 2) {
				intent.setClass(context, HelpActivity.class);
				StatService.onEvent(context, "1.7_help","使用帮助按钮", 1);
			} else if (position == 3) {
				intent.setClass(context, AboutActivity.class);
				StatService.onEvent(context, "1.6_aboutus","关于我们按钮", 1);
			}
			context.startActivity(intent);
			closeMenu();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void closeMenu(){
		if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

}

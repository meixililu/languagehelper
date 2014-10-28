package com.messi.languagehelper.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.CollectedFragment;
import com.messi.languagehelper.ImgShareActivity;
import com.messi.languagehelper.LanguageApplication;
import com.messi.languagehelper.MainFragment;
import com.messi.languagehelper.PracticeActivity;
import com.messi.languagehelper.R;
import com.messi.languagehelper.bean.DialogBean;
import com.messi.languagehelper.bean.UserSpeakBean;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.dialog.PopDialog;
import com.messi.languagehelper.dialog.PopDialog.PopViewItemOnclickListener;
import com.messi.languagehelper.task.PublicTask;
import com.messi.languagehelper.task.PublicTask.PublicTaskListener;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.BaiduStatistics;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.ScreenUtil;
import com.messi.languagehelper.util.SharedPreferencesUtil;
import com.messi.languagehelper.util.ShowView;
import com.messi.languagehelper.util.ToastUtil;
import com.messi.languagehelper.util.XFUtil;

public class PracticePageListItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context context;
	private ArrayList<UserSpeakBean> beans;

	public PracticePageListItemAdapter(Context mContext,ArrayList<UserSpeakBean> mUserSpeakBean) {
		context = mContext;
		this.mInflater = LayoutInflater.from(mContext);
		this.beans = mUserSpeakBean;
	}

	public int getCount() {
		return beans.size();
	}

	public Object getItem(int position) {
		return beans.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LogUtil.DefalutLog("CollectedListItemAdapter---getView");
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.practice_activity_lv_item, null);
			holder = new ViewHolder();
			holder.user_speak_content = (TextView) convertView.findViewById(R.id.user_speak_content);
			holder.user_speak_score = (TextView) convertView.findViewById(R.id.user_speak_score);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final UserSpeakBean mBean = beans.get(position);
		holder.user_speak_content.setText(mBean.getContent());
		holder.user_speak_score.setText(mBean.getScore());
		int color = context.getResources().getColor(mBean.getColor());
		holder.user_speak_score.setTextColor(color);
		holder.user_speak_score.setShadowLayer(1f, 1f, 1f, color);
		return convertView;
	}
	
	static class ViewHolder {
		TextView user_speak_content;
		TextView user_speak_score;
	}
	

}

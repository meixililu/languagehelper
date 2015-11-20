package com.messi.languagehelper;

import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.gc.materialdesign.views.ButtonRectangle;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.SDCardUtil;

public class StudyDialogActivity extends BaseActivity implements OnClickListener{

	public static final String TypeListen = "TypeListen";
	public static final String TypeRead = "TypeRead";
	public static final String TypeA = "TypeA";
	public static final String TypeB = "TypeB";
	
	private String vedioPath;
	private TextView error_txt;
	private LinearLayout study_dialog_type;
	private ButtonRectangle study_dialog_rolea, study_dialog_roleb;
	private ButtonRectangle study_dialog_listen,study_dialog_read;
	private String SDCode;
	private String SDLCode;
	private AVObject avObject;
	private String content;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_dialog_activity);
		initData();
		initViews();
		new QueryTask().execute();
	}
	
	private void initData(){
		SDCode = getIntent().getStringExtra(AVOUtil.StudyDialogListCategory.SDCode);
		SDLCode = getIntent().getStringExtra(AVOUtil.StudyDialogListCategory.SDLCode);
		vedioPath = SDCardUtil.StudyDialogPath + SDCode + SDCardUtil.Delimiter + SDLCode + SDCardUtil.Delimiter;
	}

	private void initViews() {
		getSupportActionBar().setTitle(getResources().getString(R.string.title_dialog_select));
		study_dialog_listen = (ButtonRectangle) findViewById(R.id.study_dialog_listen);
		study_dialog_read = (ButtonRectangle) findViewById(R.id.study_dialog_read);
		study_dialog_rolea = (ButtonRectangle) findViewById(R.id.study_dialog_rolea);
		study_dialog_roleb = (ButtonRectangle) findViewById(R.id.study_dialog_roleb);
		study_dialog_type = (LinearLayout) findViewById(R.id.study_dialog_type);
		error_txt = (TextView) findViewById(R.id.error_txt);
//		study_dialog_listen.setTextColor(getResources().getColor(R.color.text_black));
//		study_dialog_read.setTextColor(getResources().getColor(R.color.text_black));
//		study_dialog_rolea.setTextColor(getResources().getColor(R.color.text_black));
//		study_dialog_roleb.setTextColor(getResources().getColor(R.color.text_black));
		study_dialog_listen.setTextSize(18);
		study_dialog_read.setTextSize(18);
		study_dialog_rolea.setTextSize(18);
		study_dialog_roleb.setTextSize(18);
		study_dialog_listen.setOnClickListener(this);
		study_dialog_read.setOnClickListener(this);
		study_dialog_rolea.setOnClickListener(this);
		study_dialog_roleb.setOnClickListener(this);
		error_txt.setOnClickListener(this);
	}

	private class QueryTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressbar();
			study_dialog_type.setVisibility(View.GONE);
			error_txt.setVisibility(View.GONE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.StudyDialogDetail.StudyDialogDetail);
			query.whereEqualTo(AVOUtil.StudyDialogDetail.SDCode, SDCode);
			query.whereEqualTo(AVOUtil.StudyDialogDetail.SDLCode, SDLCode);
			try {
				List<AVObject> avObjects  = query.find();
				if(avObjects != null && avObjects.size() > 0){
					avObject = avObjects.get(0);
				}
			} catch (AVException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			hideProgressbar();
			if(avObject != null){
				content = avObject.getString(AVOUtil.StudyDialogDetail.SDDContent);
				if(!TextUtils.isEmpty(content)){
					study_dialog_type.setVisibility(View.VISIBLE);
				}else{
					error_txt.setVisibility(View.VISIBLE);
				}
			}else{
				error_txt.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.study_dialog_listen:
			toDialogStudyActivity(StudyDialogActivity.TypeListen);
			break;
		case R.id.study_dialog_read:
			toDialogStudyActivity(StudyDialogActivity.TypeRead);
			break;
		case R.id.study_dialog_rolea:
			toDialogStudyActivity(StudyDialogActivity.TypeA);
			break;
		case R.id.study_dialog_roleb:
			toDialogStudyActivity(StudyDialogActivity.TypeB);
			break;
		case R.id.error_txt:
			new QueryTask().execute();
			break;
		default:
			break;
		}
	}
	
	private void toDialogStudyActivity(String type){
		Intent intent = new Intent(this, DialogPracticeActivity.class);
		intent.putExtra(KeyUtil.SDcardPathKey, vedioPath + type + SDCardUtil.Delimiter);
		intent.putExtra(KeyUtil.ContextKey, content);
		intent.putExtra(KeyUtil.StudyDialogAction, type);
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}

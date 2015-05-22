package com.messi.languagehelper;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;

import com.gc.materialdesign.views.ButtonRectangle;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.ScreenUtil;

public class StudyDialogActivity extends BaseActivity implements OnClickListener{

	public static String vedioPath = "";
	private ButtonRectangle study_dialog_rolea, study_dialog_roleb;
	private ButtonRectangle study_dialog_listen,study_dialog_read;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_dialog_activity);
		initViews();
	}

	private void initViews() {
		getSupportActionBar().setTitle(getResources().getString(R.string.title_dialog_select));
		study_dialog_listen = (ButtonRectangle) findViewById(R.id.study_dialog_listen);
		study_dialog_read = (ButtonRectangle) findViewById(R.id.study_dialog_read);
		study_dialog_rolea = (ButtonRectangle) findViewById(R.id.study_dialog_rolea);
		study_dialog_roleb = (ButtonRectangle) findViewById(R.id.study_dialog_roleb);
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
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.study_dialog_listen:
			toDialogStudyActivity("");
			break;
		case R.id.study_dialog_read:
			toDialogStudyActivity("");
			break;
		case R.id.study_dialog_rolea:
			toDialogStudyActivity("");
			break;
		case R.id.study_dialog_roleb:
			toDialogStudyActivity("");
			break;
		default:
			break;
		}
	}
	
	private void toDialogStudyActivity(String type){
		Intent intent = new Intent(this, DialogPracticeActivity.class);
		intent.putExtra(KeyUtil.PracticeDialogAction, type);
		startActivity(intent);
	}
}

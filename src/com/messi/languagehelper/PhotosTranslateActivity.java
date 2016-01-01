package com.messi.languagehelper;

import android.os.Bundle;
import android.widget.TextView;

public class PhotosTranslateActivity extends BaseActivity {

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		init();
	}

	private void init() {
		getSupportActionBar().setTitle(getResources().getString(R.string.title_about));
		
	}
	
}

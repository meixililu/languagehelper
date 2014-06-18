package com.messi.languagehelper;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;

public class ImgShareActivity extends BaseActivity implements OnClickListener {

	private EditText share_content;
	private FrameLayout share_btn_cover;
	private LinearLayout parent_layout;
	private String shareContent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.share_layout);
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {
		Intent intent = getIntent();
		shareContent = intent.getStringExtra(KeyUtil.ShareContentKey);
		mActionBar.setTitle(getResources().getString(R.string.title_about));
        parent_layout = (LinearLayout)findViewById(R.id.parent_layout);
        share_content = (EditText) findViewById(R.id.share_content);
        share_btn_cover = (FrameLayout) findViewById(R.id.share_btn_cover);
        
        share_content.setText(shareContent);
        share_btn_cover.setOnClickListener(this);
	}
	
	private void shareWithImg(String dstString) throws IOException{
		share_content.setText(dstString);
		parent_layout.setDrawingCacheEnabled(true);
		parent_layout.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		parent_layout.layout(0, 0, parent_layout.getMeasuredWidth(), parent_layout.getMeasuredHeight());
		parent_layout.buildDrawingCache();
		Bitmap bitmap = parent_layout.getDrawingCache();
		if(bitmap !=  null){
			String imgPath = SDCardUtil.saveBitmap(this, bitmap);
			File file = new File(imgPath);    
			if (file != null && file.exists() && file.isFile()) {    
				Uri u = Uri.fromFile(file);    
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("image/png");    
				intent.putExtra(Intent.EXTRA_STREAM, u); 
				intent.putExtra(Intent.EXTRA_SUBJECT, this.getResources().getString(R.string.share));  
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
				this.startActivity(Intent.createChooser(intent, this.getResources().getString(R.string.share))); 
			}    
		}else{
			LogUtil.DefalutLog("bitmap == null");
		}
		parent_layout.requestLayout();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share_btn_cover:
			try {
				shareContent = share_content.getText().toString();
				shareWithImg(shareContent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
	
}

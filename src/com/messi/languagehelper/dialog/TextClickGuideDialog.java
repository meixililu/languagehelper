package com.messi.languagehelper.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.messi.languagehelper.R;
import com.messi.languagehelper.util.ScreenUtil;

/**
 * 自定义退出dialog
 * 
 * @author Messi
 * 
 */
public class TextClickGuideDialog extends Dialog {

	private Context context;

	private FrameLayout readed_btn;
	
	public TextClickGuideDialog(Context mContext) {
		super(mContext, R.style.mydialog);
		context = mContext;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.textclickguidedialog);
		readed_btn = (FrameLayout) findViewById(R.id.readed_btn);
		readed_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextClickGuideDialog.this.dismiss();
			}
		});
	}
	
	@Override
	public void dismiss() {
		super.dismiss();
	}
	
}

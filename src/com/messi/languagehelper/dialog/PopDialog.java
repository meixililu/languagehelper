package com.messi.languagehelper.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.messi.languagehelper.R;
import com.messi.languagehelper.util.ScreenUtil;

public class PopDialog extends Dialog {
	
	private PopViewItemOnclickListener listener;
	private Context context;
	private String[] tempText;
	
	public void setListener(PopViewItemOnclickListener listener) {
		this.listener = listener;
	}

	public PopDialog(Context context, int theme) {
	    super(context, theme);
	    this.context = context;
	}

	public PopDialog(Context context) {
	    super(context);
	    this.context = context;
	}

	/**
	 * 更改TextView的提示内容
	 * @param context
	 * @param theme
	 * @param tempText
	 */
	public PopDialog(Context context, String[] tempText) {
		super(context, R.style.mydialog);
		this.context = context;
		this.tempText = tempText;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.select_popwindow);
	    FrameLayout text1Cover = (FrameLayout) findViewById(R.id.select_popwindow_text1_cover);
	    FrameLayout text2Cover = (FrameLayout) findViewById(R.id.select_popwindow_text2_cover);
	    TextView text1 = (TextView) findViewById(R.id.select_popwindow_text1);
	    TextView text2 = (TextView) findViewById(R.id.select_popwindow_text2);
	    text1Cover.setOnClickListener(onClickListener);
	    text2Cover.setOnClickListener(onClickListener);
		
		if(tempText != null){
			text1.setText(tempText[0]);
			text2.setText(tempText[1]);
		}
	}
	
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.select_popwindow_text1_cover:
				 if(listener != null){
					 listener.onFirstClick(v);
				 }
				 PopDialog.this.dismiss();
				 break;
			case R.id.select_popwindow_text2_cover:
				 if(listener != null){
					 listener.onSecondClick(v);
				 }					
				 PopDialog.this.dismiss();
				 break;
			}
		}
	};
	
	public void setPopViewPosition(){
		Window win = this.getWindow();
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.gravity = Gravity.TOP|Gravity.RIGHT;
		params.x = ScreenUtil.dip2px(context,10f);
		params.y = ScreenUtil.dip2px(context,45f);
		win.setAttributes(params);
		this.setCanceledOnTouchOutside(true);
	}
	
	public interface PopViewItemOnclickListener{
		public void onFirstClick(View v);
		public void onSecondClick(View v);
	}
}

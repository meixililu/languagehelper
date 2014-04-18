package com.messi.languagehelper;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.messi.languagehelper.bean.DialogBean;
import com.messi.languagehelper.util.DialogUtil;
import com.messi.languagehelper.util.LogUtil;

public class ReadAfterMeFragment extends Fragment implements OnClickListener{

	private View view;
	private ListView dialog_listview;
	private TextView stop_btn,start_btn;
	private ArrayList<DialogBean> dialogList;
	private MyListViewAdapter adapter;
	private DialogBean currentItem;
	private int currentIndex;
	
	private SpeechRecognizer mSpeechRecognizer;
	private SpeechSynthesizer mSynthesizerPlayer;
	private StringBuilder builder;
	
	private static ReadAfterMeFragment mReadAfterMeFragment;
	
	public static ReadAfterMeFragment getInstance(){
		if(mReadAfterMeFragment == null){
			mReadAfterMeFragment = new ReadAfterMeFragment();
		}
		return mReadAfterMeFragment;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.DefalutLog("ReadAfterMeFragment-onDestroy");
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.DefalutLog("ReadAfterMeFragment-onPause");
		stop();
	}
	
	private void stop(){
		if(mSynthesizerPlayer != null){
			mSynthesizerPlayer.cancel();
		}
		if(mSpeechRecognizer != null){
			mSpeechRecognizer.cancel();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		iatDialog = new RecognizerDialog(getActivity(), "appid=" + getString(R.string.app_id));
//		builder = new StringBuilder();
//		iatDialog.setListener(new RecognizerDialogListener() {
//			@Override
//			public void onResults(ArrayList<RecognizerResult> results, boolean arg1) {
//				for (RecognizerResult recognizerResult : results) {
//					builder.append(recognizerResult.text);
//				}
//			}
//			@Override
//			public void onEnd(SpeechError arg0) {
//				String content = builder.toString();
//				if(currentItem != null){
//					currentItem.setAnswer(content);
//					adapter.notifyDataSetChanged();
//					dialog_listview.setSelection(currentIndex);
//					moveToNext();
//					builder = new StringBuilder();
//				}
//			}
//		});
		mSynthesizerPlayer = SpeechSynthesizer.createSynthesizer(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.read_after_me_fragment, null);
		init();
		return view;
	}

	private void init() {
		dialog_listview = (ListView)view.findViewById(R.id.dialog_listview);
		stop_btn = (TextView)view.findViewById(R.id.stop_btn);
		start_btn = (TextView)view.findViewById(R.id.start_btn);
		dialogList = new ArrayList<DialogBean>();
		adapter = new MyListViewAdapter(getActivity());
		dialog_listview.setAdapter(adapter);
		
		stop_btn.setOnClickListener(this);
		start_btn.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		dialogList = DialogUtil.getDialogList(getActivity());
		currentItem = dialogList.get(currentIndex);
		adapter.notifyDataSetChanged();
//		Start();
		LogUtil.DefalutLog("ReadAfterMeFragment-onActivityCreated");
	}
	
	private void reset(){
		currentIndex = 0;
		currentItem = dialogList.get(currentIndex);
	}
	
	private void Start(){
//		XunFeiDialogUtil.showSynWithourDialog(currentItem.getQuestion(), this, mSynthesizerPlayer);
	}
	
	private void moveToNext(){
		currentIndex ++;
		if(currentIndex < dialogList.size()){
			currentItem = dialogList.get(currentIndex);
			Start();
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.stop_btn){
			stop();
		}else if(v.getId() == R.id.start_btn){
			reset();
			Start();
		}
	}
	
	private void showZhuanxieDialog(){
//		XunFeiDialogUtil.showIatDialog(iatDialog,XunFeiDialogUtil.VoiceEngineEN);
	}
	
	class MyListViewAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater;
		
		public MyListViewAdapter (Context context) {
			mInflater = LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return dialogList.size();
		}

		@Override
		public Object getItem(int position) {
			return dialogList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.feedback_list_item, null);
				holder = new ViewHolder();
				holder.dialog_a = (TextView)convertView.findViewById(R.id.dialog_a);
				holder.dialog_b = (TextView)convertView.findViewById(R.id.dialog_b);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			final DialogBean bean = dialogList.get(position);
			holder.dialog_a.setText(bean.getQuestion());
			String answerStr = bean.getAnswer();
			LogUtil.DefalutLog("questionStr:"+bean.getQuestion());
			if(!TextUtils.isEmpty(answerStr)){
				holder.dialog_b.setVisibility(View.VISIBLE);
				holder.dialog_b.setText(answerStr);
			}else{
				holder.dialog_b.setVisibility(View.GONE);
			}
			holder.dialog_a.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					currentIndex = position;
					currentItem = bean;
					Start();
				}
			});
			return convertView;
		}
		
	}
	
	static class ViewHolder {
		TextView dialog_a;
		TextView dialog_b;
    }
}

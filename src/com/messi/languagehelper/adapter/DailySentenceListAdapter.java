package com.messi.languagehelper.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.R;
import com.messi.languagehelper.ViewImageActivity;
import com.messi.languagehelper.dao.EveryDaySentence;
import com.messi.languagehelper.task.PublicTask;
import com.messi.languagehelper.task.PublicTask.PublicTaskListener;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.BaiduStatistics;
import com.messi.languagehelper.util.KeyUtil;
import com.squareup.picasso.Picasso;

public class DailySentenceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;
	private LayoutInflater mInflater;
	private List<EveryDaySentence> beans;
	private Context context;

	public DailySentenceListAdapter(Context mContext,LayoutInflater mInflater,List<EveryDaySentence> mBeans) {
		context = mContext;
		beans = mBeans;
		this.mInflater = mInflater;
	}
	
	public static class ItemViewHolder extends RecyclerView.ViewHolder {
		
		public TextView english_txt;
		public TextView chinese_txt;
		public ImageView daily_sentence_list_item_img;
		public FrameLayout daily_sentence_list_item_cover;
		
        public ItemViewHolder(View convertView) {
            super(convertView);
            daily_sentence_list_item_cover = (FrameLayout) convertView.findViewById(R.id.daily_sentence_list_item_cover);
            daily_sentence_list_item_img = (ImageView) convertView.findViewById(R.id.daily_sentence_list_item_img);
			english_txt = (TextView) convertView.findViewById(R.id.english_txt);
			chinese_txt = (TextView) convertView.findViewById(R.id.chinese_txt);
        }
    }
	
	public static class RecyclerHeaderViewHolder extends RecyclerView.ViewHolder {
	    public RecyclerHeaderViewHolder(View itemView) {
	        super(itemView);
	    }
	}
	
	@Override
	public int getItemCount() {
		return getBasicItemCount() + 1;
	}
	
	@Override
	public int getItemViewType(int position) {
		if (isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
	}
	
	private boolean isPositionHeader(int position) {
        return position == 0;
    }
	
	public int getBasicItemCount() {
        return beans == null ? 0 : beans.size();
    }

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup arg0, int viewType) {
		if (viewType == TYPE_ITEM) {
			View v = mInflater.inflate(R.layout.daily_sentence_list_item, arg0, false);
			return new ItemViewHolder(v);
		} else {
			View v = mInflater.inflate(R.layout.recycler_header, arg0, false);
			return new RecyclerHeaderViewHolder(v);
		}
		
	}
	
	public void addEntity(int i, EveryDaySentence entity) {
		beans.add(i, entity);
        notifyItemInserted(i);
    }
 
    public void deleteEntity(int i) {
    	beans.remove(i);
        notifyItemRemoved(i);
    }
    
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
		if (!isPositionHeader(position)) {
			final ItemViewHolder holder = (ItemViewHolder) viewHolder;
			final EveryDaySentence mBean = beans.get(position-1);
			holder.english_txt.setText(mBean.getContent());
			holder.chinese_txt.setText(mBean.getNote());
			Picasso.with(context)
			.load(mBean.getPicture2())
			.tag(context)
			.into(holder.daily_sentence_list_item_img);
			
			holder.daily_sentence_list_item_cover.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					toViewImgActivity(mBean.getFenxiang_img());
				}
			});
		}
	}
	
	private void toViewImgActivity(String imgurl){
		Intent intent = new Intent(context, ViewImageActivity.class);
		intent.putExtra(KeyUtil.DailySentenceBigImgUrl, imgurl);
		context.startActivity(intent);
	}

	/**
	 * 复制按钮
	 */
	private void copy(String dstString){
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(dstString);
		showToast(context.getResources().getString(R.string.copy_success));
		StatService.onEvent(context, BaiduStatistics.CopyBtn, "复制按钮", 1);
	}
	
	/**
	 * 分享
	 */
//	private void sendToWechat(final record mBean){
//		String[] tempText = new String[2];
//		tempText[0] = context.getResources().getString(R.string.share_dialog_text_1);
//		tempText[1] = context.getResources().getString(R.string.share_dialog_text_2);
//		PopDialog mPopDialog = new PopDialog(context,tempText);
//		mPopDialog.setCanceledOnTouchOutside(true);
//		mPopDialog.setListener(new PopViewItemOnclickListener() {
//			@Override
//			public void onSecondClick(View v) {
//				toShareImageActivity(mBean);
//				StatService.onEvent(context, "1.8_to_share_image_btn", "去图片分享页面按钮", 1);
//			}
//			@Override
//			public void onFirstClick(View v) {
//				toShareTextActivity(mBean.getEnglish());
//				StatService.onEvent(context, "1.8_to_share_text_btn", "去文字分享页面按钮", 1);
//			}
//		});
//		mPopDialog.show();
//	}
	
//	private void toShareTextActivity(String dstString){
//		Intent intent = new Intent(Intent.ACTION_SEND);    
//		intent.setType("text/plain"); // 纯文本     
//		intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.share));    
//		intent.putExtra(Intent.EXTRA_TEXT, dstString);    
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
//		context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share)));    
//	}
	
//	private void toShareImageActivity(record mBean){
//		Intent intent = new Intent(context, ImgShareActivity.class); 
//		intent.putExtra(KeyUtil.ShareContentKey, mBean.getEnglish()+"\n"+mBean.getChinese());
//		context.startActivity(intent); 
//	}
	
	private void playLocalPcm(final String path,final AnimationDrawable animationDrawable){
		PublicTask mPublicTask = new PublicTask(context);
		mPublicTask.setmPublicTaskListener(new PublicTaskListener() {
			@Override
			public void onPreExecute() {
				if(!animationDrawable.isRunning()){
					animationDrawable.setOneShot(false);
					animationDrawable.start();  
				}
			}
			@Override
			public Object doInBackground() {
				AudioTrackUtil.createAudioTrack(path);
				return null;
			}
			@Override
			public void onFinish(Object resutl) {
				animationDrawable.setOneShot(true);
				animationDrawable.stop(); 
				animationDrawable.selectDrawable(0);
			}
		});
		mPublicTask.execute();
	}
	
	/**toast message
	 * @param toastString
	 */
	private void showToast(String toastString) {
		if(!TextUtils.isEmpty(toastString)){
			Toast.makeText(context, toastString, 0).show();
		}
	}

}

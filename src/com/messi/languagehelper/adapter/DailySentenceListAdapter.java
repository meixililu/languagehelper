package com.messi.languagehelper.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.messi.languagehelper.R;
import com.messi.languagehelper.ViewImageActivity;
import com.messi.languagehelper.dao.EveryDaySentence;
import com.messi.languagehelper.task.PublicTask;
import com.messi.languagehelper.task.PublicTask.PublicTaskListener;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.KeyUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
		public ImageView play_img;
		public FrameLayout daily_sentence_list_item_cover;
		
        public ItemViewHolder(View convertView) {
            super(convertView);
            daily_sentence_list_item_cover = (FrameLayout) convertView.findViewById(R.id.daily_sentence_list_item_cover);
            daily_sentence_list_item_img = (ImageView) convertView.findViewById(R.id.daily_sentence_list_item_img);
            play_img = (ImageView) convertView.findViewById(R.id.play_img);
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
			Glide.with(context)
			.load(mBean.getPicture2())
			.into(holder.daily_sentence_list_item_img);
			
			holder.daily_sentence_list_item_cover.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					toViewImgActivity(mBean.getFenxiang_img());
				}
			});
			final MediaPlayer mPlayer = new MediaPlayer();
			try {
				Uri uri = Uri.parse(mBean.getTts());
				mPlayer.setDataSource(context, uri);
				mPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						holder.play_img.setBackgroundResource(R.drawable.ic_play_circle_outline_white_48dp);
					}
				});
				mPlayer.prepare();
			} catch (Exception e) {
				e.printStackTrace();
			} 
			holder.play_img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mPlayer.isPlaying()){
						mPlayer.pause();
						holder.play_img.setBackgroundResource(R.drawable.ic_play_circle_outline_white_48dp);
					}else{
						mPlayer.start();
						holder.play_img.setBackgroundResource(R.drawable.ic_pause_circle_outline_white_48dp);
					}
				}
			});
		}
	}
	
	private void toViewImgActivity(String imgurl){
		Intent intent = new Intent(context, ViewImageActivity.class);
		intent.putExtra(KeyUtil.DailySentenceBigImgUrl, imgurl);
		context.startActivity(intent);
	}

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

package com.messi.languagehelper.adapter;

import com.messi.languagehelper.JokeBuDeJieFragment;
import com.messi.languagehelper.JokePictureFragment;
import com.messi.languagehelper.JokeTextFragment;
import com.messi.languagehelper.R;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class JokePageAdapter extends FragmentPagerAdapter {

	public static String[] CONTENT;
	
    public JokePageAdapter(FragmentManager fm,Context mContext) {
        super(fm);
        CONTENT = new String[] { 
//        		mContext.getResources().getString(R.string.title_duanzi),
        		mContext.getResources().getString(R.string.title_duanzi_img),
        		mContext.getResources().getString(R.string.title_duanzi_word)
        };
    }

    @Override
    public Fragment getItem(int position) {
        if( position == 0 ){
        	return new JokePictureFragment();
//        	return new JokeBuDeJieFragment();
        }else if( position == 1 ){
        	return new JokeTextFragment();
        }else if( position == 2 ){
        	return new JokeTextFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return CONTENT.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position].toUpperCase();
    }
}
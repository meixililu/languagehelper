package com.messi.languagehelper.adapter;

import com.messi.languagehelper.R;
import com.messi.languagehelper.ReadingFragment;
import com.messi.languagehelper.WordStudyListFragment;
import com.messi.languagehelper.util.AVOUtil;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class WordStudyTabAdapter extends FragmentPagerAdapter {

	public static String[] CONTENT;
	
    public WordStudyTabAdapter(FragmentManager fm,Context mContext) {
        super(fm);
        CONTENT = new String[] { 
        		mContext.getResources().getString(R.string.title_word_study),
        		mContext.getResources().getString(R.string.title_word_vocabulary)
        };
    }

    @Override
    public Fragment getItem(int position) {
        if( position == 0 ){
        	return new WordStudyListFragment();
        }else if( position == 1 ){
        	return new ReadingFragment(AVOUtil.Category.word);
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
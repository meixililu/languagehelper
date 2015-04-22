package com.messi.languagehelper.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.messi.languagehelper.CollectedDictionaryFragment;
import com.messi.languagehelper.CollectedTranslateFragment;
import com.messi.languagehelper.R;

public class CollectedActivityAdapter extends FragmentPagerAdapter {

	public static String[] CONTENT;
	private Bundle bundle;
	
    public CollectedActivityAdapter(FragmentManager fm,Bundle bundle,Context mContext) {
        super(fm);
        this.bundle = bundle;
        CONTENT = new String[] { 
        		mContext.getResources().getString(R.string.title_translate),
        		mContext.getResources().getString(R.string.title_dictionary),
        };
    }

    @Override
    public Fragment getItem(int position) {
        if( position == 0 ){
        	return new CollectedTranslateFragment();
        }else if( position == 1 ){
        	return new CollectedDictionaryFragment();
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
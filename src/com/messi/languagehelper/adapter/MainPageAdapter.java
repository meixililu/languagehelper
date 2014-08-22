package com.messi.languagehelper.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.messi.languagehelper.CollectedFragment;
import com.messi.languagehelper.MainFragment;
import com.messi.languagehelper.R;
import com.messi.languagehelper.WebViewFragment;

public class MainPageAdapter extends FragmentPagerAdapter {

	public static String[] CONTENT;
	private Bundle bundle;
	
    public MainPageAdapter(FragmentManager fm,Bundle bundle,Context mContext) {
        super(fm);
        this.bundle = bundle;
        CONTENT = new String[] { mContext.getResources().getString(R.string.app_name),
        		mContext.getResources().getString(R.string.title_favorite),
        		mContext.getResources().getString(R.string.title_cailing),
        		mContext.getResources().getString(R.string.title_cailing)
        };
    }

    @Override
    public Fragment getItem(int position) {
        if( position == 0 ){
        	return MainFragment.getInstance(bundle);
        }else if( position == 1 ){
        	return CollectedFragment.getInstance(bundle);
        }else if( position == 2 ){
        	return WebViewFragment.getInstance();
        }
        return null;
//        else if( position == 2 ){
//        	return new ReadAfterMeFragment();
//        }else {
//        	return MainFragment.getInstance();
//        }
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
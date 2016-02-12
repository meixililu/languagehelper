package com.messi.languagehelper.adapter;

import java.util.List;

import com.avos.avoscloud.AVObject;
import com.messi.languagehelper.CompositionFragment;
import com.messi.languagehelper.util.AVOUtil;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CompositionAdapter extends FragmentPagerAdapter {

	public static String[] CONTENT;
	private List<AVObject> avObjects;
	
    public CompositionAdapter(FragmentManager fm,Context mContext,List<AVObject> avObjects) {
        super(fm);
        this.avObjects = avObjects;
    }

    @Override
    public Fragment getItem(int position) {
    	String code = avObjects.get(position).getString(AVOUtil.CompositionType.type_id);
    	int random_max = avObjects.get(position).getInt(AVOUtil.CompositionType.random_max);
    	return new CompositionFragment(code,random_max);
    }

    @Override
    public int getCount() {
        return avObjects.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return avObjects.get(position).getString(AVOUtil.CompositionType.type_name);
    }
}
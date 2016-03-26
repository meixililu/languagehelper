package com.messi.languagehelper.adapter;

import java.util.List;

import com.avos.avoscloud.AVObject;
import com.messi.languagehelper.ReadingFragment;
import com.messi.languagehelper.util.AVOUtil;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ExaminationListAdapter extends FragmentPagerAdapter {

	private List<AVObject> avObjects;
	
    public ExaminationListAdapter(FragmentManager fm,Context mContext,List<AVObject> avObjects) {
        super(fm);
        this.avObjects = avObjects;
    }

    @Override
    public Fragment getItem(int position) {
    	String code = avObjects.get(position).getString(AVOUtil.ExaminationType.type_id);
    	return new ReadingFragment(AVOUtil.Category.examination,code);
    }

    @Override
    public int getCount() {
        return avObjects.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return avObjects.get(position).getString(AVOUtil.ExaminationType.type_name);
    }
}
package com.zhuoli.onecard.setting.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zhuoli.onecard.setting.adapter.model.CounterPager;

import java.util.ArrayList;
import java.util.List;

/**
 * author: CLD
 * created on: 2016/11/3 0003 下午 4:33
 * description:
 */

public class CounterPagerAdapter extends FragmentPagerAdapter{

	private List<CounterPager> mCounterPagerList;

	public CounterPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	public CounterPagerAdapter(FragmentManager fm, List<CounterPager> counterPagerList) {
		super(fm);
		if (counterPagerList == null){
			counterPagerList = new ArrayList<>();
		}
		mCounterPagerList = counterPagerList;
	}

	@Override
	public Fragment getItem(int position) {
		return mCounterPagerList.get(position).getCounterFragment();
	}

	@Override
	public int getCount() {
		return mCounterPagerList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mCounterPagerList.get(position).getTitle();
	}



}

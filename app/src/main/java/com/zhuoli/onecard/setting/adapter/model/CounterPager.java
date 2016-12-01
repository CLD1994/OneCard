package com.zhuoli.onecard.setting.adapter.model;

import com.zhuoli.onecard.setting.view.CounterFragment;

/**
 * author: CLD
 * created on: 2016/11/3 0003 下午 4:47
 * description:
 */

public class CounterPager {
	private String title;
	private CounterFragment counterFragment;

	public CounterPager(String title, CounterFragment counterFragment) {
		this.title = title;
		this.counterFragment = counterFragment;
	}

	public String getTitle() {
		return title;
	}

	public CounterFragment getCounterFragment() {
		return counterFragment;
	}
}

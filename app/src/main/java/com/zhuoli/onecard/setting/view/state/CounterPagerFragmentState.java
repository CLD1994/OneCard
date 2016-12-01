package com.zhuoli.onecard.setting.view.state;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.zhuoli.onecard.setting.SettingContract;

/**
 * author: CLD
 * created on: 2016/11/3 0003 下午 4:31
 * description:
 */

public class CounterPagerFragmentState implements RestorableViewState<SettingContract.CounterPagerView> {
	@Override
	public void saveInstanceState(@NonNull Bundle out) {

	}

	@Override
	public RestorableViewState<SettingContract.CounterPagerView> restoreInstanceState(Bundle in) {
		return null;
	}

	@Override
	public void apply(SettingContract.CounterPagerView view, boolean retained) {

	}
}

package com.zhuoli.onecard.setting.view.state;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.zhuoli.onecard.setting.SettingContract;

/**
 * Created by CLD on 2016/9/30 0030.
 */

public class CleanFragmentState implements RestorableViewState<SettingContract.CleanView>{
	@Override
	public void saveInstanceState(@NonNull Bundle out) {

	}

	@Override
	public RestorableViewState<SettingContract.CleanView> restoreInstanceState(Bundle in) {
		return null;
	}

	@Override
	public void apply(SettingContract.CleanView view, boolean retained) {

	}
}

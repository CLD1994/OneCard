package com.zhuoli.onecard.setting.view.state;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.zhuoli.onecard.setting.SettingContract;

/**
 * Created by CLD on 2016/9/30 0030.
 */

public class ParameterFragmentState implements RestorableViewState<SettingContract.ParameterView>{
	@Override
	public void saveInstanceState(@NonNull Bundle out) {

	}

	@Override
	public RestorableViewState<SettingContract.ParameterView> restoreInstanceState(Bundle in) {
		return null;
	}

	@Override
	public void apply(SettingContract.ParameterView view, boolean retained) {

	}
}

package com.zhuoli.onecard.main.view.state;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.zhuoli.onecard.main.MainContract;

/**
 * Created by CLD on 2016/9/30 0030.
 */

public class MainFragmentState implements RestorableViewState<MainContract.View>{
	@Override
	public void saveInstanceState(@NonNull Bundle out) {

	}

	@Override
	public RestorableViewState<MainContract.View> restoreInstanceState(Bundle in) {
		return null;
	}

	@Override
	public void apply(MainContract.View view, boolean retained) {

	}
}

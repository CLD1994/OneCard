package com.zhuoli.onecard.main.view.state;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.zhuoli.onecard.main.MainContract;

/**
 * @author CLD
 */
public class OtherViewState implements RestorableViewState<MainContract.OtherView> {
	@Override
	public void saveInstanceState(@NonNull Bundle out) {

	}

	@Override
	public RestorableViewState<MainContract.OtherView> restoreInstanceState(Bundle in) {
		return null;
	}

	@Override
	public void apply(MainContract.OtherView view, boolean retained) {

	}
}
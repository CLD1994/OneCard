package com.zhuoli.onecard.connect.view.state;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby.mvp.viewstate.RestorableViewState;
import com.zhuoli.onecard.connect.ConnectContract;

/**
 * @author CLD
 */
public class ChooseSSIDViewState implements RestorableViewState<ConnectContract.ChooseSSIDView> {
	@Override
	public void saveInstanceState(@NonNull Bundle out) {

	}

	@Override
	public RestorableViewState<ConnectContract.ChooseSSIDView> restoreInstanceState(Bundle in) {
		return null;
	}

	@Override
	public void apply(ConnectContract.ChooseSSIDView view, boolean retained) {

	}
}
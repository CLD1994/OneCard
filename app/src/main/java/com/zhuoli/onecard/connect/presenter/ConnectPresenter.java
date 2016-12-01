package com.zhuoli.onecard.connect.presenter;

import com.zhuoli.onecard.base.BasePresenter;
import com.zhuoli.onecard.connect.ConnectContract;
import com.zhuoli.onecard.connect.usecase.SetCurrentSSID;

/**
 * @author CLD
 */
public class ConnectPresenter extends BasePresenter<ConnectContract.ConnectView> implements ConnectContract.ConnectPresenter {
	private final SetCurrentSSID mSetCurrentSSID;
	public ConnectPresenter(SetCurrentSSID setCurrentSSID) {
		mSetCurrentSSID = setCurrentSSID;
	}

	@Override
	public void setCurrentSSID(String ssid) {
		executeUseCase(mSetCurrentSSID,new SetCurrentSSID.RequestValues(ssid));
	}
}
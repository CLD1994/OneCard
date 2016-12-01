package com.zhuoli.onecard.connect;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.zhuoli.onecard.base.BaseView;
import com.zhuoli.onecard.data.model.SSID;

import java.util.List;

/**
 * @author CLD
 */
public interface ConnectContract {
	interface ConnectView extends BaseView {
	}

	interface ConnectPresenter extends MvpPresenter<ConnectView> {
		void setCurrentSSID(String ssid);
	}

	interface ChooseSSIDView extends BaseView{
		void showAllSSID(List<SSID> SSIDList);
	}

	interface ChooseSSIDPresenter extends MvpPresenter<ChooseSSIDView>{
		void selectAllSSID(List<String> SSIDList);
	}
}
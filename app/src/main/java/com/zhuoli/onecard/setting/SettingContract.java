package com.zhuoli.onecard.setting;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.zhuoli.onecard.base.BaseView;
import com.zhuoli.onecard.setting.adapter.model.PrintContent;

import java.util.Date;
import java.util.List;

/**
 * @author CLD
 */
public interface SettingContract {

	interface View extends BaseView{
		void showProgressIndicator();
		void hideProgressIndicator();
		void showSucceed(String message);
		void showError(String error);
	}

	interface ParameterView extends View{
		void readSucceed(String charWidth, String pointSize, String printDelay, String charSpace, String charStyle);
	}

	interface ParameterPresenter extends MvpPresenter<ParameterView>{
		void readParameter();
		void writeParameter(String charWidth, String pointSize, String printDelay, String charSpace, String charStyle);
	}

	interface PrintView extends View{
		void onReadSucceed(List<PrintContent> contentList);
	}

	interface PrintPresenter extends MvpPresenter<PrintView>{
		void readPrint();
		void writePrint(String content);
	}

	interface CounterPagerView extends BaseView{

	}

	interface CounterPagerPresenter extends MvpPresenter<CounterPagerView>{

	}

	interface CounterView extends View{
		void readSucceed(String sn, String way, String digit, String pre, String target, String start, String current);
	}

	interface CounterPresenter extends MvpPresenter<CounterView>{
		void readCounterConfig(String sn);
		void writeCounterConfig(String sn, String way, String digit, String pre, String target, String start, String current);
	}

	interface CleanView extends View{
		void stopSucceed();
	}

	interface CleanPresenter extends MvpPresenter<CleanView>{
		void cleanSprayer();
		void cleanSprayer(String sn);
		void stopClean();
	}

	interface DateView extends View{
		void readSucceed(Date date);
	}

	interface DatePresenter extends MvpPresenter<DateView>{
		void readDate();
		void writeDate(Date date);
	}

	interface SystemView extends View{
		void showSystemConfig(String address, String name, String deviceAddress);
	}

	interface SystemPresenter extends MvpPresenter<SystemView>{
		void writeSystemConfig(String ssid, String name, String deviceAddress,String password);
		void readSystemConfig();
	}
}
package com.zhuoli.onecard.setting;

import android.app.ProgressDialog;

import com.zhuoli.onecard.data.DataComponent;
import com.zhuoli.onecard.utils.Toastor;
import com.zhuoli.onecard.utils.daggerScope.FragmentScope;

import dagger.Component;

/**
 * @author CLD
 */
@FragmentScope
@Component(dependencies = DataComponent.class, modules = {SettingModule.class})
public interface SettingComponent {
	SettingContract.ParameterPresenter getParameterPresenter();
	
	SettingContract.PrintPresenter getPrintPresenter();
	
	SettingContract.CounterPresenter getCounterPresenter();
	
	SettingContract.CleanPresenter getCleanPresenter();
	
	SettingContract.DatePresenter getDatePresener();
	
	SettingContract.SystemPresenter getSystemPresenter();

	Toastor getToastor();

	ProgressDialog getProgressDialog();

	void inject(SettingActivity activity);
}
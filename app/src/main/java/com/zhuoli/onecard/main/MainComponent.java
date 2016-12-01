package com.zhuoli.onecard.main;

import com.zhuoli.onecard.data.DataComponent;
import com.zhuoli.onecard.utils.Toastor;
import com.zhuoli.onecard.utils.daggerScope.FragmentScope;

import dagger.Component;

/**
 * @author CLD
 */
@FragmentScope
@Component(dependencies = DataComponent.class, modules = {MainModule.class})
public interface MainComponent {
	Toastor getToastor();

	MainContract.Presenter getMainPresenter();
	MainContract.OtherPresenter getOtherPresenter();
	void inject(MainActivity activity);
}
package com.zhuoli.onecard.connect;

import com.zhuoli.onecard.data.DataComponent;
import com.zhuoli.onecard.utils.Toastor;
import com.zhuoli.onecard.utils.daggerScope.FragmentScope;

import dagger.Component;

/**
 * @author CLD
 */
@FragmentScope
@Component(dependencies = DataComponent.class, modules = {ConnectModule.class})
public interface ConnectComponent {
	Toastor getToastor();
	ConnectContract.ConnectPresenter getConnectPresenter();
	ConnectContract.ChooseSSIDPresenter getChooseSSIDPresenter();
	void inject(ConnectActivity activity);
}
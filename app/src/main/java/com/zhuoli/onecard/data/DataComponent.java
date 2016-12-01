package com.zhuoli.onecard.data;


import android.content.Context;

import com.zhuoli.onecard.AppComponent;
import com.zhuoli.onecard.data.utils.Preferences;
import com.zhuoli.onecard.utils.daggerScope.DataScope;

import dagger.Component;

@DataScope
@Component(dependencies = AppComponent.class, modules = {DataModule.class})
public interface DataComponent {

	Preferences getPreferences();

	DataSource getDataSource();

	Context getAppContext();
}
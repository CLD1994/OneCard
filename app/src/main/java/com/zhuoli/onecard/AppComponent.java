package com.zhuoli.onecard;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Component;
import io.realm.Realm;
import retrofit2.Retrofit;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
	// 全局上下文
	Context getAppContext();

	//全局首选项
	SharedPreferences getSharedPreferences();

	//数据库框架
	Realm getRealm();

	//网络请求框架
	Retrofit getRetrofit();
}
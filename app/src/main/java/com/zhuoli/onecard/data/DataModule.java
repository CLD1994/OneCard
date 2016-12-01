package com.zhuoli.onecard.data;

import android.content.SharedPreferences;

import com.zhuoli.onecard.data.impl.DataRepository;
import com.zhuoli.onecard.data.source.local.LocalDataSource;
import com.zhuoli.onecard.data.source.local.database.RealmConsole;
import com.zhuoli.onecard.data.source.remote.RemoteDataSource;
import com.zhuoli.onecard.data.source.remote.api.RetrofitConsole;
import com.zhuoli.onecard.data.utils.Preferences;
import com.zhuoli.onecard.utils.daggerScope.DataScope;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import retrofit2.Retrofit;

@Module
public final class DataModule {

	@Provides
	@DataScope
	LocalDataSource provideLocalDataSource(Realm realm){
		return new RealmConsole(realm);
	}

	@Provides
	@DataScope
	RemoteDataSource provideRemoteDataSource(Retrofit retrofit){
		return new RetrofitConsole(retrofit);
	}

	@Provides
	@DataScope
	Preferences providePreferences(SharedPreferences sharedPreferences){
		return new Preferences(sharedPreferences);
	}

	@Provides
	@DataScope
	DataSource provideDataSource(LocalDataSource localDataSource, RemoteDataSource remoteDataSource, Preferences preferences){
		return new DataRepository(localDataSource, remoteDataSource, preferences);
	}
}
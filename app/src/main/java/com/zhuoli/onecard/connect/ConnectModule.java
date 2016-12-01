package com.zhuoli.onecard.connect;

import android.content.Context;

import com.zhuoli.onecard.connect.presenter.ChooseSSIDPresenter;
import com.zhuoli.onecard.connect.presenter.ConnectPresenter;
import com.zhuoli.onecard.connect.usecase.SelectAllSSID;
import com.zhuoli.onecard.connect.usecase.SetCurrentSSID;
import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.utils.Toastor;
import com.zhuoli.onecard.utils.daggerScope.FragmentScope;

import dagger.Module;
import dagger.Provides;

/**
 * @author CLD
 */
@Module
class ConnectModule {

	private final Context mContext;

	public ConnectModule(Context context) {
		mContext = context;
	}

	@Provides
	@FragmentScope
	Toastor provideToastor(){
		return new Toastor(mContext);
	}

	@Provides
	@FragmentScope
	ConnectContract.ConnectPresenter provideConnectPresenter(DataSource dataSource){
		return new ConnectPresenter(new SetCurrentSSID(dataSource));
	}

	@Provides
	@FragmentScope
	ConnectContract.ChooseSSIDPresenter provideChooseSSIDPresenter(DataSource dataSource){
		return new ChooseSSIDPresenter(new SelectAllSSID(dataSource));
	}
}

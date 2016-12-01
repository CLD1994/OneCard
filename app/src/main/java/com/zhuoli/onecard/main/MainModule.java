package com.zhuoli.onecard.main;

import android.content.Context;

import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.main.presenter.MainPresenter;
import com.zhuoli.onecard.main.presenter.OtherPresenter;
import com.zhuoli.onecard.main.view.MainFragment;
import com.zhuoli.onecard.utils.Toastor;
import com.zhuoli.onecard.utils.daggerScope.FragmentScope;

import dagger.Module;
import dagger.Provides;

/**
 * @author CLD
 */
@Module
final class MainModule {

	private final Context mContext;

	public MainModule(Context context) {
		mContext = context;
	}

	@Provides
	@FragmentScope
	Toastor provideToastor(){
		return new Toastor(mContext);
	}


	@Provides
	@FragmentScope
	MainContract.Presenter provideMainPresenter(DataSource dataSource) {
		return new MainPresenter();
	}

	@Provides
	@FragmentScope
	MainContract.View provideMainView() {
		return new MainFragment();
	}

	@Provides
	@FragmentScope
	MainContract.OtherPresenter provideOtherPresenter(DataSource dataSource){
		return new OtherPresenter();
	}
}

package com.zhuoli.onecard.setting;

import android.app.ProgressDialog;
import android.content.Context;

import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.setting.presenter.CleanPresenter;
import com.zhuoli.onecard.setting.presenter.CounterPresenter;
import com.zhuoli.onecard.setting.presenter.DatePresenter;
import com.zhuoli.onecard.setting.presenter.ParameterPresenter;
import com.zhuoli.onecard.setting.presenter.PrintPresenter;
import com.zhuoli.onecard.setting.presenter.SystemPresenter;
import com.zhuoli.onecard.setting.usecase.CleanSprayer;
import com.zhuoli.onecard.setting.usecase.ReadCounterConfig;
import com.zhuoli.onecard.setting.usecase.ReadDate;
import com.zhuoli.onecard.setting.usecase.ReadParameter;
import com.zhuoli.onecard.setting.usecase.ReadPrintConfig;
import com.zhuoli.onecard.setting.usecase.ReadSystemConfig;
import com.zhuoli.onecard.setting.usecase.StopCleanSprayer;
import com.zhuoli.onecard.setting.usecase.WriteCounterConfig;
import com.zhuoli.onecard.setting.usecase.WriteDate;
import com.zhuoli.onecard.setting.usecase.WriteParameter;
import com.zhuoli.onecard.setting.usecase.WritePrintConfig;
import com.zhuoli.onecard.setting.usecase.WriteSystemConfig;
import com.zhuoli.onecard.utils.Toastor;
import com.zhuoli.onecard.utils.daggerScope.FragmentScope;

import dagger.Module;
import dagger.Provides;

/**
 * @author CLD
 */
@Module
class SettingModule {

	private final Context mContext;

	public SettingModule(Context context) {
		mContext = context;
	}

	@Provides
	@FragmentScope
	Toastor provideToastor(){
		return new Toastor(mContext);
	}

	@Provides
	@FragmentScope
	ProgressDialog provideProgressDialog(){
		ProgressDialog progressDialog = new ProgressDialog(mContext);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setTitle("正在加载");
		progressDialog.setMessage("请稍后...");
		return progressDialog;
	}

	@Provides
	@FragmentScope
	SettingContract.ParameterPresenter provideSettingParameterPresenter(DataSource dataSource) {
		return new ParameterPresenter(new ReadParameter(dataSource), new WriteParameter(dataSource));
	}
	
	@Provides
	@FragmentScope
	SettingContract.PrintPresenter provideSettingPrintPresenter(DataSource dataSource) {
		return new PrintPresenter(new ReadPrintConfig(dataSource), new WritePrintConfig(dataSource));
	}
	
	@Provides
	SettingContract.CounterPresenter provideSettingCounterPresenter(DataSource dataSource) {
		return new CounterPresenter(new ReadCounterConfig(dataSource), new WriteCounterConfig(dataSource));
	}
	
	@Provides
	@FragmentScope
	SettingContract.CleanPresenter provideSettingCleanPresenter(DataSource dataSource) {
		return new CleanPresenter(new CleanSprayer(dataSource),new StopCleanSprayer(dataSource));
	}
	
	@Provides
	@FragmentScope
	SettingContract.DatePresenter provideSettingDatePresenter(DataSource dataSource) {
		return new DatePresenter(new WriteDate(dataSource),new ReadDate(dataSource));
	}
	
	@Provides
	@FragmentScope
	SettingContract.SystemPresenter provideSettingSystemPresenter(DataSource dataSource) {
		return new SystemPresenter(new ReadSystemConfig(dataSource),new WriteSystemConfig(dataSource));
	}
}

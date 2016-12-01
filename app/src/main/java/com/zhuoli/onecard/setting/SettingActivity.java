package com.zhuoli.onecard.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.zhuoli.onecard.R;
import com.zhuoli.onecard.base.BaseActivity;
import com.zhuoli.onecard.connect.ConnectEvent;
import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.main.view.MainFragment;
import com.zhuoli.onecard.setting.view.CleanFragment;
import com.zhuoli.onecard.setting.view.CounterPagerFragment;
import com.zhuoli.onecard.setting.view.DateFragment;
import com.zhuoli.onecard.setting.view.ParameterFragment;
import com.zhuoli.onecard.setting.view.PrintFragment;
import com.zhuoli.onecard.setting.view.SystemFragment;

import javax.inject.Inject;

import rx.functions.Action1;

import static com.zhuoli.onecard.main.view.MainFragment.SETTING_CLEAN;
import static com.zhuoli.onecard.main.view.MainFragment.SETTING_PRINT;
import static com.zhuoli.onecard.main.view.OtherFragment.SETTING_COUNTER;
import static com.zhuoli.onecard.main.view.OtherFragment.SETTING_DATE;
import static com.zhuoli.onecard.main.view.OtherFragment.SETTING_PARAMETER;
import static com.zhuoli.onecard.main.view.OtherFragment.SETTING_SYSTEM;

/**
 * @author CLD
 */
public class SettingActivity extends BaseActivity<SettingComponent> {

	@Override
	protected Integer getLayoutId() {
		return R.layout.activity_setting;
	}

	@Override
	protected Integer getToolbarId() {
		return R.id.toolbar;
	}

	@Override
	protected Integer getFragmentContainerId() {
		return R.id.fragment_container;
	}

	@Override
	protected SettingComponent injectDependencies() {
		SettingComponent component = DaggerSettingComponent.builder()
				.dataComponent(getApp().getDataComponent())
				.settingModule(new SettingModule(this))
				.build();
		component.inject(this);
		return component;
	}

	@Inject
	DataSource mDataSource;

	@Override
	protected void initView(Bundle savedInstanceState) {
		if (getIntent().hasExtra(MainFragment.TAG) && savedInstanceState == null){
			String target = getIntent().getStringExtra(MainFragment.TAG);
			String name = mDataSource.selectSSID(mDataSource.getCurrentSSID()).getName();
			SpannableString title;
			switch (target){
				case SETTING_PARAMETER:
					addFragment(new ParameterFragment(),ParameterFragment.class.getSimpleName(),false);
					title = new SpannableString(name + SETTING_PARAMETER);
					break;
				case SETTING_PRINT:
					addFragment(new PrintFragment(),PrintFragment.class.getSimpleName(),false);
					title = new SpannableString(name + SETTING_PRINT);
					break;
				case SETTING_COUNTER:
					addFragment(new CounterPagerFragment(),CounterPagerFragment.class.getSimpleName(),false);
					title = new SpannableString(name + SETTING_COUNTER);
					break;
				case SETTING_CLEAN:
					addFragment(new CleanFragment(),CleanFragment.class.getSimpleName(),false);
					title = new SpannableString(name + SETTING_CLEAN);
					break;
				case SETTING_DATE:
					addFragment(new DateFragment(),DateFragment.class.getSimpleName(),false);
					title = new SpannableString(name + SETTING_DATE);
					break;
				case SETTING_SYSTEM:
					addFragment(new SystemFragment(),SystemFragment.class.getSimpleName(),false);
					title = new SpannableString(name + SETTING_SYSTEM);
					break;
				default:
					title = null;
			}
			if (title != null){
				title.setSpan(new ForegroundColorSpan(Color.RED),0,name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				setToolbarTitle(title);
			}
		}

		busSubscribe(ConnectEvent.class, new Action1<ConnectEvent>() {
			@Override
			public void call(ConnectEvent connectEvent) {
				if (connectEvent.getType() == ConnectEvent.WIFI_DISCONNECTED){
					finish();
				}
			}
		});
	}
}
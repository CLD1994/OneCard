package com.zhuoli.onecard.main;

import android.os.Bundle;
import android.text.TextUtils;

import com.zhuoli.onecard.R;
import com.zhuoli.onecard.base.BaseActivity;
import com.zhuoli.onecard.connect.ConnectEvent;
import com.zhuoli.onecard.customview.PasswordDialogFragment;
import com.zhuoli.onecard.main.view.OtherFragment;
import com.zhuoli.onecard.utils.Toastor;

import javax.inject.Inject;

import rx.functions.Action1;

/**
 * @author CLD
 */
public class MainActivity extends BaseActivity<MainComponent> implements PasswordDialogFragment.PasswordDialogListener{

	@Inject
	MainContract.View mMainFragment;
	
	@Override
	protected Integer getLayoutId() {
		return R.layout.activity_main;
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
	protected MainComponent injectDependencies() {
		MainComponent component = DaggerMainComponent.builder()
				.dataComponent(getApp().getDataComponent())
				.mainModule(new MainModule(this))
				.build();
		component.inject(this);
		return component;
	}

	@Inject
	Toastor mToastor;

	@Override
	protected void initView(Bundle savedInstanceState) {
		if (savedInstanceState == null){
			addFragment(mMainFragment, "mainFragment", false);
		}
		setToolbarTitle("设置");

		busSubscribe(MainEvent.class, new Action1<MainEvent>() {
			@Override
			public void call(MainEvent mainEvent) {
				if (mainEvent.getType() == MainEvent.OTHER_SETTING){

				}
			}
		});

		busSubscribe(ConnectEvent.class, new Action1<ConnectEvent>() {
			@Override
			public void call(ConnectEvent connectEvent) {
				if (connectEvent.getType() == ConnectEvent.WIFI_DISCONNECTED){
					finish();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPositiveButtonClick(String password) {
		if (TextUtils.equals(password,"LINGDIAN")){
			replaceFragment(new OtherFragment(),"other",true);
		}else {
			mToastor.showToast("密码错误!");
		}
	}

	@Override
	public void onNegativeButtonClick() {

	}
}
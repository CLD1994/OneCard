package com.zhuoli.onecard.connect;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.zhuoli.onecard.R;
import com.zhuoli.onecard.base.BaseActivity;
import com.zhuoli.onecard.connect.view.ConnectFragment;
import com.zhuoli.onecard.customview.PasswordDialogFragment;
import com.zhuoli.onecard.data.utils.Preferences;
import com.zhuoli.onecard.utils.Toastor;

import javax.inject.Inject;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;
import rx.functions.Action1;


/**
 * @author CLD
 */
@RuntimePermissions
public class ConnectActivity extends BaseActivity<ConnectComponent> implements PasswordDialogFragment.PasswordDialogListener{

	@Override
	protected Integer getLayoutId() {
		return R.layout.activity_connect;
	}

	@Override
	protected Integer getToolbarId() {
		return null;
	}

	@Override
	protected Integer getFragmentContainerId() {
		return R.id.fragment_container;
	}

	@Override
	protected ConnectComponent injectDependencies() {
		ConnectComponent component = DaggerConnectComponent.builder()
				.dataComponent(getApp().getDataComponent())
				.connectModule(new ConnectModule(this))
				.build();
		component.inject(this);
		return component;
	}

	@Inject
	Preferences mPreferences;

	@Inject
	Toastor mToastor;

	ConnectFragment connectFragment;

	@Override
	protected void initView(Bundle savedInstanceState) {

		if (mPreferences.isAdmin()){
			connectFragment = ConnectFragment.newInstance();
			addFragment(connectFragment,"searching",false);

			busSubscribe(ConnectEvent.class, new Action1<ConnectEvent>() {
				@Override
				public void call(ConnectEvent connectEvent) {
					if (connectEvent.getType() == ConnectEvent.SEARCHING_SSID){
						ConnectActivityPermissionsDispatcher.startServiceWithCheck(ConnectActivity.this);
					}
				}
			});
		}else {
			new PasswordDialogFragment().show(getSupportFragmentManager(),"passwordDialog");
		}
	}

	@NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
	public void startService() {
		connectFragment.changeState(ConnectFragment.SEARCHING);
	}


	@OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
	void OnShowRationale(final PermissionRequest request) {
		Logger.d("OnShowRationale has been called");
		request.proceed();
	}

	@OnNeverAskAgain({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
	void OnNeverAskAgain() {
		Logger.d("OnNeverAskAgain has been called");
	}

	@OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
	void OnPermissionDenied() {
		Logger.d("OnPermissionDenied has been called");
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		ConnectActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
	}

	@Override
	public void onPositiveButtonClick(String password) {
		if (TextUtils.equals(password,"zerolink") || TextUtils.equals(password,"ZEROLINK")){
			mPreferences.setAdminFlag(true);
			connectFragment = ConnectFragment.newInstance();
			addFragment(connectFragment,"searching",false);

			busSubscribe(ConnectEvent.class, new Action1<ConnectEvent>() {
				@Override
				public void call(ConnectEvent connectEvent) {
					if (connectEvent.getType() == ConnectEvent.SEARCHING_SSID){
						ConnectActivityPermissionsDispatcher.startServiceWithCheck(ConnectActivity.this);
					}
				}
			});
		}else {
			mToastor.showToast("密码输入错误!程序自动关闭!");
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					finish();
				}
			},2000);
		}

	}

	@Override
	public void onNegativeButtonClick() {
		mToastor.showToast("程序将自动关闭!");
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				finish();
			}
		},2000);
	}
}
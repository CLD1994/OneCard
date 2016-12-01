package com.zhuoli.onecard.setting.presenter;

import android.text.TextUtils;

import com.zhuoli.onecard.base.BasePresenter;
import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.setting.SettingContract;
import com.zhuoli.onecard.setting.usecase.ReadSystemConfig;
import com.zhuoli.onecard.setting.usecase.WriteSystemConfig;

/**
 * Created by CLD on 2016/9/27 0027.
 *
 */

public class SystemPresenter
		extends BasePresenter<SettingContract.SystemView>
		implements SettingContract.SystemPresenter{

	private final ReadSystemConfig mReadSystemConfig;
	private final WriteSystemConfig mWriteSystemConfig;

	public SystemPresenter(ReadSystemConfig readSystemConfig, WriteSystemConfig writeSystemConfig) {
		mWriteSystemConfig = writeSystemConfig;
		mReadSystemConfig = readSystemConfig;
	}

	@Override
	public void writeSystemConfig(String ssid, String name, String deviceAddress, String password) {
		if (TextUtils.equals("123456",password)){
			executeUseCase(mWriteSystemConfig, new WriteSystemConfig.RequestValues(ssid, name,deviceAddress), new UseCase.UseCaseCallback<WriteSystemConfig.ResponseValue>() {
				@Override
				public void onSuccess(WriteSystemConfig.ResponseValue response) {
					if (isViewAttached()){
						//noinspection ConstantConditions
						getView().showSucceed("修改成功!");
					}
				}

				@Override
				public void onError(Status status) {
					if (isViewAttached()){
						//noinspection ConstantConditions
						getView().showError(status.getCode() + status.getMsg());
					}
				}
			});
		}else {
			if (isViewAttached()){
				//noinspection ConstantConditions
				getView().showError("密码错误");
			}
		}

	}

	@Override
	public void readSystemConfig() {
		executeUseCase(mReadSystemConfig, new ReadSystemConfig.RequestValues(), new UseCase.UseCaseCallback<ReadSystemConfig.ResponseValue>() {
			@Override
			public void onSuccess(ReadSystemConfig.ResponseValue response) {
				if (isViewAttached()){
					//noinspection ConstantConditions
					getView().showSystemConfig(response.getSsid(),response.getName(),response.getAddress());
				}
			}

			@Override
			public void onError(Status status) {
				if (isViewAttached()){
					//noinspection ConstantConditions
					getView().showError(status.getCode() + status.getMsg());
				}
			}
		});
	}
}

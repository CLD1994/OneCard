package com.zhuoli.onecard.setting.presenter;

import com.zhuoli.onecard.base.BasePresenter;
import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.setting.SettingContract;
import com.zhuoli.onecard.setting.usecase.ReadParameter;
import com.zhuoli.onecard.setting.usecase.WriteParameter;

/**
 * Created by CLD on 2016/9/26 0026.
 *
 */

public class ParameterPresenter
		extends BasePresenter<SettingContract.ParameterView>
		implements SettingContract.ParameterPresenter{

	private final ReadParameter mReadParameter;
	private final WriteParameter mWriteParameter;

	public ParameterPresenter(ReadParameter readParameter, WriteParameter writeParameter) {
		mReadParameter = readParameter;
		mWriteParameter = writeParameter;
	}

	@Override
	public void readParameter() {
		if (isViewAttached()){
			//noinspection ConstantConditions
			getView().showProgressIndicator();
		}
		ReadParameter.RequestValues requestValues = new ReadParameter.RequestValues();
		executeUseCase(mReadParameter, requestValues, new UseCase.UseCaseCallback<ReadParameter.ResponseValue>() {
			@Override
			public void onSuccess(ReadParameter.ResponseValue response) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().readSucceed(
							response.getCharWidth(),
							response.getPointSize(),
							response.getPrintDelay(),
							response.getCharSpace(),
							response.getCharStyle()
					);
				}
			}

			@Override
			public void onError(Status status) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().showError(status.getMsg());
				}
			}
		});
	}

	@Override
	public void writeParameter(String charWidth, String pointSize, String printDelay, String charSpace, String charStyle) {
		if (isViewAttached()){
			//noinspection ConstantConditions
			getView().showProgressIndicator();
		}
		WriteParameter.RequestValues requestValues =
				new WriteParameter.RequestValues(charWidth,pointSize,printDelay,charSpace,charStyle);

		executeUseCase(mWriteParameter, requestValues, new UseCase.UseCaseCallback<WriteParameter.ResponseValue>() {
			@Override
			public void onSuccess(WriteParameter.ResponseValue response) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().showSucceed(response.getMessage());
				}
			}

			@Override
			public void onError(Status status) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().showError(status.getMsg());
				}
			}
		});
	}
}

package com.zhuoli.onecard.setting.presenter;

import com.zhuoli.onecard.base.BasePresenter;
import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.setting.SettingContract;
import com.zhuoli.onecard.setting.usecase.ReadPrintConfig;
import com.zhuoli.onecard.setting.usecase.WritePrintConfig;

/**
 * Created by CLD on 2016/9/27 0027.
 */

public class PrintPresenter
		extends BasePresenter<SettingContract.PrintView>
		implements SettingContract.PrintPresenter{

	private final ReadPrintConfig mReadPrintConfig;
	private final WritePrintConfig mWritePrintConfig;

	public PrintPresenter(ReadPrintConfig readPrintConfig, WritePrintConfig writePrintConfig) {
		mReadPrintConfig = readPrintConfig;
		mWritePrintConfig = writePrintConfig;
	}

	@Override
	public void readPrint() {
		if (isViewAttached()){
			//noinspection ConstantConditions
			getView().showProgressIndicator();
		}
		executeUseCase(mReadPrintConfig, new ReadPrintConfig.RequestValues(), new UseCase.UseCaseCallback<ReadPrintConfig.ResponseValue>() {
			@Override
			public void onSuccess(ReadPrintConfig.ResponseValue response) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().onReadSucceed(response.getPrintContentList());
				}
			}

			@Override
			public void onError(Status status) {
				if(isViewAttached()){
					getView().hideProgressIndicator();
					getView().showError(status.getCode() + status.getMsg());
				}
			}
		});
	}

	@Override
	public void writePrint(String content) {
		if (isViewAttached()){
			//noinspection ConstantConditions
			getView().showProgressIndicator();
		}

		executeUseCase(mWritePrintConfig, new WritePrintConfig.RequestValues(content), new UseCase.UseCaseCallback<WritePrintConfig.ResponseValue>() {
			@Override
			public void onSuccess(WritePrintConfig.ResponseValue response) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().showSucceed(response.getMessage());
				}
			}

			@Override
			public void onError(Status status) {
				if (isViewAttached()){
					getView().hideProgressIndicator();
					getView().showError(status.getCode() + status.getMsg());
				}
			}
		});
	}
}

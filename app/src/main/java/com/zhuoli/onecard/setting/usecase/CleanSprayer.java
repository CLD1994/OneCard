package com.zhuoli.onecard.setting.usecase;

import android.os.Handler;
import android.text.TextUtils;

import com.zhuoli.onecard.base.UseCase;
import com.zhuoli.onecard.data.DataSource;
import com.zhuoli.onecard.data.SysConstant;
import com.zhuoli.onecard.data.source.remote.response.Status;
import com.zhuoli.onecard.utils.CryptoUtils;
import com.zhuoli.onecard.utils.Util;

import java.io.IOException;
import java.util.concurrent.FutureTask;

/**
 * Created by CLD on 2016/9/27 0027.
 */

public class CleanSprayer extends UseCase<CleanSprayer.RequestValues, CleanSprayer.ResponseValue> {

    private final DataSource mDataSource;

    private Cancelable mCancelable;

    public CleanSprayer(DataSource dataSource) {
        mDataSource = dataSource;
        mCancelable = new Cancelable();
    }

    @Override
    protected String getTag() {
        return CleanSprayer.class.getSimpleName();
    }

    @Override
    protected UseCase.Cancelable executeUseCase(RequestValues requestValues) {
        mCancelable.mFutureTask = mDataSource.execute(requestValues.getCommand(), new DataSource.Callback<byte[]>() {
            @Override
            public void onDataLoad(final byte[] data) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getUseCaseCallback().onSuccess(new ResponseValue(data));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 4000);

            }

            @Override
            public void onError(Status error) {
                getUseCaseCallback().onError(error);
            }
        });
        return mCancelable;
    }

    public static class RequestValues implements UseCase.RequestValues {
        private String sn;

        public RequestValues() {
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        byte[] getCommand() {
            if (TextUtils.isEmpty(sn)) {
                return Util.encodingCommand(SysConstant.CLEAN_SPRAYER, null);
            } else {
                String data = CryptoUtils.HEX.decToHexString(sn, 8);
                return Util.encodingCommand(SysConstant.CLEAN_SPRAYER_SINGLE, data);
            }

        }
    }

    public static class ResponseValue implements UseCase.ResponseValue {
        private String message;

        public ResponseValue(byte[] responseValue) throws IOException {
            if (Util.writeIsSucceed(responseValue)) {
                message = SysConstant.OK;
            } else {
                throw new IOException("失败,请重新请求!");
            }
        }

        public String getMessage() {
            return message;
        }
    }

    public static class Cancelable implements UseCase.Cancelable {

        private FutureTask mFutureTask;

        public Cancelable() {

        }

        public Cancelable(FutureTask futureTask) {
            mFutureTask = futureTask;
        }

        @Override
        public boolean isCanceled() {
            return mFutureTask.isCancelled();
        }

        @Override
        public void cancel() {
            mFutureTask.cancel(true);
        }
    }
}

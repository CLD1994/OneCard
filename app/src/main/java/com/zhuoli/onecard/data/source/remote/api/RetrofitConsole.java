package com.zhuoli.onecard.data.source.remote.api;

import com.orhanobut.logger.Logger;
import com.zhuoli.onecard.data.source.remote.RemoteDataSource;
import com.zhuoli.onecard.data.source.remote.response.Status;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by CLD on 2016/7/22 0022.
 */
public class RetrofitConsole implements RemoteDataSource{

    private final Retrofit mRetrofit;

    public RetrofitConsole(Retrofit retrofit) {
        mRetrofit = retrofit;
    }

    private Status parseError(Response<?> response) {

        Converter<ResponseBody, Status> converter =
                mRetrofit.responseBodyConverter(Status.class, new Annotation[0]);
        Status status;

        try {
            status = converter.convert(response.errorBody());
        } catch (IOException e) {
            status = new Status();
            status.setCode(String.valueOf(response.code()));
            status.setMsg(response.message());
            return status;
        }

        return status;
    }

    private Status parseError(Throwable t) {

        Status status = new Status();

        if (t instanceof IOException) {
            //Add your code for displaying no network connection error
            status.setCode(Status.NETWORK_ERROR);
            Logger.e("network connection error");
            status.setMsg(t.getMessage());
        } else {
            status.setCode(Status.SYSTEM_ERROR);
            Throwable throwable = t;
            while (throwable.getCause() != null) {
                throwable = throwable.getCause();
            }
            status.setMsg(throwable.getMessage());
        }
        return status;
    }

}

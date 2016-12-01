/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhuoli.onecard.base;

import android.os.Handler;

import com.zhuoli.onecard.data.SysConstant;
import com.zhuoli.onecard.data.source.remote.response.Status;

/**
 * Use cases are the entry points to the domain layer.
 *
 * @param <Q> the request type
 * @param <R> the response type
 */
public abstract class UseCase<Q extends UseCase.RequestValues, R extends UseCase.ResponseValue> {

    private int retryTime;

    private Q mRequestValues;

    private UseCaseCallback<R> mUseCaseCallback;

    Cancelable run(Q values, UseCase.UseCaseCallback<R> callback) {
        retryTime = 0;
        mRequestValues = values;
        mUseCaseCallback = callback;
        return executeUseCase(mRequestValues);
    }

    Cancelable run(Q values){
        retryTime = 0;
        mRequestValues = values;
        return executeUseCase(mRequestValues);
    }

    protected boolean retry(){
        if (retryTime++ < SysConstant.RETRY){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    executeUseCase(getRequestValues());
                }
            }, SysConstant.RETRY_INTERVAL);
            return true;
        }else {
            return false;
        }
    }

    public Q getRequestValues() {
        return mRequestValues;
    }

    public UseCaseCallback<R> getUseCaseCallback() {
        return mUseCaseCallback;
    }

    protected abstract String getTag();

    protected abstract Cancelable executeUseCase(Q requestValues);

    /**
     * Data passed to a request.
     */
    public interface RequestValues {
    }

    /**
     * Data received from a request.
     */
    public interface ResponseValue {
    }

    public interface UseCaseCallback<R> {
        void onSuccess(R response);
        void onError(Status status);
    }

    public interface Cancelable {

        boolean isCanceled();

        void cancel();
    }
}

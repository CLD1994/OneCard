package com.zhuoli.onecard;

import android.app.Application;
import android.util.Log;

import com.cengalabs.flatui.FlatUI;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.Settings;
import com.zhuoli.onecard.data.DaggerDataComponent;
import com.zhuoli.onecard.data.DataComponent;
import com.zhuoli.onecard.data.DataModule;

import io.realm.Realm;

/**
 * Created by CLD on 2016/7/22 0022.
 */
public class APP extends Application {

    private DataComponent mDataComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        AppComponent appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(getApplicationContext()))
                .build();

        mDataComponent = DaggerDataComponent.builder()
                .appComponent(appComponent)
                .dataModule(new DataModule())
                .build();

        FlatUI.initDefaultValues(getApplicationContext());

        Fresco.initialize(this);

        Realm.init(this);

        Logger.initialize(
                Settings.getInstance()
                        .isShowMethodLink(true)
                        .isShowThreadInfo(false)
                        .setMethodOffset(0)
                        .setLogPriority(BuildConfig.DEBUG ? Log.VERBOSE : Log.ASSERT)
        );
    }

    public DataComponent getDataComponent() {
        return mDataComponent;
    }
}

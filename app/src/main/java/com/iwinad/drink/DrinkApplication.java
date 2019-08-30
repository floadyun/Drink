package com.iwinad.drink;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/8/28
 * @description:
 */
public class DrinkApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);

        initLogger();
    }
    /**
     * 初始化日志打印
     */
    private void initLogger(){
        AndroidLogAdapter logAdapter = new AndroidLogAdapter() {
            @Override public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        };
        Logger.addLogAdapter(logAdapter);
    }
}

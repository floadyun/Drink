package com.iwinad.drink;

import android.app.Application;

import com.facebook.stetho.Stetho;

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
    }
}

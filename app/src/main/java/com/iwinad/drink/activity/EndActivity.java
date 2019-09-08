package com.iwinad.drink.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.base.lib.util.AppManager;
import com.iwinad.drink.R;
import com.base.lib.baseui.AppBaseActivity;

/*
 * @copyright : yixf
 *
 * @author : yixf
 *
 * @version :1.0
 *
 * @creation date: 2019/9/7
 *
 * @description:个人中心
 */
public class EndActivity extends AppBaseActivity {

    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_end);

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {//5秒后返回主页
            @Override
            public void run() {
                AppManager.getInstance().killActivity(IdentifyMoodActivity.class);
                AppManager.getInstance().killActivity(SelectDrinkActivity.class);
                finishSelf();
            }
        },5000);
    }
}

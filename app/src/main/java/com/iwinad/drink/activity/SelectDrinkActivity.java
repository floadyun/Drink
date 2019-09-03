package com.iwinad.drink.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.base.lib.baseui.AppBaseActivity;
import com.iwinad.drink.R;

import butterknife.ButterKnife;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/9/3
 * @description:选择酒
 */
public class SelectDrinkActivity extends AppBaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_drink_layout);
        ButterKnife.bind(this);
    }
}

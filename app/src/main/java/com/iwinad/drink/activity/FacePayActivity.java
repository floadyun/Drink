package com.iwinad.drink.activity;

import android.view.View;

import com.base.lib.baseui.AppBaseActivity;
import com.iwinad.drink.R;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/9/3
 * @description:刷脸支付
 */
public class FacePayActivity extends AppBaseActivity {
    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setContentView(R.layout.activity_face_pay_layout);
    }
}

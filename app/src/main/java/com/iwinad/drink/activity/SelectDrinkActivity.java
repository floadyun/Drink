package com.iwinad.drink.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.base.lib.baseui.AppBaseActivity;
import com.iwinad.drink.R;
import com.iwinad.drink.seriaport.DataSerialPort;
import com.iwinad.drink.seriaport.ICommonResult;
import com.iwinad.drink.seriaport.MixDrinkInfo;
import com.iwinad.drink.seriaport.SerialPortResponse;

import butterknife.ButterKnife;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/9/3
 * @description:选择酒
 */
public class SelectDrinkActivity extends AppBaseActivity {

    private DataSerialPort dataSerialPort = new DataSerialPort();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_drink_layout);
        ButterKnife.bind(this);

        dataSerialPort.init();
    }
    private void startDrink(int type){
        MixDrinkInfo mixDrinkInfo = new MixDrinkInfo();
        switch(type){
            case 0:   // 绿眼
                mixDrinkInfo.type = 0;
                mixDrinkInfo.bottles = new int[]{6, 3, 12};
                mixDrinkInfo.formulaCapacitys = new int[]{20, 15, 35};
                break;
            case 1:   // 微笑
                mixDrinkInfo.type = 0;
                mixDrinkInfo.bottles = new int[]{1, 11, 7};
                mixDrinkInfo.formulaCapacitys = new int[]{10, 20, 30};
                break;
            case 2:   // 三层彩虹鸡尾酒
                mixDrinkInfo.type = 1;
                mixDrinkInfo.bottles = new int[]{5, 3};
                mixDrinkInfo.formulaCapacitys = new int[]{30, 35};
                break;
        }
        int capacity = 0;
        for(int c : mixDrinkInfo.formulaCapacitys){
            capacity += c;
        }
        if(capacity < 100){
            mixDrinkInfo.timeOut = 100;
        } else if(capacity < 150){
            mixDrinkInfo.timeOut = 150;
        } else if(capacity < 200){
            mixDrinkInfo.timeOut = 200;
        } else {
            mixDrinkInfo.timeOut = 300;
        }
        dataSerialPort.write(mixDrinkInfo, new ICommonResult<SerialPortResponse>() {
            @Override
            public void callback(SerialPortResponse data) {
                if (data.errorCode == SerialPortResponse.ERR_BUSY) {
                    Log.d("demo","忙碌中...");
                    Toast.makeText(SelectDrinkActivity.this, "忙碌中...", Toast.LENGTH_SHORT).show();
                } else if (data.errorCode == SerialPortResponse.ERR_SUCCEED
                        || data.errorCode == SerialPortResponse.ERR_TIME_OUT
                        || data.errorCode == SerialPortResponse.ERR_FAILED) {
                    Toast.makeText(SelectDrinkActivity.this, "完成", Toast.LENGTH_SHORT).show();
                    Log.d("demo","完成");
                } else if (data.errorCode == SerialPortResponse.ERR_WAITING) {
                    Log.d("demo","等等：" + data.timeLeft);
                } else if (data.errorCode == SerialPortResponse.ERR_START) {
                    Log.d("demo","开始");
                    Toast.makeText(SelectDrinkActivity.this, "开始", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

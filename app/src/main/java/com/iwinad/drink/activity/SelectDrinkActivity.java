package com.iwinad.drink.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.base.lib.baseadapter.BaseQuickAdapter;
import com.base.lib.baseadapter.BaseViewHolder;
import com.base.lib.baseui.AppBaseActivity;
import com.base.lib.discretescrollview.DiscreteScrollView;
import com.base.lib.discretescrollview.InfiniteScrollAdapter;
import com.base.lib.discretescrollview.transform.Pivot;
import com.base.lib.discretescrollview.transform.ScaleTransformer;
import com.iwinad.drink.Consts;
import com.iwinad.drink.R;
import com.iwinad.drink.seriaport.DataSerialPort;
import com.iwinad.drink.seriaport.ICommonResult;
import com.iwinad.drink.seriaport.MixDrinkInfo;
import com.iwinad.drink.seriaport.SerialPortResponse;
import com.iwinad.drink.widget.ScaleImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/9/3
 * @description:选择酒
 */
public class SelectDrinkActivity extends AppBaseActivity {

    private DataSerialPort dataSerialPort = new DataSerialPort();

    private boolean isDrinking;

    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_drink);
        ButterKnife.bind(this);

     //   dataSerialPort.init();

        mHandler = new Handler();
    }
    private void startDrink(int type){
        MixDrinkInfo mixDrinkInfo = new MixDrinkInfo();
        switch(type){
            case 1:   // 绿眼
                mixDrinkInfo.type = 0;
                mixDrinkInfo.bottles = new int[]{11,10,12};
                mixDrinkInfo.formulaCapacitys = new int[]{15,35,20};
                break;
            case 2:   // 微笑
                mixDrinkInfo.type = 0;
                mixDrinkInfo.bottles = new int[]{3,14,6};
                mixDrinkInfo.formulaCapacitys = new int[]{10,20,30};
                break;
            case 3:   // 俄罗斯范儿
                mixDrinkInfo.type = 1;
                mixDrinkInfo.bottles = new int[]{3,11,12};
                mixDrinkInfo.formulaCapacitys = new int[]{20,20,30};
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
              //      Toast.makeText(SelectDrinkActivity.this, "忙碌中...", Toast.LENGTH_SHORT).show();
                } else if (data.errorCode == SerialPortResponse.ERR_SUCCEED
                        || data.errorCode == SerialPortResponse.ERR_TIME_OUT
                        || data.errorCode == SerialPortResponse.ERR_FAILED) {
                //    Toast.makeText(SelectDrinkActivity.this, "完成", Toast.LENGTH_SHORT).show();
                    Log.d("demo","完成");
                    isDrinking = false;
                } else if (data.errorCode == SerialPortResponse.ERR_WAITING) {
                    Log.d("demo","等等：" + data.timeLeft);
                } else if (data.errorCode == SerialPortResponse.ERR_START) {
                    Log.d("demo","开始");
                    isDrinking = true;
                  //  Toast.makeText(SelectDrinkActivity.this, "开始", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * 跳转至人脸识别
     */
    public void gotoFaceRecognition(View view){
        if(isDrinking)return;
        isDrinking = true;
        startDrink(Integer.valueOf(view.getTag().toString()));
        ((ScaleImageView)view).isPressed = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SelectDrinkActivity.this,FaceRecognitionActivity.class);
                intent.putExtra(Consts.FACE_TYPE,1);
                startActivity(intent);
                ((ScaleImageView)view).isPressed = false;
            }
        },1000);
    }
}

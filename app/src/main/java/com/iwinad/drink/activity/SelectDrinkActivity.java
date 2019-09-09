package com.iwinad.drink.activity;

import android.content.Intent;
import android.os.Bundle;
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

    @BindView(R.id.select_drink_view)
    DiscreteScrollView scrollView;

    private DataSerialPort dataSerialPort = new DataSerialPort();

    private BaseQuickAdapter quickAdapter;

    private Integer[] imageIds = new Integer[]{R.drawable.drink_green,R.drawable.drink_smile,R.drawable.drink_russia};

    private boolean isDrinking;

    private int selectPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_drink);
        ButterKnife.bind(this);

        dataSerialPort.init();

        initScrollView();
    }
    private void initScrollView(){
        quickAdapter = new BaseQuickAdapter<Integer, BaseViewHolder>(R.layout.item_select_drink){
            @Override
            protected void convert(BaseViewHolder helper,Integer item, int position) {
                helper.setImageResource(R.id.item_drink_image,item);
            }
        };
        scrollView.setItemTransformer(new ScaleTransformer.Builder()
                .setMaxScale(1.09f)
                .setMinScale(0.75f)
                .setPivotX(Pivot.X.CENTER) //CENTER is a default one
                .setPivotY(Pivot.Y.CENTER) //CENTER is a default one
                .build());
        scrollView.setOffscreenItems(2);
        scrollView.setAdapter(quickAdapter);
        scrollView.addOnItemChangedListener(new DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder>() {
            @Override
            public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
                selectPosition = adapterPosition;
            }
        });
        quickAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if(!isDrinking&&position==selectPosition){
                    startDrink(position);
                    gotoFaceRecognition();
                }
            }
        });
        quickAdapter.setNewData(Arrays.asList(imageIds));
    }
    private void startDrink(int type){
        MixDrinkInfo mixDrinkInfo = new MixDrinkInfo();
        switch(type){
            case 0:   // 绿眼
                mixDrinkInfo.type = 0;
                mixDrinkInfo.bottles = new int[]{11,10,12};
                mixDrinkInfo.formulaCapacitys = new int[]{15,35,20};
                break;
            case 1:   // 微笑
                mixDrinkInfo.type = 0;
                mixDrinkInfo.bottles = new int[]{3,14,6};
                mixDrinkInfo.formulaCapacitys = new int[]{10,20,30};
                break;
            case 2:   // 俄罗斯范儿
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
                    Toast.makeText(SelectDrinkActivity.this, "忙碌中...", Toast.LENGTH_SHORT).show();
                } else if (data.errorCode == SerialPortResponse.ERR_SUCCEED
                        || data.errorCode == SerialPortResponse.ERR_TIME_OUT
                        || data.errorCode == SerialPortResponse.ERR_FAILED) {
                    Toast.makeText(SelectDrinkActivity.this, "完成", Toast.LENGTH_SHORT).show();
                    Log.d("demo","完成");
                    isDrinking = false;
                } else if (data.errorCode == SerialPortResponse.ERR_WAITING) {
                    Log.d("demo","等等：" + data.timeLeft);
                } else if (data.errorCode == SerialPortResponse.ERR_START) {
                    Log.d("demo","开始");
                    isDrinking = true;
                    Toast.makeText(SelectDrinkActivity.this, "开始", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * 跳转至人脸识别
     */
    private void gotoFaceRecognition(){
//        Intent intent = new Intent(this,FaceRecognitionActivity.class);
//        intent.putExtra(Consts.FACE_TYPE,1);
//        startActivity(intent);
        Intent intent = new Intent(this,FacePayActivity.class);
        startActivity(intent);
    }
}

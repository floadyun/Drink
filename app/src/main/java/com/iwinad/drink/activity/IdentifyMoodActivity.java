package com.iwinad.drink.activity;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.base.lib.baseui.AppBaseActivity;
import com.iwinad.drink.Consts;
import com.vise.face.CameraPreview;
import com.vise.face.DetectorData;
import com.vise.face.DetectorProxy;
import com.vise.face.IDataListener;
import com.iwinad.drink.R;
import butterknife.BindView;
import butterknife.ButterKnife;

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
public class IdentifyMoodActivity extends AppBaseActivity {
    @BindView(R.id.face_detector_preview)
    CameraPreview mFace_detector_preview;
    private DetectorProxy mDetectorProxy;
    private IDataListener mDataListener = new IDataListener() {
        @Override
        public void onDetectorData(DetectorData detectorData) {
            if(detectorData.getLightIntensity()>150){
                //    takePicture();
            }
        }
    };
    private Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_mood);
        ButterKnife.bind(this);

        initFaceDetector();

        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                gotoSelectDrink();
            }
        },2000);
    }
    private void initFaceDetector(){
        //创建代理类，必须传入相机预览界面
        mDetectorProxy = new DetectorProxy.Builder(mFace_detector_preview)
                .setMinCameraPixels(3000000)
                .setDataListener(mDataListener)
                //设置预览相机的相机ID
                .setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT)
                .setDrawFaceRect(true)
                //设置人脸识别框是否为完整矩形
                .setFaceIsRect(false)
                //设置人脸识别框的RGB颜色
                .setFaceRectColor(Color.rgb(255, 203, 15))
                .build();
    }
    /**
     * 跳转至选酒
     */
    public void gotoSelectDrink(){
        Intent intent = new Intent(this,SelectDrinkActivity.class);
        intent.putExtra(Consts.FACE_TYPE,0);
        startActivity(intent);
    }
}

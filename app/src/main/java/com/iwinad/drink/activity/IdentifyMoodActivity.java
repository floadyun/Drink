package com.iwinad.drink.activity;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;

import com.base.lib.baseui.AppBaseActivity;
import com.iwinad.drink.Consts;
import com.iwinad.drink.util.SaveUtil;
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
                takePicture();
            }
        }
    };

    private Handler mHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_mood);
        ButterKnife.bind(this);

        initFaceDetector();

        mHandler = new Handler();
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
    private void takePicture(){
        mFace_detector_preview.getCamera().takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        }, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                mFace_detector_preview.getCamera().stopPreview();
                gotoSelectDrink();
                finish();
            }
        });
    }
    /**
     * 跳转至选酒
     */
    public void gotoSelectDrink(){
        Intent intent = new Intent(this,SelectDrinkActivity.class);
        intent.putExtra(Consts.FACE_TYPE,0);
        startActivity(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mDetectorProxy != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDetectorProxy.detector();
                }
            },1000);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mDetectorProxy != null) {
            mDetectorProxy.release();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}

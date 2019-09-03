package com.iwinad.drink.activity;

import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.Toast;

import com.base.lib.baseui.AppBaseActivity;
import com.iwinad.drink.R;
import com.vise.face.CameraPreview;
import com.vise.face.DetectorData;
import com.vise.face.DetectorProxy;
import com.vise.face.FaceRectView;
import com.vise.face.ICameraCheckListener;
import com.vise.face.IDataListener;
import com.vise.face.IFaceDetector;
import com.vise.log.ViseLog;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/9/3
 * @description:人脸识别
 */
public class FaceRecognitionActivity extends AppBaseActivity {
    @BindView(R.id.face_detector_preview)
    CameraPreview mFace_detector_preview;
    @BindView(R.id.face_detector_face)
    FaceRectView mFace_detector_face;

    private DetectorProxy mDetectorProxy;
    private DetectorData mDetectorData;

    private ICameraCheckListener mCameraCheckListener = new ICameraCheckListener() {
        @Override
        public void checkPermission(boolean isAllow) {
            ViseLog.i("checkPermission" + isAllow);
            if (!isAllow) {
                Toast.makeText(FaceRecognitionActivity.this, "权限申请被拒绝，请进入设置打开权限再试！", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        @Override
        public void checkPixels(long pixels, boolean isSupport) {
            ViseLog.i("checkPixels" + pixels);
            if (!isSupport) {
                Toast.makeText(FaceRecognitionActivity.this, "手机相机像素达不到要求！", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    };

    private IDataListener mDataListener = new IDataListener() {
        @Override
        public void onDetectorData(DetectorData detectorData) {
            mDetectorData = detectorData;
            ViseLog.i("识别数据:" + detectorData);
            //mHandler.sendEmptyMessage(0);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition_layout);
        ButterKnife.bind(this);

        checkPermission();

        initFaceDetector();
    }
    private void checkPermission(){
        ICameraCheckListener mCameraCheckListener = new ICameraCheckListener() {
            @Override
            public void checkPermission(boolean isAllow) {
                //权限是否允许
                ViseLog.i("checkPermission" + isAllow);
            }
            @Override
            public void checkPixels(long pixels, boolean isSupport) {
                //手机像素是否满足要求
                ViseLog.i("checkPixels" + pixels);
            }
        };
    }

    private void initFaceDetector(){
        //创建代理类，必须传入相机预览界面
        mDetectorProxy = new DetectorProxy.Builder(mFace_detector_preview)
                .setMinCameraPixels(3000000)
                .setCheckListener(mCameraCheckListener)
                .setDataListener(mDataListener)
                .setFaceRectView(mFace_detector_face)
                .setDrawFaceRect(true)
                .build();
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
            mDetectorProxy.detector();
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}

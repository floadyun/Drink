package com.iwinad.drink.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;
import com.base.lib.baseui.AppBaseActivity;
import com.base.lib.http.okhttputils.utils.ImageUtils;
import com.base.lib.util.DeviceUtils;
import com.base.lib.util.ImageUtil;
import com.iwinad.drink.Consts;
import com.iwinad.drink.R;
import com.iwinad.drink.model.FaceType;
import com.iwinad.drink.util.SaveUtil;
import com.vise.face.CameraPreview;
import com.vise.face.DetectorData;
import com.vise.face.DetectorProxy;
import com.vise.face.FaceRectView;
import com.vise.face.ICameraCheckListener;
import com.vise.face.IDataListener;
import com.vise.log.ViseLog;
import org.greenrobot.eventbus.EventBus;
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

    private int faceType;

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
            if(mDetectorData.getLightIntensity()>150){
                try {
                    takePicture();
                }catch (Exception e){

                }
            }
        }
    };
    private Handler mHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition);
        ButterKnife.bind(this);
        mFace_detector_face.setZOrderOnTop(true);
        mFace_detector_face.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        initFaceDetector();
        mHandler = new Handler();
        // Activity启动后就锁定为启动时的方向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        faceType = getIntent().getIntExtra(Consts.FACE_TYPE,0);
    }
    private void initFaceDetector(){
        //创建代理类，必须传入相机预览界面
        mDetectorProxy = new DetectorProxy.Builder(mFace_detector_preview)
                .setMinCameraPixels(3000000)
                .setCheckListener(mCameraCheckListener)
                .setDataListener(mDataListener)
                .setFaceRectView(mFace_detector_face)
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
                String avatorPath = SaveUtil.saveImage(FaceRecognitionActivity.this,mFace_detector_preview,mDetectorData,bytes);
                if(!TextUtils.isEmpty(avatorPath)){
                    gotoPayResult(avatorPath);
                    finish();
                }
            }
        });
    }
    private void gotoPayResult(String imagePath){
        if(faceType==0){
            EventBus.getDefault().post(new FaceType(faceType,imagePath));
        }else {
            Intent intent = new Intent(this,FacePayActivity.class);
            intent.putExtra(Consts.FACE_IAMGE_PATH,imagePath);
            startActivity(intent);
        }
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

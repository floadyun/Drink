package com.iwinad.drink.activity;

import android.content.Context;
import android.content.Intent;
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
import android.view.WindowManager;
import android.widget.Toast;
import com.base.lib.baseui.AppBaseActivity;
import com.base.lib.http.okhttputils.utils.ImageUtils;
import com.base.lib.util.DeviceUtils;
import com.base.lib.util.ImageUtil;
import com.iwinad.drink.Consts;
import com.iwinad.drink.R;
import com.iwinad.drink.model.FaceType;
import com.vise.face.CameraPreview;
import com.vise.face.DetectorData;
import com.vise.face.DetectorProxy;
import com.vise.face.FaceRectView;
import com.vise.face.ICameraCheckListener;
import com.vise.face.IDataListener;
import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
            if(mDetectorData.getLightIntensity()>150){
            //    takePicture();
            }
        }
    };
    private Handler mHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition_layout);
        ButterKnife.bind(this);
        mFace_detector_face.setZOrderOnTop(true);
        mFace_detector_face.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        initFaceDetector();
        mHandler = new Handler();

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
                saveImage(bytes);
                finish();
            }
        });
    }
    /**
     * 保存拍照的图片
     *
     * @param data
     */
    private void saveImage(byte[] data) {
        File pictureFile = getOutputFile(this, "face", "photo.jpg");//拍照图片
        File avatarFile = getOutputFile(this, "face", "avatar.jpg");//截取人脸图片
        if (pictureFile == null || avatarFile == null) {
            return;
        }
        if (pictureFile.exists()) {
            pictureFile.delete();
        }
        if (avatarFile.exists()) {
            avatarFile.delete();
        }
        Rect rect = new Rect();
        if (mDetectorData != null && mDetectorData.getFaceRectList() != null
                && mDetectorData.getFaceRectList().length > 0
                && mDetectorData.getFaceRectList()[0].right > 0) {
            rect = mDetectorData.getFaceRectList()[0];
        }
        ViseLog.i("save picture start!");
        Bitmap bitmap = getImage(data, rect, 150, 200, pictureFile, avatarFile);
        ViseLog.i("save picture complete!");
        gotoPayResult(avatarFile.getAbsolutePath());
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }
    private String getDiskCacheDir(Context context, String dirName) {
        String cachePath = "";
        if ((Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable())
                && context != null && context.getExternalCacheDir() != null) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            if (context != null && context.getCacheDir() != null) {
                cachePath = context.getCacheDir().getPath();
            }
        }
        return cachePath + File.separator + dirName;
    }
    private File getOutputFile(Context context, String dirName, String fileName) {
        File dirFile = new File(getDiskCacheDir(context, dirName));
        if (!dirFile.exists()) {
            if (!dirFile.mkdirs()) {
                ViseLog.d("failed to create directory");
                return null;
            }
        }
        File file = new File(dirFile.getPath() + File.separator + fileName);
        return file;
    }
    private Bitmap getImage(byte[] data, Rect rect, float ww, float hh, File pictureFile, File avatarFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        int w = options.outWidth;
        int h = options.outHeight;
        float scale = 1.0F;
        if (w < h) {
            scale = ww / (float) w;
            if (hh / (float) h > scale) {
                scale = hh / (float) h;
            }
        } else {
            scale = ww / (float) h;
            if (hh / (float) w > scale) {
                scale = hh / (float) w;
            }
        }
        Bitmap scaleBitmap = scaleImage(bitmap, (int) ((float) w * scale), (int) ((float) h * scale));
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        return compressImage(scaleBitmap, rect, 1536, pictureFile, avatarFile);
    }
    private Bitmap compressImage(Bitmap image, Rect rect, int size, File pictureFile, File avatarFile) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = image;
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > size) {
            baos.reset();
            options -= 5;
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(pictureFile);
            if (mFace_detector_preview != null) {
                if (mFace_detector_preview.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    bitmap = rotaingImageView(360 - mFace_detector_preview.getDisplayOrientation(), bitmap);
                } else {
                    bitmap = rotaingImageView(mFace_detector_preview.getDisplayOrientation(), bitmap);
                }
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, fileOutputStream);
        } catch (FileNotFoundException var18) {
            ViseLog.e("File not found: " + var18.getMessage());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException var16) {
                ViseLog.e("Error accessing file: " + var16.getMessage());
            }
        }
        float scale = (float) bitmap.getHeight() / DeviceUtils.getScreenHeight(this);
        if (rect.right > 0) {
            int top = (int) (rect.top * scale);
            int bottom = (int) (rect.bottom * scale);
            rect.left = 0;
            rect.right = bitmap.getWidth();
            rect.top = top - (bitmap.getWidth() - (bottom - top)) / 2;
            rect.bottom = bottom + (bitmap.getWidth() - (bottom - top)) / 2;
        } else {
            rect.left = 0;
            rect.right = bitmap.getWidth();
            rect.top = (bitmap.getHeight() - bitmap.getWidth()) / 2;
            rect.bottom = (bitmap.getHeight() - bitmap.getWidth()) / 2 + bitmap.getWidth();
        }
        Bitmap avatarBitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
        try {
            fileOutputStream = new FileOutputStream(avatarFile);
            avatarBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
        } catch (FileNotFoundException var18) {
            ViseLog.e("File not found: " + var18.getMessage());
        } finally {
            if (avatarBitmap != null && !avatarBitmap.isRecycled()) {
                avatarBitmap.recycle();
                avatarBitmap = null;
            }
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (IOException var16) {
                ViseLog.e("Error accessing file: " + var16.getMessage());
            }

        }

        if (baos != null) {
            try {
                baos.close();
            } catch (IOException var17) {
                var17.printStackTrace();
            }
        }

        if (image != null && !image.isRecycled()) {
            image.recycle();
            image = null;
        }

        return bitmap;
    }
    private Bitmap scaleImage(Bitmap bm, int newWidth, int newHeight) {
        if (bm == null) {
            return null;
        } else {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = (float) newWidth / (float) width;
            float scaleHeight = (float) newHeight / (float) height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
            if (bm != null & !bm.isRecycled()) {
                bm.recycle();
                bm = null;
            }

            return newbm;
        }
    }
    private Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) angle);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (resizedBitmap != bitmap && bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }

        return resizedBitmap;
    }

    private void gotoPayResult(String imagePath){
        if(faceType==0){
            EventBus.getDefault().post(new FaceType(faceType,imagePath));
        }else {
            Intent intent = new Intent();
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

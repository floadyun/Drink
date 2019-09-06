package com.iwinad.drink.activity;

import android.content.Intent;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.base.lib.baseui.AppBaseActivity;
import com.base.lib.http.ApiHelper;
import com.base.lib.http.base.BaseObserver;
import com.base.lib.util.FileUtil;
import com.base.lib.util.ImageUtil;
import com.iwinad.drink.Consts;
import com.iwinad.drink.R;
import com.iwinad.drink.api.ApiLoader;
import com.iwinad.drink.model.FaceInfoEntity;
import com.iwinad.drink.model.FaceType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppBaseActivity {

    private String imagePath;

    private Uri imgUri;

    private static final int TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);
    }
    /**
     * 跳转至人脸识别
     * @param view
     */
    public void gotoFaceRecognition(View view){
        Intent intent = new Intent(this,SelectDrinkActivity.class);
        intent.putExtra(Consts.FACE_TYPE,0);
        startActivity(intent);
    }
    /**
     * 拍照
     * @param view
     */
    public void captureImage(View view){
        imagePath = Environment.getExternalStorageDirectory()+"/upload/"+System.currentTimeMillis()+".png";
        FileUtil.createfile(imagePath);
        //储存拍照图片file
        File outputImage = new File(imagePath);

        if (outputImage.exists()){
            outputImage.delete();
        }
        try {
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //
        if (Build.VERSION.SDK_INT>=24){
            //7.0以上新增的方法 共享文件 FileProvider是一种特殊的内容提供者
            // 第二个参数为对应filepaths.xml中provider（内容提供者的）的name
            imgUri = FileProvider
                    .getUriForFile(this,"com.iwinad.drink.fileprovider",outputImage);
        }else {
            imgUri = Uri.fromFile(outputImage);
        }
        //启动相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imgUri);
        startActivityForResult(intent,TAKE_PHOTO);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO:
                    if(resultCode == RESULT_OK) {
                        uploadImage(imagePath);
                    }
                    break;
                default:
                    break;
            }
        }
    }
    private void uploadImage(String imagePath){
        ApiLoader.uploadImage(ImageUtil.imageToBase64(imagePath), new BaseObserver<FaceInfoEntity>(this) {
            @Override
            public void onSuccess(FaceInfoEntity value) {
                gotoSelectDrink(value);
            }
        });
    }
    private void gotoSelectDrink(FaceInfoEntity faceInfoEntity){
        Intent intent = new Intent(this,SelectDrinkActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("faceInfo",faceInfoEntity);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    @Subscribe
    public void onEventMainThread(FaceType faceType){
        if(faceType.type== FaceType.FACE_TYPE_1){
     //       uploadImage(faceType.imagePath);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

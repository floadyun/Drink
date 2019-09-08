package com.iwinad.drink.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.BinderThread;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.base.lib.baseui.AppBaseActivity;
import com.base.lib.http.base.BaseObserver;
import com.base.lib.util.ImageUtil;
import com.iwinad.drink.Consts;
import com.iwinad.drink.R;
import com.iwinad.drink.api.ApiLoader;
import com.iwinad.drink.model.FaceInfoEntity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/9/3
 * @description:刷脸支付
 */
public class FacePayActivity extends AppBaseActivity {

    @BindView(R.id.face_image_view)
    ImageView faceImage;
    @BindView(R.id.person_info_view)
    View infoView;
    @BindView(R.id.identity_status_view)
    View identityStatusView;
    @BindView(R.id.pay_status_view)
    View payStatusView;

    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_pay);
        ButterKnife.bind(this);
        //   uploadImage(getIntent().getStringExtra(Consts.FACE_IAMGE_PATH));

        faceImage.setImageResource(R.drawable.face_image);

        mHandler = new Handler();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(FacePayActivity.this,EndActivity.class);
                startActivity(intent);
                finishSelf();
            }
        },3000);
    }
    /**
     * 上传图片识别
     * @param imagePath
     */
    private void uploadImage(String imagePath){
        ApiLoader.uploadImage(ImageUtil.imageToBase64(imagePath), new BaseObserver<FaceInfoEntity>(this) {
            @Override
            public void onSuccess(FaceInfoEntity value) {
                isIdentifySuccess(value);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(FacePayActivity.this,EndActivity.class);
                        startActivity(intent);
                    }
                },3000);
            }
        });
    }
    /**
     * 判断是否存在人脸信息
     * @param faceInfoEntity
     * @return
     */
    private boolean isIdentifySuccess(FaceInfoEntity faceInfoEntity){
        for (FaceInfoEntity faceInfo:Consts.getFaceInfoEntities()){
            if(faceInfoEntity.姓名.equals(faceInfo.姓名)){
                infoView.setBackgroundResource(faceInfoEntity.faceImage);
                return true;
            }
        }
        return false;
    }

    /**
     * 识别失败
     */
    private void setIdentityFailure(){

    }
}

package com.iwinad.drink.activity;

import android.view.View;

import com.base.lib.baseui.AppBaseActivity;
import com.base.lib.http.base.BaseObserver;
import com.base.lib.util.ImageUtil;
import com.iwinad.drink.Consts;
import com.iwinad.drink.R;
import com.iwinad.drink.api.ApiLoader;
import com.iwinad.drink.model.FaceInfoEntity;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/9/3
 * @description:刷脸支付
 */
public class FacePayActivity extends AppBaseActivity {

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setContentView(R.layout.activity_face_pay_layout);

        uploadImage(getIntent().getStringExtra(Consts.FACE_IAMGE_PATH));
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
                return true;
            }
        }
        return false;
    }
}

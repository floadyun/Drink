package com.iwinad.drink;

import com.iwinad.drink.model.FaceInfoEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @copyright : 深圳市喜投金融服务有限公司
 * Created by yixf on 2019/9/5
 * @description:
 */
public class Consts {
    //姓名
    private static final String[] NAMES = new String[]{"果子博弈","Olia","洛小鱼","Andier","felix.zhou","Juan.Zhao"};
    //性别
    private static final String[] SEXS = new String[]{"女","女","女","女","男","女"};
    //部门
    private static final String PART = "华为云应用平台";
    //性格
    private static final String[] NATURES = new String[]{"沉着、冷静","温柔、体贴","乐观、开朗","善良、淡雅","沉着、睿智","端庄、稳重"};
    //兴趣
    private static final String[] INTERESTS = new String[]{"唐诗宋词、健身","瑜伽","流行、音乐","美食、旅行","IT技术钻研、交流","IT技术钻研、交流"};

    public static List<FaceInfoEntity> faceInfoEntities;

    public static List<FaceInfoEntity> getFaceInfoEntities(){
        if(faceInfoEntities==null){
            faceInfoEntities = new ArrayList<>();
            for (String name:NAMES){
                FaceInfoEntity faceInfoEntity = new FaceInfoEntity();
                faceInfoEntity.姓名 = name;
                faceInfoEntities.add(faceInfoEntity);
            }
        }
        return faceInfoEntities;
    }
}

package com.douyaim.effect;

/**
 * Created by hj on 16/9/5.
 */
public class ZZEffectCommon {
    //"extra"最大容量
    public static final int  ZZMaxCountOfShaderExtraArray = 128;
    //"skinningMatrix"最大容量
    public static final int  ZZMaxCountOfSkinningMatrixArray = 60;
    //一张脸总点数
    public static final int  ZZNumberOfFacePoints = 106;
    //变形点数
    public static final int  ZZNumberOfDeformPoints = 3;
    //人脸数
    public static final int  ZZNumberOfFace = 2;
    //人脸数+全屏
    public static final int ZZNumberOfFaceAndScreen = ZZNumberOfFace + 1;
    //变脸数
    public static final int  ZZNumberOfFaceForChange = 1;
    //双人特效
    public static final int ZZTwoFaceEffect = 2;

    public static final int ZZEffectElementNumber = 8;

    public static final int ZZEffectFaceIndexf1 = -1;
    public static final int ZZEffectFaceIndex0 = 0;
    public static final int ZZEffectFaceIndex1 = 1;
    public static final int ZZEffectFaceIndex2 = 2;
    public static final int ZZEffectFaceIndex3 = 3;

    public static final int ZZEffectFaceCount0 = 0;
    public static final int ZZEffectFaceCount1 = 1;
    public static final int ZZEffectFaceCount2 = 2;
    public static final int ZZEffectFaceCount3 = 3;
    public static final int ZZEffectFaceCount4 = 4;

    //纹理拼图类别
    public static final int TexCoorType_default = 0;//默认类别，表示正常拼图
    public static final int TexCoorType_col = 1;//列是正常的序列帧
    public static final int TexCoorType_row = 2;//行是正常的序列帧
    public static final int TexCoorType_no = 3;//行列都不是正常的序列帧

    //普通和
    public static final int ZZEffectElementCommonType = 0;//普通
    public static final int ZZEffectElementActionType = 1;//张嘴变胖

    public static final int FaceActionType_default = 0;//默认是瘦脸
    public static final int FaceActionType_FitFace = 1;//胖脸

    public static final int ZZColorConstant = 255;
    public static final int ZZFrameWidth = 750;
    public static final int ZZFrameHeight = 1334;
    public static final double PI = 3.1415926;
}


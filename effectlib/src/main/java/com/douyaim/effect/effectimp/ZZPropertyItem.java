package com.douyaim.effect.effectimp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by hj on 17/1/16.
 */

public class ZZPropertyItem implements Serializable {

    @SerializedName("isTwoFaceEffect")
    private boolean isTwoFaceEffect;
    @SerializedName("isRaiseWhenOnlyOneFace")
    private boolean isRaiseWhenOnlyOneFace;//近一张人脸时是否绘制
    private boolean isCircle;//是否循环回来，例如张嘴吐唇印
    @SerializedName("isGoneOnDis")
    private boolean isGoneOnDis;//指定范围是否消失
    @SerializedName("isGoneOffDis")
    private boolean isGoneOffDis;//超过范围是否消失
    @SerializedName("isUseOptimizeDistance")
    private boolean isUseOptimizeDistance;//是否使用优化距离算法
    private int openMouthType;//张嘴触发的类型
    @SerializedName("elementType")
    private int elementType;//元素类型
    private int mouthCount;//第几次张嘴触发
    @SerializedName("scale")
    private float scale;//指定触发距离
    private int[] indexs;

    public boolean isTwoFaceEffect() {
        return isTwoFaceEffect;
    }

    public void setTwoFaceEffect(boolean twoFaceEffect) {
        isTwoFaceEffect = twoFaceEffect;
    }

    public boolean isRaiseWhenOnlyOneFace() {
        return isRaiseWhenOnlyOneFace;
    }

    public void setRaiseWhenOnlyOneFace(boolean raiseWhenOnlyOneFace) {
        isRaiseWhenOnlyOneFace = raiseWhenOnlyOneFace;
    }

    public boolean isCircle() {
        return isCircle;
    }

    public void setCircle(boolean circle) {
        isCircle = circle;
    }

    public boolean isGoneOnDis() {
        return isGoneOnDis;
    }

    public void setGoneOnDis(boolean goneOnDis) {
        isGoneOnDis = goneOnDis;
    }

    public boolean isGoneOffDis() {
        return isGoneOffDis;
    }

    public void setGoneOffDis(boolean goneOffDis) {
        isGoneOffDis = goneOffDis;
    }

    public boolean isUseOptimizeDistance() {
        return isUseOptimizeDistance;
    }

    public void setUseOptimizeDistance(boolean useOptimizeDistance) {
        isUseOptimizeDistance = useOptimizeDistance;
    }

    public int getOpenMouthType() {
        return openMouthType;
    }

    public void setOpenMouthType(int openMouthType) {
        this.openMouthType = openMouthType;
    }

    public int getElementType() {
        return elementType;
    }

    public void setElementType(int elementType) {
        this.elementType = elementType;
    }

    public int getMouthCount() {
        return mouthCount;
    }

    public void setMouthCount(int mouthCount) {
        this.mouthCount = mouthCount;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public int[] getIndexs() {
        return indexs;
    }

    public void setIndexs(int[] indexs) {
        this.indexs = indexs;
    }
}

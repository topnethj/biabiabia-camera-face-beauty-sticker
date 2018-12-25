package com.biabiabia.effect.effectimp;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by hj on 17/3/20.
 */

public class ZZEffectAffectoritem implements Serializable {

    private static final long serialVersionUID = 3494941209616705595L;

    @SerializedName("affector_needReverse")
    private boolean needReverse;
    private int affectorType;
    @SerializedName("affector_startTime")
    private float startTime;
    @SerializedName("affector_endTime")
    private float endTime;
    @SerializedName("affector_totalTime")
    private float totalTime;
    @SerializedName("affector_loopTime")
    private float loopTime;
    @SerializedName("affector_startPosX")
    private float startPosX;
    @SerializedName("affector_startPosY")
    private float startPosY;
    @SerializedName("affector_endPosX")
    private float endPosX;
    @SerializedName("affector_endPosY")
    private float endPosY;
    @SerializedName("affector_startBindPoint")
    private String startBindPoint;
    @SerializedName("affector_endBindPoint")
    private String endBindPoint;
    @SerializedName("affector_startWidth")
    private float startWidth;
    @SerializedName("affector_startHeight")
    private float startHeight;
    @SerializedName("affector_endWidth")
    private float endWidth;
    @SerializedName("affector_endHeight")
    private float endHeight;
    @SerializedName("affector_startAlpha")
    private float startAlpha;
    @SerializedName("affector_endAlpha")
    private float endAlpha;
    @SerializedName("affector_startPitch")
    private float startPitch;
    @SerializedName("affector_startYaw")
    private float startYaw;
    @SerializedName("affector_startRoll")
    private float startRoll;
    @SerializedName("affector_endPitch")
    private float endPitch;
    @SerializedName("affector_endYaw")
    private float endYaw;
    @SerializedName("affector_endRoll")
    private float endRoll;
    @SerializedName("affector_frameTimes")
    private float[] frameTimes;
    @SerializedName("affector_frameNames")
    private String frameNames;

    //action
    @SerializedName("affector_action")
    private String affectorActions;
    @SerializedName("affector_times")
    private String affectorTimes;
    //@SerializedName("affector_frameNames")
    private String affectorFrameNames;

    public boolean isNeedReverse() {
        return needReverse;
    }

    public void setNeedReverse(boolean needReverse) {
        this.needReverse = needReverse;
    }

    public int getAffectorType() {
        return affectorType;
    }

    public void setAffectorType(int affectorType) {
        this.affectorType = affectorType;
    }

    public float getStartTime() {
        return startTime;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
    }

    public float getEndTime() {
        return endTime;
    }

    public void setEndTime(float endTime) {
        this.endTime = endTime;
    }

    public float getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(float totalTime) {
        this.totalTime = totalTime;
    }

    public float getLoopTime() {
        return loopTime;
    }

    public void setLoopTime(float loopTime) {
        this.loopTime = loopTime;
    }

    public float getStartPosX() {
        return startPosX;
    }

    public void setStartPosX(float startPosX) {
        this.startPosX = startPosX;
    }

    public float getStartPosY() {
        return startPosY;
    }

    public void setStartPosY(float startPosY) {
        this.startPosY = startPosY;
    }

    public float getEndPosX() {
        return endPosX;
    }

    public void setEndPosX(float endPosX) {
        this.endPosX = endPosX;
    }

    public float getEndPosY() {
        return endPosY;
    }

    public void setEndPosY(float endPosY) {
        this.endPosY = endPosY;
    }

    public String getStartBindPoint() {
        return startBindPoint;
    }

    public void setStartBindPoint(String startBindPoint) {
        this.startBindPoint = startBindPoint;
    }

    public String getEndBindPoint() {
        return endBindPoint;
    }

    public void setEndBindPoint(String endBindPoint) {
        this.endBindPoint = endBindPoint;
    }

    public float getStartWidth() {
        return startWidth;
    }

    public void setStartWidth(float startWidth) {
        this.startWidth = startWidth;
    }

    public float getStartHeight() {
        return startHeight;
    }

    public void setStartHeight(float startHeight) {
        this.startHeight = startHeight;
    }

    public float getEndWidth() {
        return endWidth;
    }

    public void setEndWidth(float endWidth) {
        this.endWidth = endWidth;
    }

    public float getEndHeight() {
        return endHeight;
    }

    public void setEndHeight(float endHeight) {
        this.endHeight = endHeight;
    }

    public float getStartAlpha() {
        return startAlpha;
    }

    public void setStartAlpha(float startAlpha) {
        this.startAlpha = startAlpha;
    }

    public float getEndAlpha() {
        return endAlpha;
    }

    public void setEndAlpha(float endAlpha) {
        this.endAlpha = endAlpha;
    }

    public float getStartPitch() {
        return startPitch;
    }

    public void setStartPitch(float startPitch) {
        this.startPitch = startPitch;
    }

    public float getStartYaw() {
        return startYaw;
    }

    public void setStartYaw(float startYaw) {
        this.startYaw = startYaw;
    }

    public float getStartRoll() {
        return startRoll;
    }

    public void setStartRoll(float startRoll) {
        this.startRoll = startRoll;
    }

    public float getEndPitch() {
        return endPitch;
    }

    public void setEndPitch(float endPitch) {
        this.endPitch = endPitch;
    }

    public float getEndYaw() {
        return endYaw;
    }

    public void setEndYaw(float endYaw) {
        this.endYaw = endYaw;
    }

    public float getEndRoll() {
        return endRoll;
    }

    public void setEndRoll(float endRoll) {
        this.endRoll = endRoll;
    }

    public float[] getFrameTimes() {
        return frameTimes;
    }

    public void setFrameTimes(float[] frameTimes) {
        this.frameTimes = frameTimes;
    }

    public String getFrameNames() {
        return frameNames;
    }

    public String getAffectorActions() {
        return affectorActions;
    }

    public void setAffectorActions(String affectorActions) {
        this.affectorActions = affectorActions;
    }

    public String getAffectorTimes() {
        return affectorTimes;
    }

    public void setAffectorTimes(String affectorTimes) {
        this.affectorTimes = affectorTimes;
    }

    public String getAffectorFrameNames() {
        //return affectorFrameNames;
        return frameNames;
    }

    public void setAffectorFrameNames(String affectorFrameNames) {
        this.affectorFrameNames = affectorFrameNames;
    }

    public void setFrameNames(String frameNames) {
        this.frameNames = frameNames;
    }

}

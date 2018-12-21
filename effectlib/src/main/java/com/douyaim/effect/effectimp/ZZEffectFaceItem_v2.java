package com.douyaim.effect.effectimp;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by hj on 16/9/22.
 */
public class ZZEffectFaceItem_v2 implements Serializable {

    private int start;
    private int end;
    private int action;
    private float duration;
    @SerializedName("vertex")
    private String vertexName;
    @SerializedName("fragment")
    private String fragmentName;
    private int count;
    @SerializedName("extra")
    private float[] extras;
    private String dirPath;
    private float[] framePoints;
    private float[] frameRotates;
    private float[] fillColor;
    private int frameCount;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getVertexName() {
        return vertexName;
    }

    public void setVertexName(String vertexName) {
        this.vertexName = vertexName;
    }

    public String getFragmentName() {
        return fragmentName;
    }

    public void setFragmentName(String fragmentName) {
        this.fragmentName = fragmentName;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float[] getExtras() {
        return extras;
    }

    public void setExtras(float[] extras) {
        this.extras = extras;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float[] getFramePoints() {
        return framePoints;
    }

    public void setFramePoints(float[] framePoints) {
        this.framePoints = framePoints;
    }

    public float[] getFrameRotates() {
        return frameRotates;
    }

    public void setFrameRotates(float[] frameRotates) {
        this.frameRotates = frameRotates;
    }

    public float[] getFillColor() {
        return fillColor;
    }

    public void setFillColor(float[] fillColor) {
        this.fillColor = fillColor;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }
}

package com.douyaim.effect.effectimp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by hj on 16/9/22.
 */
public class ZZEffectFaceItem_v2 implements Serializable {

    private int start;
    @SerializedName("vertex")
    private String vertexName;
    @SerializedName("fragment")
    private String fragmentName;
    private int count;
    @SerializedName("extra")
    private float[] extras;
    @SerializedName("note")
    private String note;
    private String dirPath;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}

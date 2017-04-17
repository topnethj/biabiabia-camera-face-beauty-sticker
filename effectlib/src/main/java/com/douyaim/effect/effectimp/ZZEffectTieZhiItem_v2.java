package com.douyaim.effect.effectimp;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hj on 16/11/29.
 */

public class ZZEffectTieZhiItem_v2 {
    @SerializedName("effectType")
    private int effectType;
    @SerializedName("blendType")
    private int blendType;
    @SerializedName("vertex")
    private String vertex;
    @SerializedName("fragment")
    private String fragment;
    private float duration;
    private float scale;
    private boolean isReStart;
    private String texCoorName;
    private String dirPath;

    public int getEffectType() {
        return effectType;
    }

    public void setEffectType(int effectType) {
        this.effectType = effectType;
    }

    public int getBlendType() {
        return blendType;
    }

    public void setBlendType(int blendType) {
        this.blendType = blendType;
    }

    public String getVertex() {
        return vertex;
    }

    public void setVertex(String vertex) {
        this.vertex = vertex;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isReStart() {
        return isReStart;
    }

    public void setReStart(boolean reStart) {
        isReStart = reStart;
    }

    public String getTexCoorName() {
        return texCoorName;
    }

    public void setTexCoorName(String texCoorName) {
        this.texCoorName = texCoorName;
    }

}

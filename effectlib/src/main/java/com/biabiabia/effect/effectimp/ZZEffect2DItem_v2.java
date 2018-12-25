package com.biabiabia.effect.effectimp;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

/**
 * Created by hj on 16/9/1.
 */
public class ZZEffect2DItem_v2 implements Serializable {

    private int start;
    private int end;
    private float duration;
    private int isAction;
    @SerializedName("vertex")
    private String vertexName;
    @SerializedName("fragment")
    private String fragmentName;
    @SerializedName("image")
    private String imageName;
    @SerializedName("extra")
    private float[] extras;
    @SerializedName("faceIndex")
    private int faceIndex;
    @SerializedName("note")
    private String note;
    private String dirPath;
    private int randomType;

    private int cameraType;
    private int[] faceIndexs;
    private int[] renderOrders;
    private String speechStr;
    private int strikeType;
    private String texCoorName;
    private float width;
    private float height;
    private float posx;
    private float posy;
    private float rollOffset;
    private int rollType;
    private String bindFaceIndex;
    @SerializedName("affector")
    private List<ZZEffectAffectoritem> affectorItems;
    private String alpha;
    private boolean isTimeNoRepeate;//动作触发时是否重置时间

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
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

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public float[] getExtras() {
        return extras;
    }

    public void setExtras(float[] extras) {
        this.extras = extras;
    }

    public int getFaceIndex() {
        return faceIndex;
    }

    public void setFaceIndex(int faceIndex) {
        this.faceIndex = faceIndex;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getIsAction() {
        return isAction;
    }

    public void setIsAction(int isAction) {
        this.isAction = isAction;
    }

    public int getRandomType() {
        return randomType;
    }

    public void setRandomType(int randomType) {
        this.randomType = randomType;
    }

    public int getCameraType() {
        return cameraType;
    }

    public void setCameraType(int cameraType) {
        this.cameraType = cameraType;
    }

    public int[] getFaceIndexs() {
        return faceIndexs;
    }

    public void setFaceIndexs(int[] faceIndexs) {
        this.faceIndexs = faceIndexs;
    }

    public int[] getRenderOrders() {
        return renderOrders;
    }

    public void setRenderOrders(int[] renderOrders) {
        this.renderOrders = renderOrders;
    }

    public String getSpeechStr() {
        return speechStr;
    }

    public void setSpeechStr(String speechStr) {
        this.speechStr = speechStr;
    }

    public int getStrikeType() {
        return strikeType;
    }

    public void setStrikeType(int strikeType) {
        this.strikeType = strikeType;
    }

    public String getTexCoorName() {
        return texCoorName;
    }

    public void setTexCoorName(String texCoorName) {
        this.texCoorName = texCoorName;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getPosx() {
        return posx;
    }

    public void setPosx(float posx) {
        this.posx = posx;
    }

    public float getPosy() {
        return posy;
    }

    public void setPosy(float posy) {
        this.posy = posy;
    }

    public float getRollOffset() {
        return rollOffset;
    }

    public void setRollOffset(float rollOffset) {
        this.rollOffset = rollOffset;
    }

    public int getRollType() {
        return rollType;
    }

    public void setRollType(int rollType) {
        this.rollType = rollType;
    }

    public String getBindFaceIndex() {
        return bindFaceIndex;
    }

    public void setBindFaceIndex(String bindFaceIndex) {
        this.bindFaceIndex = bindFaceIndex;
    }

    public List<ZZEffectAffectoritem> getAffectorItems() {
        return affectorItems;
    }

    public void setAffectorItems(List<ZZEffectAffectoritem> affectorItems) {
        this.affectorItems = affectorItems;
    }

    public String getAlpha() {
        return alpha;
    }

    public void setAlpha(String alpha) {
        this.alpha = alpha;
    }

    public boolean isTimeNoRepeate() {
        return isTimeNoRepeate;
    }

    public void setTimeNoRepeate(boolean timeNoRepeate) {
        isTimeNoRepeate = timeNoRepeate;
    }
}

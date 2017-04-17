package com.douyaim.effect.effectimp;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * Created by hj on 17/3/17.
 */

public class ZZEffectTexCoorItem implements Serializable {

    @SerializedName("texCoorName")
    private String elementName;
    private float speed;
    private int type;
    private boolean isReverse;
    private int repeatStartFrame;
    private int leftRightMirror;
    private int topBottomMirror;
    private int col;
    private int row;
    private float widthTexture;
    private float heightTexture;
    private float widthPic;
    private float heightPic;
    private float startX;
    private float startY;
    @SerializedName("image")
    private String imageName;
    private float[] framePos;
    private String dirPath;

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }

    public int getRepeatStartFrame() {
        return repeatStartFrame;
    }

    public void setRepeatStartFrame(int repeatStartFrame) {
        this.repeatStartFrame = repeatStartFrame;
    }

    public int getLeftRightMirror() {
        return leftRightMirror;
    }

    public void setLeftRightMirror(int leftRightMirror) {
        this.leftRightMirror = leftRightMirror;
    }

    public int getTopBottomMirror() {
        return topBottomMirror;
    }

    public void setTopBottomMirror(int topBottomMirror) {
        this.topBottomMirror = topBottomMirror;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public float getWidthTexture() {
        return widthTexture;
    }

    public void setWidthTexture(float widthTexture) {
        this.widthTexture = widthTexture;
    }

    public float getHeightTexture() {
        return heightTexture;
    }

    public void setHeightTexture(float heightTexture) {
        this.heightTexture = heightTexture;
    }

    public float getWidthPic() {
        return widthPic;
    }

    public void setWidthPic(float widthPic) {
        this.widthPic = widthPic;
    }

    public float getHeightPic() {
        return heightPic;
    }

    public void setHeightPic(float heightPic) {
        this.heightPic = heightPic;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public float[] getFramePos() {
        return framePos;
    }

    public void setFramePos(float[] framePos) {
        this.framePos = framePos;
    }

}

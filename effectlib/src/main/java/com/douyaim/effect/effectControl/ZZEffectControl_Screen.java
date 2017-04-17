package com.douyaim.effect.effectControl;

import com.douyaim.effect.ZZEffectCommon;
import com.douyaim.effect.effectimp.ZZEffectTexCoorItem;
import com.douyaim.effect.effectimp.ZZEffectTextureManager;
import com.douyaim.effect.model.Matrix3;
import com.douyaim.effect.model.Vector2;

/**
 * Created by hj on 17/3/22.
 */

public class ZZEffectControl_Screen extends ZZEffectControl {

    private static final float eps = 0.00001f;

    /**
     * 获取当前帧的行列值
     *
     * @param currentTime 当前时间
     * @param name        对应当前纹理信息的名称
     * @return 行列值
     */
    private Vector2 currentFrame(float currentTime, String name) {
        Vector2 result = new Vector2(-1f, -1f);
        if (name == null || "".equals(name.trim())) {
            return result;
        }

        ZZEffectTexCoorItem item = ZZEffectTextureManager.getZZEffectTextureManager().getTexCoorByName(name);
        if (item == null) {
            return result;
        }

        int noRepeatFrame = item.getRepeatStartFrame();//开始循环的帧数索引
        int frameCount = item.getFramePos().length / 2;

        //默认速度为12
        float speed = 1.0f / (item.getSpeed() < 0.002f ? 12.0f : item.getSpeed());
        float[] curFramePos = item.getFramePos();

        int currentFrame;
        if (noRepeatFrame * speed > currentTime) {
            currentFrame = (int) Math.floor(currentTime / speed);
            currentFrame = currentFrame % frameCount;
        } else {
            currentFrame = (int) Math.floor(currentTime / speed);
            currentFrame = (currentFrame % (frameCount - noRepeatFrame)) + noRepeatFrame;
        }

        result.one = curFramePos[currentFrame * 2];
        result.two = curFramePos[currentFrame * 2 + 1];

        return result;
    }

    /**
     * 根据配置指定的名称获取其对应的UV矩阵
     *
     * @param frameName 指定名称
     * @param frameTime 当前时间
     * @return 结果矩阵
     */
    public Matrix3 texMatrixWithItemName(String frameName, float frameTime) {
        Matrix3 resultMatrix = new Matrix3();
        if (frameName == null || "".equals(frameName.trim())) {
            return resultMatrix;
        }

        ZZEffectTexCoorItem item = ZZEffectTextureManager.getZZEffectTextureManager().getTexCoorByName(frameName);
        if (item == null) {
            return resultMatrix;
        }

        int type = item.getType();//是否规则拼图
        float leftRightMirror = item.getLeftRightMirror() == 0 ? 1 : item.getLeftRightMirror();//-1 表示左右对称，默认为1
        float topBottomMirror = item.getTopBottomMirror() == 0 ? 1 : item.getTopBottomMirror();//-1表示上下对称,默认为1

        float widthRatio = item.getWidthPic() / (item.getWidthTexture() + eps);//小图与大图的宽度比
        float heightRatio = item.getHeightPic() / (item.getHeightTexture() + eps);//小图与大图的高度比
        float startX = item.getStartX() / (item.getWidthTexture() + eps);//不规则拼图时的起点X
        float startY = item.getStartY() / (item.getHeightTexture() + eps);//不规则拼图时的起点Y

        if (type != ZZEffectCommon.TexCoorType_no) {//规则拼图
            Vector2 currentFrame = this.currentFrame(frameTime, frameName);
            resultMatrix = this.regularMatrix(currentFrame, widthRatio, heightRatio, leftRightMirror, topBottomMirror, startX, startY, type);
        } else {
            resultMatrix = this.notRegularMatrix(widthRatio, heightRatio, leftRightMirror, topBottomMirror, startX, startY);
        }

        return resultMatrix;
    }

    /**
     * 获取规则序列帧的UV矩阵
     *
     * @param currentFrame    当前帧
     * @param widthRatio      当前帧的宽度与大纹理宽度的比值
     * @param heightRatio     当前帧的高度与大纹理高度的比值
     * @param leftRightMirror 是否左右对称，1表示正常；-1表示左右镜像
     * @param topBottomMirror 是否上下对称，1表示正常；-1表示上下镜像
     * @param x               起点x
     * @param y               起点y
     * @param type            类别
     * @return 结果矩阵
     */
    private Matrix3 regularMatrix(Vector2 currentFrame, float widthRatio, float heightRatio, float leftRightMirror,
                                  float topBottomMirror, float x, float y, int type) {
        float targetSizeX = widthRatio;//1/col
        float targetSizeY = heightRatio;//1/row

        //缩放矩阵
        float[] m0 = {leftRightMirror * targetSizeX, 0, 0,
                0, topBottomMirror * targetSizeY, 0,
                0, 0, 1};
        Matrix3 scaleMatrix = new Matrix3(m0);

        float targetX = 0.0f;
        float targetY = 0.0f;

        switch (type) {
            case ZZEffectCommon.TexCoorType_default: {
                targetX = (currentFrame.one + (1.0f - leftRightMirror) * 0.5f) * targetSizeX;
                targetY = (currentFrame.two + (1.0f - topBottomMirror) * 0.5f) * targetSizeY;
            }
            break;

            case ZZEffectCommon.TexCoorType_col: {
                targetX = (currentFrame.one + (1.0f - leftRightMirror) * 0.5f) * targetSizeX;
                targetY = y;
            }
            break;

            case ZZEffectCommon.TexCoorType_row: {
                targetX = x;
                targetY = (currentFrame.two + (1.0f - topBottomMirror) * 0.5f) * targetSizeY;
            }
            break;

            default: {
                targetX = (currentFrame.one + (1.0f - leftRightMirror) * 0.5f) * targetSizeX;
                targetY = (currentFrame.two + (1.0f - topBottomMirror) * 0.5f) * targetSizeY;
            }
            break;
        }

        //平移到目标点的平移矩阵
        float[] m1 = {1, 0, targetX,
                0, 1, targetY,
                0, 0, 1};
        Matrix3 lastTranslate = new Matrix3(m1);

        return scaleMatrix.mul(lastTranslate);
    }

    /**
     * 行列都不规则时，计算UV的矩阵
     *
     * @param widthRatio      当前帧的宽度与大纹理宽度的比值
     * @param heightRatio     当前帧的高度与大纹理高度的比值
     * @param leftRightMirror 是否左右对称，1表示正常；-1表示左右镜像
     * @param topBottomMirror 是否上下对称，1表示正常；-1表示上下镜像
     * @param x               起点x
     * @param y               起点y
     * @return 结果矩阵
     */
    private Matrix3 notRegularMatrix(float widthRatio, float heightRatio, float leftRightMirror, float topBottomMirror, float x, float y) {
        //缩放矩阵
        float[] m0 = {leftRightMirror * widthRatio, 0, 0,
                0, topBottomMirror * heightRatio, 0,
                0, 0, 1};
        Matrix3 scaleMatrix = new Matrix3(m0);

        //平移矩阵
        float[] m1 = {1, 0, x,
                0, 1, y,
                0, 0, 1};
        Matrix3 lastTranslate = new Matrix3(m1);

        return scaleMatrix.mul(lastTranslate);
    }

}

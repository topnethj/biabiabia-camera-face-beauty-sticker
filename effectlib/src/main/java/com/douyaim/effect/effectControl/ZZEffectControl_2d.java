package com.douyaim.effect.effectControl;

import android.graphics.Point;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import com.douyaim.effect.ZZEffectCommon;
import com.douyaim.effect.effectControl.effectAffectors.ZZEffectAffector;
import com.douyaim.effect.effectControl.effectAffectors.ZZEffectAlphaAffector;
import com.douyaim.effect.effectControl.effectAffectors.ZZEffectFacePositionAffector;
import com.douyaim.effect.effectControl.effectAffectors.ZZEffectFrameChangeAffector;
import com.douyaim.effect.effectControl.effectAffectors.ZZEffectPositionAffector;
import com.douyaim.effect.effectControl.effectAffectors.ZZEffectRotateAffector;
import com.douyaim.effect.effectControl.effectAffectors.ZZEffectSizeScaleAffector;
import com.douyaim.effect.effectimp.ZZEffect2DItem_v2;
import com.douyaim.effect.effectimp.ZZEffectAffectoritem;
import com.douyaim.effect.effectimp.ZZEffectTexCoorItem;
import com.douyaim.effect.effectimp.ZZEffectTextureManager;
import com.douyaim.effect.effectimp.ZZEffectUtils;
import com.douyaim.effect.model.Matrix;
import com.douyaim.effect.model.Matrix3;
import com.douyaim.effect.model.Matrix4;
import com.douyaim.effect.model.Vector2;
import com.douyaim.effect.model.Vector4;
import com.douyaim.effect.utils.ScreenUtils;
import com.douyaim.effect.LibApp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hj on 17/3/20.
 */

public class ZZEffectControl_2d extends ZZEffectControl {

    public static final int eRollType_Default = 0;
    public static final int eRollType_WithFace = 1;
    public static final int eRollType_WithCam = 2;
    private static final float eps = 0.00001f;

    private Vector2 size = new Vector2();
    private Vector2 position = new Vector2();
    private float pitch;
    private float yaw;
    private float roll;
    private float alpha;
    private float screenRatio;
    private String frameName;
    private float frameTime;

    private PointF[] _facePoints;
    private ZZEffect2DItem_v2 _item;
    private List<ZZEffectAffector> v_EffectAffectors = new ArrayList<>();

    public void initWithItem(ZZEffect2DItem_v2 item) {
        _item = item;
        size.one = item.getWidth();
        size.two = item.getHeight();
        position.one = item.getPosx();
        position.two = item.getPosy();
        frameName = item.getTexCoorName();

        Point point = ScreenUtils.getScreenRealSize(LibApp.getAppContext());
        screenRatio = (float) point.x / (float) point.y;

        if (_item.getAffectorItems() != null && _item.getAffectorItems().size() > 0) {
            for (int i = 0; i < _item.getAffectorItems().size(); i++) {
                initEffectAffector(_item.getAffectorItems().get(i));
            }
        }
        if (_item.getAlpha() == null || "".equals(_item.getAlpha().trim())) {
            alpha = 1.0f;
        } else {
            alpha = Float.parseFloat(_item.getAlpha());
        }
    }

    public void initEffectAffector(ZZEffectAffectoritem item) {
        switch (item.getAffectorType()) {
            case ZZEffectAffector.eAffectorType_Position: {
                ZZEffectPositionAffector pAffector = new ZZEffectPositionAffector();
                pAffector.setM_totalTime(item.getTotalTime());
                pAffector.setM_startTime(item.getStartTime());
                pAffector.setM_endTime(item.getEndTime());
                pAffector.getM_vStartPos().one = item.getStartPosX();
                pAffector.getM_vStartPos().two = item.getStartPosY();
                pAffector.getM_vEndPos().one = item.getEndPosX();
                pAffector.getM_vEndPos().two = item.getEndPosY();
                pAffector.setM_bNeedReverse(item.isNeedReverse());
                pAffector.reset();
                v_EffectAffectors.add(pAffector);
            }
            break;
            case ZZEffectAffector.eAffectorType_SizeScale: {
                ZZEffectSizeScaleAffector pAffector = new ZZEffectSizeScaleAffector();
                pAffector.setM_totalTime(item.getTotalTime());
                pAffector.setM_startTime(item.getStartTime());
                pAffector.setM_endTime(item.getEndTime());
                pAffector.setM_loopTime(item.getLoopTime());
                pAffector.getM_vStartSize().one = item.getStartWidth();
                pAffector.getM_vStartSize().two = item.getStartHeight();
                pAffector.getM_vEndSize().one = item.getEndWidth();
                pAffector.getM_vEndSize().two = item.getEndHeight();
                pAffector.setM_bNeedReverse(item.isNeedReverse());
                pAffector.reset();
                v_EffectAffectors.add(pAffector);
            }
            break;
            case ZZEffectAffector.eAffectorType_SizeScaleWithFace: {
                ZZEffectSizeScaleAffector pAffector = new ZZEffectSizeScaleAffector(ZZEffectAffector.eAffectorType_SizeScaleWithFace);
                pAffector.setM_totalTime(item.getTotalTime());
                pAffector.setM_startTime(item.getStartTime());
                pAffector.setM_endTime(item.getEndTime());
                pAffector.getM_vStartSize().one = item.getStartWidth();
                pAffector.getM_vStartSize().two = item.getStartHeight();
                pAffector.getM_vEndSize().one = item.getEndWidth();
                pAffector.getM_vEndSize().two = item.getEndHeight();
                pAffector.setM_bNeedReverse(item.isNeedReverse());
                pAffector.reset();
                v_EffectAffectors.add(pAffector);
            }
            break;
            case ZZEffectAffector.eAffectorType_Alpha: {
                ZZEffectAlphaAffector pAffector = new ZZEffectAlphaAffector();
                pAffector.setM_totalTime(item.getTotalTime());
                pAffector.setM_startTime(item.getStartTime());
                pAffector.setM_endTime(item.getEndTime());
                pAffector.setM_StartAlpha(item.getStartAlpha());
                pAffector.setM_EndAlpha(item.getEndAlpha());
                pAffector.setM_bNeedReverse(item.isNeedReverse());
                pAffector.reset();
                v_EffectAffectors.add(pAffector);
            }
            break;
            case ZZEffectAffector.eAffectorType_Rotate: {
                ZZEffectRotateAffector pAffector = new ZZEffectRotateAffector();
                pAffector.setM_totalTime(item.getTotalTime());
                pAffector.setM_startTime(item.getStartTime());
                pAffector.setM_endTime(item.getEndTime());
                pAffector.getM_vStartRotate().one = item.getStartPitch();
                pAffector.getM_vStartRotate().two = item.getStartYaw();
                pAffector.getM_vStartRotate().three = item.getStartRoll();
                pAffector.getM_vEndRotate().one = item.getEndPitch();
                pAffector.getM_vEndRotate().two = item.getEndYaw();
                pAffector.getM_vEndRotate().three = item.getEndRoll();
                pAffector.setM_bNeedReverse(item.isNeedReverse());
                pAffector.reset();
                v_EffectAffectors.add(pAffector);
            }
            break;
            case ZZEffectAffector.eAffectorType_Frame: {
                ZZEffectFrameChangeAffector pAffector = new ZZEffectFrameChangeAffector();
                String[] names = item.getFrameNames().split(",");
                if (names.length == (item.getFrameTimes().length / 2.0)) {
                    for (int i = 0; i < names.length; i++) {
                        ZZEffectFrameChangeAffector.FrameInfo info = new ZZEffectFrameChangeAffector.FrameInfo();
                        info.framename = names[i];
                        int stIndex = i * 2;
                        int etIndex = i * 2 + 1;
                        info.starttime = item.getFrameTimes()[stIndex];
                        info.endtime = item.getFrameTimes()[etIndex];
                        pAffector.getM_vFrameInfos().add(info);
                    }
                    pAffector.setM_totalTime(item.getTotalTime());
                    pAffector.setM_startTime(item.getFrameTimes()[0]);
                    pAffector.reset();
                    v_EffectAffectors.add(pAffector);
                }
            }
            break;
            case ZZEffectAffector.eAffectorType_PositionWithFace: {
                ZZEffectFacePositionAffector pAffector = new ZZEffectFacePositionAffector();
                pAffector.setM_totalTime(item.getTotalTime());
                pAffector.setM_startTime(item.getStartTime());
                pAffector.setM_endTime(item.getEndTime());
                pAffector.getM_vStartPos().one = item.getStartPosX();
                pAffector.getM_vStartPos().two = item.getStartPosY();
                pAffector.getM_vEndPos().one = item.getEndPosX();
                pAffector.getM_vEndPos().two = item.getEndPosY();
                pAffector.getM_vStartOffset().one = item.getStartPosX();
                pAffector.getM_vStartOffset().two = item.getStartPosY();
                pAffector.getM_vEndOffset().one = item.getEndPosX();
                pAffector.getM_vEndOffset().two = item.getEndPosY();
                pAffector.setM_bNeedReverse(item.isNeedReverse());
                pAffector.setM_StartBindPoint(item.getStartBindPoint());
                pAffector.setM_EndBindPoint(item.getEndBindPoint());
                pAffector.reset();
                v_EffectAffectors.add(pAffector);
            }
            break;
            default:
                break;
        }
    }

    private float getDistance2DWithPoint(PointF p1, PointF p2) {
        return (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + ((p1.y - p2.y) / screenRatio) * ((p1.y - p2.y) / screenRatio));
    }

    private Vector2 getMidPointWithPoint(Vector2 p1, PointF p2) {
        return new Vector2((p1.one + p2.x) / 2.0f, (p1.two + p2.y) / 2.0f);
    }

    private Vector2 getRoateByPoint(Vector2 point, Vector2 roatePoint, float angleX, float angleY, float angleZ) {
        double[] m = {1.0, 0.0, 0.0, 0.0,
                0.0, 1.0, 0.0, 0.0,
                0.0, 0.0, 1.0, 0.0,
                -roatePoint.one, -roatePoint.two, -1.0, 1.0};
        Matrix4 firstransfromMat = new Matrix4(m);

        double[] m1 = {1.0, 0.0, 0.0, 0.0,
                0.0, screenRatio, 0.0, 0.0,
                0.0, 0.0, 1.0, 0.0,
                0.0, 0.0, 0.0, 1.0};
        Matrix4 sceneScalMat = new Matrix4(m1);

        double[] m2 = {Math.cos(angleZ), Math.sin(angleZ), 0.0, 0.0,
                -Math.sin(angleZ), Math.cos(angleZ), 0.0, 0.0,
                0.0, 0.0, 1.0, 0.0,
                0.0, 0.0, 0.0, 1.0};
        Matrix4 roateMatZ = new Matrix4(m2);

        double[] m3 = {1.0, 0.0, 0.0, 0.0,
                0.0, 1.0, 0.0, 0.0,
                0.0, 0.0, 1.0, 0.0,
                roatePoint.one, roatePoint.two, 1.0, 1.0};
        Matrix4 lastTransFormat = new Matrix4(m3);

        Matrix4 finalMat = lastTransFormat.multiply(sceneScalMat.multiply(roateMatZ.multiply(firstransfromMat)));

        Vector4 p = new Vector4();
        p.one = point.one;
        p.two = point.two;
        p.three = 1.0f;
        p.four = 1.0f;

        p = Matrix.multiply(finalMat, p);

        Vector2 ret = new Vector2();
        ret.one = p.one;
        ret.two = p.two;
        return ret;
    }

    private void getRoate(float facePitch, float faceYaw, float faceRoll) {
        switch (_item.getRollType()) {
            case eRollType_Default:
                pitch = 0.0f;
                yaw = 0.0f;
                roll = 0.0f;
                break;
            case eRollType_WithFace:
                pitch = facePitch;
                yaw = faceYaw;
                roll = faceRoll;
                break;
            case eRollType_WithCam:
                pitch = 0.0f;
                yaw = 0.0f;
                roll = (float) (Math.PI / 180.0f) * 0.0f;//TODO://
                break;
            default:
                pitch = 0.0f;
                yaw = 0.0f;
                roll = 0.0f;
                break;
        }
    }

    public void updateFaceResult(PointF[] facePoints, float time, float pitch, float yaw, float roll, boolean animating) {
        this._facePoints = facePoints;
        getRoate(pitch, yaw, roll);
        if (_item.getIsAction() > 0 && !animating) {
            size.one = _item.getWidth();
            size.two = _item.getHeight();
            position.one = _item.getPosx();
            position.two = _item.getPosy();
            frameName = _item.getTexCoorName();
        }

        if (_item.getBindFaceIndex() != null && !"".equals(_item.getBindFaceIndex().trim())) {
            //如果配置了人脸绑定点就重新计算元素大小和坐标
            if (facePoints != null) {
                Vector2 facePositon = getFacePosition(_item.getBindFaceIndex());
                float faceSize = getDistance2DWithPoint(_facePoints[0], _facePoints[32]);
                size.one = faceSize * _item.getWidth();
                size.two = faceSize * _item.getHeight();
                position.one = facePositon.one + _item.getPosx() * size.one;
                position.two = facePositon.two + _item.getPosy() * size.two;
                position = getRoateByPoint(position, facePositon, pitch, yaw, roll);
            }
        } else {
            //todo
        }

        this.frameTime = time;

        if (_item.getIsAction() == 1 || animating) {
            for (int i = 0; i < v_EffectAffectors.size(); i++) {
                if (v_EffectAffectors.get(i) != null) {
                    if (v_EffectAffectors.get(i).getM_type() == ZZEffectAffector.eAffectorType_PositionWithFace) {
                        ZZEffectFacePositionAffector pAffector = (ZZEffectFacePositionAffector) v_EffectAffectors.get(i);
                        if (_facePoints != null) {
                            String ssp = pAffector.getM_StartBindPoint();
                            if (ssp != null && !"".equals(ssp.trim())) {
                                Vector2 facePos = getFacePosition(ssp);
                                Vector2 pos = new Vector2();
                                float faceSize = getDistance2DWithPoint(_facePoints[0], _facePoints[32]);
                                pos.one = facePos.one + pAffector.getM_vStartOffset().one * faceSize;
                                pos.two = facePos.two + pAffector.getM_vStartOffset().two * faceSize;
                                pos = getRoateByPoint(pos, facePos, pitch, yaw, roll);
                                pAffector.getM_vStartPos().one = pos.one;
                                pAffector.getM_vStartPos().two = pos.two;
                            }
                            String sep = pAffector.getM_EndBindPoint();
                            if (sep != null && !"".equals(sep.trim())) {
                                Vector2 facePos = getFacePosition(sep);
                                Vector2 pos = new Vector2();
                                float faceSize = getDistance2DWithPoint(_facePoints[0], _facePoints[32]);
                                pos.one = facePos.one + pAffector.getM_vEndOffset().one * faceSize;
                                pos.two = facePos.two + pAffector.getM_vEndOffset().two * faceSize;
                                pos = getRoateByPoint(pos, facePos, pitch, yaw, roll);
                                pAffector.getM_vEndPos().one = pos.one;
                                pAffector.getM_vEndPos().two = pos.two;
                            }
                        }
                        pAffector.updateProperty();
                    }

                    updateAffector(v_EffectAffectors.get(i), time);
                }
            }
        }

        if (_item.getRollOffset() != 0.0f) {
            this.roll = this.roll + _item.getRollOffset();
        }
    }

    private Vector2 getFacePosition(@NonNull String faceIndex) {
        Vector2 facePositon = null;
        if (_facePoints != null) {
            String[] indexs = faceIndex.split(";");
            if (indexs.length > 0) {
                facePositon = new Vector2();
                Map<String, Integer> facePointIndexs = ZZEffectUtils.sharedFacePointIndexs();
                Integer curIndexI = facePointIndexs.get(indexs[0]);
                //TODO:资源文件配置有问题的时候,有可能找不到对应点,取nose1的值避免空指针
                if (curIndexI == null) {
                    curIndexI = facePointIndexs.get("nose1");
                }
                int curIndex = curIndexI.intValue();
                facePositon.one = _facePoints[curIndex].x;
                facePositon.two = _facePoints[curIndex].y;
                for (int i = 0; i < indexs.length; i++) {
                    int j = i + 1;
                    if (j >= indexs.length) {
                        break;
                    } else {
                        int nextIndex = facePointIndexs.get(indexs[j]).intValue();
                        facePositon = getMidPointWithPoint(facePositon, _facePoints[nextIndex]);
                    }
                }
            }
        }
        return facePositon;
    }

    private void updateAffector(ZZEffectAffector affector, float time) {
        if (affector.update(time)) {
            switch (affector.getM_type()) {
                case ZZEffectAffector.eAffectorType_Position: {
                    ZZEffectPositionAffector p = (ZZEffectPositionAffector) affector;
                    this.position.one = p.getM_vCurrentPos().one;
                    this.position.two = p.getM_vCurrentPos().two;
                }
                break;
                case ZZEffectAffector.eAffectorType_SizeScale: {
                    ZZEffectSizeScaleAffector p = (ZZEffectSizeScaleAffector) affector;
                    this.size.one = p.getM_vCurrentSize().one;
                    this.size.two = p.getM_vCurrentSize().two;
                }
                break;
                case ZZEffectAffector.eAffectorType_SizeScaleWithFace: {
                    if (_facePoints != null) {
                        ZZEffectSizeScaleAffector p = (ZZEffectSizeScaleAffector) affector;
                        float faceSize = getDistance2DWithPoint(_facePoints[0], _facePoints[32]);
                        this.size.one += p.getM_vCurrentSize().one * faceSize;
                        this.size.two += p.getM_vCurrentSize().two * faceSize;
                    }
                }
                break;
                case ZZEffectAffector.eAffectorType_Alpha: {
                    ZZEffectAlphaAffector p = (ZZEffectAlphaAffector) affector;
                    this.alpha = p.getM_CurrentAlpha();
                }
                break;
                case ZZEffectAffector.eAffectorType_Rotate: {
                    ZZEffectRotateAffector p = (ZZEffectRotateAffector) affector;
                    this.pitch = p.getM_vCurrentRotate().one;
                    this.yaw = p.getM_vCurrentRotate().two;
                    this.roll = p.getM_vCurrentRotate().three;
                }
                break;
                case ZZEffectAffector.eAffectorType_Frame: {
                    ZZEffectFrameChangeAffector p = (ZZEffectFrameChangeAffector) affector;
                    this.frameName = p.getM_CurrentFrame().framename;
                    this.frameTime = p.getM_effectTime();
                }
                break;
                case ZZEffectAffector.eAffectorType_PositionWithFace: {
                    ZZEffectFacePositionAffector p = (ZZEffectFacePositionAffector) affector;
                    this.position.one = p.getM_vCurrentPos().one;
                    this.position.two = p.getM_vCurrentPos().two;
                }
                break;
                default:
                    break;
            }
        }
    }

    public Matrix4 getTransformMatrix() {
        double[] m0 = {1.0, 0.0, 0.0, 0.0,
                0.0, screenRatio, 0.0, 0.0,
                0.0, 0.0, 1.0, 0.0,
                0.0, 0.0, 0.0, 1.0};
        Matrix4 sceneScalMat = new Matrix4(m0);

        //图片宽高比
        double[] m1 = {size.one, 0.0, 0.0, 0.0,
                0.0, size.two, 0.0, 0.0,
                0.0, 0.0, 1.0, 0.0,
                0.0, 0.0, 0.0, 1.0};
        Matrix4 sizeScaleMat = new Matrix4(m1);

        //旋转矩阵
        double[] m2 = {1.0, 0.0, 0.0, 0.0,
                0.0, Math.cos(pitch), -Math.sin(pitch), 0.0,
                0.0, Math.sin(pitch), Math.cos(pitch), 0.0,
                0.0, 0.0, 0.0, 1.0};
        Matrix4 roateMatX = new Matrix4(m2);

        double[] m3 = {Math.cos(yaw), 0.0, Math.sin(yaw), 0.0,
                0.0, 1.0, 0.0, 0.0,
                -Math.sin(yaw), 0.0, Math.cos(yaw), 0.0,
                0.0, 0.0, 0.0, 1.0};
        Matrix4 roateMatY = new Matrix4(m3);

        double[] m4 = {Math.cos(roll), -Math.sin(roll), 0.0, 0.0,
                Math.sin(roll), Math.cos(roll), 0.0, 0.0,
                0.0, 0.0, 1.0, 0.0,
                0.0, 0.0, 0.0, 1.0};
        Matrix4 roateMatZ = new Matrix4(m4);

        Matrix4 roateMatrix = roateMatX.multiply(roateMatY.multiply(roateMatZ));

        //平移到点的平移矩阵
        double[] m5 = {1.0, 0.0, 0.0, position.one,
                0.0, 1.0, 0.0, position.two,
                0.0, 0.0, 1.0, 1.0,
                0.0, 0.0, 0.0, 1.0};
        Matrix4 transfromMatrix = new Matrix4(m5);

        return sizeScaleMat.multiply(roateMatrix.multiply(sceneScalMat.multiply(transfromMatrix)));
    }

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
            if(item.isReverse()) {
                int curFrameCount = frameCount * 2 - noRepeatFrame - 1;
                currentFrame = (int)Math.floor((currentTime - noRepeatFrame * speed) / speed);
                currentFrame = currentFrame % (curFrameCount - noRepeatFrame) + noRepeatFrame;
                int finalFrame = frameCount - 1;
                if(currentFrame > finalFrame) {
                    int dis = currentFrame - finalFrame;
                    currentFrame = finalFrame - dis;
                }
            } else {
                currentFrame = (int)Math.floor((currentTime - noRepeatFrame * speed) / speed);
                currentFrame = currentFrame % (frameCount - noRepeatFrame) + noRepeatFrame;
            }
        }

        result.one = curFramePos[currentFrame * 2];
        result.two = curFramePos[currentFrame * 2 + 1];

        return result;
    }

    /**
     * 根据配置指定的名称获取其对应的UV矩阵
     *
     * @return 结果矩阵
     */
    public Matrix3 texMatrixWithItemName() {
        Matrix3 resultMatrix = new Matrix3();

        if (this.frameName == null) {
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
        float[] m2 = {1, 0, targetX,
                0, 1, targetY,
                0, 0, 1};
        Matrix3 lastTranslate = new Matrix3(m2);

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

    public String getFrameName() {
        return frameName;
    }

    public float getFrameTime() {
        return frameTime;
    }

    public ZZEffect2DItem_v2 get_item() {
        return _item;
    }

    public float getAlpha() {
        return alpha;
    }

}

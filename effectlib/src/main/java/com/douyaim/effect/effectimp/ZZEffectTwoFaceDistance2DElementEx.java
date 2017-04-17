package com.douyaim.effect.effectimp;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.support.annotation.NonNull;
import com.douyaim.effect.face.ZZFaceResult;
import com.douyaim.effect.utils.UniformUtil2;
import java.nio.FloatBuffer;
import java.util.List;

/**
 * Created by hj on 17/2/10.
 */

public class ZZEffectTwoFaceDistance2DElementEx extends ZZEffectTwoFace2DElement {
    private boolean _distanceStrike;//距离触发
    private int _state;
    private int _actionStatusUniform;
    private final float SCALE_Z = 0.35f;

    public void initWithItem(@NonNull ZZEffect2DItem_v2 curItem, FloatBuffer verticesBuffer, FloatBuffer textureCoordinatesBuffer) {
        super.initWithItem(curItem, verticesBuffer, textureCoordinatesBuffer);
        renew();
    }

    public void reset() {
        super.reset();
    }

    protected void renew() {
        super.renew();
        _distanceStrike = false;
        _state = 0;
        _animating = true;
    }

    public void generateProgram() {
        super.generateProgram();
        _actionStatusUniform = GLES20.glGetUniformLocation(_program, "ActionStatus");
    }

    public void updateWithFaceResult(List<ZZFaceResult> faceResult) {
        super.updateFacePoints(faceResult);
        float time = 0.0f;
        double now = System.currentTimeMillis();
        if (_faceCount >= 2) {
            //检测是否距离触发
            time = checkItemStartWithTwoFaces(now, time, faceResult);
        } else {
            if (_faceCount == 1) {
                if (_state != 0) {

                    if (_state == 1) {
                        _state = 2;
                        _startTs = now;
                    }
                    time = (float) (now - _startTs) / 1000f;
                }
            } else {
                renew();
            }
        }

        if (faceResult.size() >= 2) {
            _firstFaceStatus = faceResult.get(0).getFaceStatus();
            _secFaceStatus = faceResult.get(1).getFaceStatus();
        } else if (faceResult.size() == 1) {
            _firstFaceStatus = faceResult.get(0).getFaceStatus();
            _secFaceStatus = ZZFaceResult.ZZ_FACESTATUS_UNKNOWN;
        }

        //if (!_animating) {
        //    time = 0;
        //}
        _time = time;
    }

    public void render() {
        GLES20.glUseProgram(_program);
        UniformUtil2.setInteger(_actionStatusUniform, _state);
        super.render();
    }

    public void updateWithNoFaceResult() {
        super.updateWithNoFaceResult();
        renew();
    }

    private float checkItemStartWithTwoFaces(double now, float currentTime, List<ZZFaceResult> faceResult) {
        int itemStart = _item.getStart();
        float time = currentTime;
        switch (itemStart) {
            case ZZFaceResult.ZZTwoFaceStatusFirstFaceToSecFaceDistanceRaiseEx:
                if (isRaiseSecEffectWithDistance(faceResult)) {//触发
                    if (_startTs == 0.0f || _state == 2) {
                        _startTs = now;
                    }
                    _distanceStrike = true;
                    _state = 1;
                } else if (_distanceStrike) {
                    _distanceStrike = false;
                    if (_state == 1) {
                        _state = 2;
                        _startTs = now;
                    }
                }

                time = (float) (now - _startTs) / 1000f;
                break;
        }
        return time;
    }

    //判断距离是否小于指定的距离，小于指定距离触发第二张人脸特效
    protected boolean isRaiseSecEffectWithDistance(List<ZZFaceResult> faceResult) {
        if (faceResult.size() < 2) {//只有一张人脸不触发第二张人脸的特效
            return false;
        }
        float point0ToPoint32First = distanceBetweenPoints(faceResult.get(0).getPoints()[centerIndex1], faceResult.get(0).getPoints()[centerIndex2]);
        float point0ToPoint32Sec = distanceBetweenPoints(faceResult.get(1).getPoints()[centerIndex1], faceResult.get(1).getPoints()[centerIndex2]);
        float scale = point0ToPoint32First / point0ToPoint32Sec;

        //if (scale < (1.0 - SCALE_Z) || scale > (1.0 + SCALE_Z)) {
        //return false;
        //}

        float faceToFaceDistance;
        //计算两张人脸之间的距离
        if (isFirstFaceOnLeft(faceResult, centerIndex)) {
            faceToFaceDistance = distanceBetweenPoints(faceResult.get(0).getPoints()[centerIndex2], faceResult.get(1).getPoints()[centerIndex1]);
        } else {
            faceToFaceDistance = distanceBetweenPoints(faceResult.get(1).getPoints()[centerIndex2], faceResult.get(0).getPoints()[centerIndex1]);
        }

        float normalDis = _item.propertyItem.getScale() * 0.5f * (point0ToPoint32First + point0ToPoint32Sec);
        if (faceToFaceDistance < normalDis) {
            return true;
        }
        return false;
    }

    float setAniFlag(double now, float currentTime, List<ZZFaceResult> faceResult) {
        float time = currentTime;
        if (_animating) { // 正在动画中
            if (_startTs == 0.0) {
                _startTs = now;
            }
            time = (float) (now - _startTs) / 1000f;
            if (_item.getDuration() > 0.0f) { // 有动画时长限制
                if (time > _item.getDuration()) { // 超时
                    _animating = false;
                } else { // 没有超时
                    _animating = true;
                }
            } else { // 没有动画时长限制
                _animating = true;
            }
        } else { // 没有在动画中
            if (_startTs == 0.0f) {
                _startTs = now;
            }
            time = (float) (now - _startTs) / 1000f;
            if (_item.getDuration() > 0.0f) { // 有动画时长限制
                if (time > _item.getDuration()) { // 超时
                    _animating = false;
                } else { // 没有超时
                    _animating = true;
                }
            } else { // 没有动画时长限制
                _animating = true;
            }

        }
        if (faceResult.size() >= 2) {
            _firstFaceStatus = faceResult.get(0).getFaceStatus();
            _secFaceStatus = faceResult.get(1).getFaceStatus();
        } else if (faceResult.size() == 1) {
            _firstFaceStatus = faceResult.get(0).getFaceStatus();
            _secFaceStatus = ZZFaceResult.ZZ_FACESTATUS_UNKNOWN;
        }
        return time;
    }

    //判断第一张脸是否位于左边
    private boolean isFirstFaceOnLeft(List<ZZFaceResult> faceResult, int centerIndex) {
        return faceResult.get(0).getPoints()[centerIndex].x < faceResult.get(1).getPoints()[centerIndex].x;
    }

    private float distanceBetweenPoints(PointF p1, PointF p2) {
        float dis = (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
        return (float) Math.sqrt(dis);
    }

}

package com.douyaim.effect.effectimp;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.support.annotation.NonNull;

import com.douyaim.effect.ZZEffectCommon;
import com.douyaim.effect.face.ZZFaceManager_v2;
import com.douyaim.effect.face.ZZFaceResult;
import com.douyaim.effect.utils.UniformUtil2;

import java.nio.FloatBuffer;
import java.util.List;

/**
 * Created by hj on 17/1/16.
 */

public class ZZEffectTwoFace2DElement extends ZZEffect2DElement_v2 {
    private int _firstFaceStatusUniform, _secFaceStatusUniform, _twoFacePointsUniform,_rollsUniform,_faceCountUniform,_faceIndexUniform,_twoFaceRandomUniform;
    private PointF[] _twoFacePoints;
    int _firstFaceStatus = ZZFaceResult.ZZ_FACESTATUS_UNKNOWN;
    int _secFaceStatus = ZZFaceResult.ZZ_FACESTATUS_UNKNOWN;
    private float[] _rolls;
    int _faceCount;
    private boolean _isSecFace;
    private boolean _isOneFace;

    protected final int centerIndex = 45;
    protected final int centerIndex1 = 0;
    protected final int centerIndex2 = 32;
    private final float SCALE_Z = 0.25f;
    private final int mouthIndex2 = 93;

    public void reset() {
        renew();
        _twoFacePoints = null;
        super.reset();
    }

    protected void renew() {
        _time = 0;
        _animating = false;
        _startTs = 0.0;
        _firstFaceStatus = ZZFaceResult.ZZ_FACESTATUS_UNKNOWN;
        _secFaceStatus = ZZFaceResult.ZZ_FACESTATUS_UNKNOWN;
        _faceCount = 0;
        _isSecFace = false;
        _isOneFace = false;
        //_twoFacePoints = ZZEffect2DEngine_v2.defaultPointsForTwo;
    }

    public void initWithItem(@NonNull ZZEffect2DItem_v2 curItem, FloatBuffer verticesBuffer, FloatBuffer textureCoordinatesBuffer) {
        super.initWithItem(curItem, verticesBuffer, textureCoordinatesBuffer);
        _twoFacePoints = ZZEffect2DEngine_v2.defaultPointsForTwo;
        _rolls = new float[ZZEffectCommon.ZZTwoFaceEffect];
    }

    public void updateWithFaceResult(@NonNull List<ZZFaceResult> faceResult) {
        updateFacePoints(faceResult);
        float time = 0.0f;
        double now = System.currentTimeMillis();
        if (_faceCount >= 2) {//第二张人脸触发
            int itemStart = _item.getStart();
            if(itemStart == ZZFaceResult.ZZTwoFaceStatusSecFaceRaise
                    ||(itemStart & faceResult.get(0).getFaceStatus()) != 0
                    ||(itemStart & faceResult.get(1).getFaceStatus()) != 0){//第二张人脸出现
                time = setAniFlag(now, time, faceResult);
            }else if(_item.getStart() == ZZFaceResult.ZZTwoFaceStatusSecFaceGone){
                renew();
                if (!_isSecFace) {
                    _isSecFace = true;
                }
            }
        }else{//只检测到一张人脸或者没有检测到人脸
            if(_faceCount == 1){//第二张人脸消失触发
                int itemStart = _item.getStart();
                if (_isSecFace && itemStart == ZZFaceResult.ZZTwoFaceStatusSecFaceGone) {
                    _isOneFace = true;
                    time = setAniFlag(now, time, faceResult);
                }else if((itemStart & faceResult.get(0).getFaceStatus()) != 0){
                    time = setAniFlag(now, time, faceResult);
                }
            }else{
                renew();
                if (_faceCount == 0) {
                    _firstFaceStatus = ZZFaceResult.ZZ_FACESTATUS_UNKNOWN;
                    _secFaceStatus = ZZFaceResult.ZZ_FACESTATUS_UNKNOWN;
                }else{
                    _firstFaceStatus = faceResult.get(0).getFaceStatus();
                    _secFaceStatus = ZZFaceResult.ZZ_FACESTATUS_UNKNOWN;
                }
            }
        }
        if (!_animating) {
            time = 0.0f;
        }
        _time = time;
    }

    void updateFacePoints(List<ZZFaceResult> faceResult) {
        _faceCount = faceResult.size();
        //更新两张人脸点
        for (int i = 0; i < ZZEffectCommon.ZZTwoFaceEffect; i++) {
            if (i < _faceCount && faceResult.get(i).getFaceStatus() != ZZFaceResult.ZZ_FACESTATUS_UNKNOWN) {
                ZZFaceResult current = faceResult.get(i);
                PointF[] points = current.getPoints();
                for (int j = 0; j < ZZEffectCommon.ZZNumberOfFacePoints; j++) {
                    _twoFacePoints[i * ZZEffectCommon.ZZNumberOfFacePoints + j].x = points[j].x;
                    _twoFacePoints[i * ZZEffectCommon.ZZNumberOfFacePoints + j].y = points[j].y;
                }
                _rolls[i] = -current.getRoll();
            }else{
                for (int j = 0; j < ZZEffectCommon.ZZNumberOfFacePoints; j++) {
                    _twoFacePoints[i * ZZEffectCommon.ZZNumberOfFacePoints + j].x = -2.0f;
                    _twoFacePoints[i * ZZEffectCommon.ZZNumberOfFacePoints + j].y = -2.0f;
                }
                _rolls[i] = 0.0f;
            }
        }
    }

    float setAniFlag(double now, float currentTime, List<ZZFaceResult> faceResult) {
        float time = currentTime;
        if (_animating) { // 正在动画中
            if (_startTs == 0.0) {
                _startTs = now;
            }
            time = (float)(now - _startTs)/1000f;
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
            if (_startTs == 0.0) {
                _startTs = now;
            }
            time = (float)(now - _startTs)/1000f;
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

        if (_faceCount >= 2) {
            _firstFaceStatus = faceResult.get(0).getFaceStatus();
            _secFaceStatus = faceResult.get(1).getFaceStatus();
        } else {
            _firstFaceStatus = faceResult.get(0).getFaceStatus();
            _secFaceStatus = ZZFaceResult.ZZ_FACESTATUS_UNKNOWN;
        }

        return time;
    }

    public void updateWithNoFaceResult() {
        super.updateWithNoFaceResult();
        renew();
        for (int i=0; i< ZZEffectCommon.ZZNumberOfFacePoints * ZZEffectCommon.ZZTwoFaceEffect; i++) {
            _twoFacePoints[i].x = -2.0f;
            _twoFacePoints[i].y = -2.0f;
        }
        for (int i = 0; i < ZZEffectCommon.ZZTwoFaceEffect; i++) {
            _rolls[i] = 0.0f;
        }
    }

    public void render() {
        if (!_animating && _item.getIsAction() == 0) {
            return;
        }
        GLES20.glUseProgram(_program);
        // 人脸姿态
        UniformUtil2.setInteger(_firstFaceStatusUniform, _firstFaceStatus);
        UniformUtil2.setInteger(_secFaceStatusUniform, _secFaceStatus);
        // 人脸关键点
        UniformUtil2.setPoints(_twoFacePointsUniform, _twoFacePoints);

        UniformUtil2.setFloatArray(_rollsUniform, _rolls);

        int random = ZZFaceManager_v2.getZZFaceManager().getRandom();
        if (random != -1) {
            UniformUtil2.setInteger(_twoFaceRandomUniform, random);
        }

        UniformUtil2.setInteger(_faceCountUniform, _faceCount);
        UniformUtil2.setInteger(_faceIndexUniform, _item.getFaceIndex());
        super.render();
    }

    //判断距离是否小于指定的距离，小于指定距离触发第二张人脸特效
    protected boolean isRaiseSecEffectWithDistanceBetter(List<ZZFaceResult> faceResult) {
        if (faceResult.size() < 2) {//只有一张人脸不触发第二张人脸的特效
            return false;
        }
        float point0ToPoint32First = distanceBetweenPoints(faceResult.get(0).getPoints()[centerIndex1], faceResult.get(0).getPoints()[centerIndex2]);
        float point0ToPoint32Sec = distanceBetweenPoints(faceResult.get(1).getPoints()[centerIndex1], faceResult.get(1).getPoints()[centerIndex2]);
        float scale = point0ToPoint32First/point0ToPoint32Sec;
        if (scale < (1.0f - SCALE_Z) || scale > (1.0f + SCALE_Z)) {
            return false;
        }
        //计算两张人脸之间的距离
        float faceToFaceDistance = distanceBetweenPoints(faceResult.get(0).getPoints()[mouthIndex2], faceResult.get(1).getPoints()[mouthIndex2]);

        if (_item.propertyItem.getIndexs() != null && _item.propertyItem.getIndexs().length == 2) {
            int firIndex = _item.propertyItem.getIndexs()[0];
            int secIndex = _item.propertyItem.getIndexs()[1];
            PointF p1 = new PointF();
            PointF p2 = new PointF();
            p1.x = (faceResult.get(0).getPoints()[firIndex].x + faceResult.get(0).getPoints()[secIndex].x) * 0.5f;
            p1.y = (faceResult.get(0).getPoints()[firIndex].y + faceResult.get(0).getPoints()[secIndex].y) * 0.5f;
            p2.x = (faceResult.get(1).getPoints()[firIndex].x + faceResult.get(1).getPoints()[secIndex].x) * 0.5f;
            p2.y = (faceResult.get(1).getPoints()[firIndex].y + faceResult.get(1).getPoints()[secIndex].y) * 0.5f;
            faceToFaceDistance = distanceBetweenPoints(p1, p2);
        }

        float topToBottom1 = distanceBetweenPoints(faceResult.get(0).getPoints()[16], faceResult.get(0).getPoints()[43]);
        float topToBottom2 = distanceBetweenPoints(faceResult.get(1).getPoints()[16], faceResult.get(1).getPoints()[43]);
        float dis = (topToBottom1 + topToBottom2 + point0ToPoint32First + point0ToPoint32Sec) * 0.5f;
        float normalDis = _item.propertyItem.getScale() * dis;

        if (faceToFaceDistance < normalDis) {
            return true;
        }
        return false;
    }

    //判断距离是否小于指定的距离，小于指定距离触发第二张人脸特效
    protected boolean isRaiseSecEffectWithDistance(List<ZZFaceResult> faceResult) {
        if (faceResult.size() < 2) {//只有一张人脸不触发第二张人脸的特效
            return false;
        }
        float point0ToPoint32First = distanceBetweenPoints(faceResult.get(0).getPoints()[centerIndex1], faceResult.get(0).getPoints()[centerIndex2]);
        float point0ToPoint32Sec = distanceBetweenPoints(faceResult.get(1).getPoints()[centerIndex1], faceResult.get(1).getPoints()[centerIndex2]);
        float scale = point0ToPoint32First/point0ToPoint32Sec;
        if (scale < (1.0f - SCALE_Z) || scale > (1.0f + SCALE_Z)) {
            return false;
        }

        float faceToFaceDistance;
        //计算两张人脸之间的距离
        if (isFirstFaceOnLeft(faceResult, centerIndex)) {
            faceToFaceDistance = distanceBetweenPoints(faceResult.get(0).getPoints()[centerIndex2], faceResult.get(1).getPoints()[centerIndex1]);
        }else{
            faceToFaceDistance = distanceBetweenPoints(faceResult.get(1).getPoints()[centerIndex2], faceResult.get(0).getPoints()[centerIndex1]);
        }

        float normalDis = _item.propertyItem.getScale() * 0.5f * (point0ToPoint32First + point0ToPoint32Sec);
        if (faceToFaceDistance < normalDis) {
            return true;
        }
        return false;
    }

    public void generateProgram() {
        super.generateProgram();
        _firstFaceStatusUniform = GLES20.glGetUniformLocation(_program, "firstFaceStatus");
        _secFaceStatusUniform = GLES20.glGetUniformLocation(_program, "secFaceStatus");
        _twoFacePointsUniform = GLES20.glGetUniformLocation(_program, "twoFacePoints");
        _rollsUniform = GLES20.glGetUniformLocation(_program, "rolls");
        _faceCountUniform = GLES20.glGetUniformLocation(_program, "faceCount");
        _faceIndexUniform = GLES20.glGetUniformLocation(_program, "faceIndex");
        _twoFaceRandomUniform = GLES20.glGetUniformLocation(_program, "twoFaceRandom");
    }

    //判断第一张脸是否位于左边
    private boolean isFirstFaceOnLeft(List<ZZFaceResult> faceResult, int centerIndex) {
        return faceResult.get(0).getPoints()[centerIndex].x < faceResult.get(1).getPoints()[centerIndex].x;
    }

    private float distanceBetweenPoints(PointF p1, PointF p2) {
        float dis = (p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y);
        return (float)Math.sqrt(dis);
    }

}

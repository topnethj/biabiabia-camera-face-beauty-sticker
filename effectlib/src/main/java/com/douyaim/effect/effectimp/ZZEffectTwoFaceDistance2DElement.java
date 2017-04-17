package com.douyaim.effect.effectimp;

import android.support.annotation.NonNull;

import com.douyaim.effect.face.ZZFaceResult;

import java.nio.FloatBuffer;
import java.util.List;

/**
 * Created by hj on 17/1/18.
 */

public class ZZEffectTwoFaceDistance2DElement extends ZZEffectTwoFace2DElement {
    private boolean _distanceStrike;//距离触发

    public void initWithItem(@NonNull ZZEffect2DItem_v2 curItem, FloatBuffer verticesBuffer, FloatBuffer textureCoordinatesBuffer) {
        super.initWithItem(curItem, verticesBuffer, textureCoordinatesBuffer);
        renew();
    }

    public void reset() {
        super.reset();
        //renew();
    }

    protected void renew() {
        super.renew();
        _distanceStrike = false;
    }

    public void updateWithFaceResult(List<ZZFaceResult> faceResult) {
        updateFacePoints(faceResult);
        float time = 0.0f;
        double now = System.currentTimeMillis();
        if (_faceCount >= 2) {
            //检测是否距离触发
            time = checkItemStart(now, time, faceResult);
            //检测是否距离结束
            time = checkItemEnd(now, time, faceResult);
        } else {
            boolean curIsRaiseWhenOnlyOneFace = _item.propertyItem.isRaiseWhenOnlyOneFace();
            if (_faceCount == 1 && curIsRaiseWhenOnlyOneFace) {
                time = checkItemWithOneFace(now, time, faceResult);
            } else {
                renew();
            }
        }
        if (!_animating && _item.getIsAction() == 0) {
            time = 0f;
        }
        _time = time;
    }

    public void render() {
        if (!_animating && _item.getIsAction() == 0) {
            return;
        }
        super.render();
    }

    public void updateWithNoFaceResult() {
        super.updateWithNoFaceResult();
        renew();
    }

    private float checkItemWithOneFace(double now, float currentTime, List<ZZFaceResult> faceResult) {
        int newStatus = faceResult.get(0).getFaceStatus();
        float time = currentTime;
        if ((newStatus & _item.getStart()) != 0) { // 满足触发条件
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
                if ((_firstFaceStatus & _item.getStart()) != 0) { // 上一帧也满足触发条件
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
                } else { // 上一帧不满足触发条件
                    _startTs = now;
                    time = (float) (now - _startTs) / 1000f;
                    _animating = true;
                }
            }
            _firstFaceStatus = newStatus;
            _secFaceStatus = ZZFaceResult.ZZ_FACESTATUS_UNKNOWN;
            _distanceStrike = false;
        } else {
            //不满足触发条件
            if (_animating) { // 正在动画中
                time = (float) (now - _startTs) / 1000f;
                if (_item.getDuration() > 0.0f) { // 有动画时长限制
                    if (time > _item.getDuration()) { // 超时
                        _animating = false;
                    } else { // 没有超时
                        // do nothing
                    }
                } else { // 没有动画时长限制
                    // do nothing
                }
            } else {
            }
        }
        return time;
    }

    private float checkItemStart(double now, float currentTime, List<ZZFaceResult> faceResult) {
        int itemStart = _item.getStart();
        float time = currentTime;
        switch (itemStart) {
            case ZZFaceResult.ZZTwoFaceStatusFirstFaceToSecFaceDistanceRaise:
                //boolean curIsGoneOnDis = _item.propertyItem.isGoneOnDis();
                boolean curIsGoneOffDis = _item.propertyItem.isGoneOffDis();
                if (_item.propertyItem.isUseOptimizeDistance()) {
                    if (isRaiseSecEffectWithDistanceBetter(faceResult)) {//触发
                        time = setAniFlag(now, time, faceResult);
                        _distanceStrike = true;
                    } else if (_distanceStrike && !curIsGoneOffDis) {//超出指定范围不消失
                        time = setAniFlag(now, time, faceResult);
                    } else {
                        if (_item.getIsAction() > 0) {
                            if (_startTs == 0.0) {
                                _startTs = now;
                            }
                            time = (float)(now - _startTs)/1000f;
                            _animating = false;
                        }else{
                            _animating = false;
                            _startTs = 0.0;
                        }
                    }
                } else {
                    if (isRaiseSecEffectWithDistance(faceResult)) {//触发
                        time = setAniFlag(now, time, faceResult);
                        _distanceStrike = true;
                    } else if (_distanceStrike && !curIsGoneOffDis) {//超出指定范围不消失
                        time = setAniFlag(now, time, faceResult);
                    } else {
                        if (_item.getIsAction() > 0) {
                            if (_startTs == 0.0) {
                                _startTs = now;
                            }
                            time = (float)(now - _startTs)/1000f;
                            _animating = false;
                        }else{
                            _animating = false;
                            _startTs = 0.0;
                        }
                    }
                }
                break;
        }
        return time;
    }

    //当两张人脸时，检测当前特效的结束条件
    private float checkItemEnd(double now, float currentTime, List<ZZFaceResult> faceResult) {
        int itemEnd = _item.getEnd();
        float time = currentTime;
        switch (itemEnd) {
            case ZZFaceResult.ZZTwoFaceStatusFirstFaceToSecFaceDistanceRaise:
                boolean curIsGoneOnDis = _item.propertyItem.isGoneOnDis();
                boolean curIsGoneOffDis = _item.propertyItem.isGoneOffDis();
                if (isRaiseSecEffectWithDistance(faceResult)) {//在指定范围内
                    if (curIsGoneOnDis) {//消失
                        _startTs = 0.0;
                        _animating = false;
                    } else {
                        time = setAniFlag(now, time, faceResult);
                    }
                    _distanceStrike = true;
                } else {
                    if (_distanceStrike && curIsGoneOffDis) {//超出范围消失
                        _startTs = 0.0;
                        _animating = false;
                    } else {
                        _distanceStrike = false;
                        time = setAniFlag(now, time, faceResult);
                    }
                }
                break;
        }
        return time;
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

}

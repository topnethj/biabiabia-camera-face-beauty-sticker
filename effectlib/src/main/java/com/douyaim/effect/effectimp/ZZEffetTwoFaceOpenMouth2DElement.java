package com.douyaim.effect.effectimp;

import android.support.annotation.NonNull;
import com.douyaim.effect.ZZEffectCommon;
import com.douyaim.effect.face.ZZFaceManager_v2;
import com.douyaim.effect.face.ZZFaceResult;
import java.nio.FloatBuffer;
import java.util.List;

/**
 * Created by hj on 17/2/10.
 */

public class ZZEffetTwoFaceOpenMouth2DElement extends ZZEffectTwoFace2DElement {
    private boolean _mouthOpen;//是否张过嘴
    private int _currentMouthCount;
    private int _circleCount;//循环次数
    private boolean _fromOneFaceToTwoFace;//从一张人脸变为两张人脸
    private boolean _fromTwoFaceToOneFace;//从两张人脸变为一张人脸

    public void initWithItem(@NonNull ZZEffect2DItem_v2 curItem, FloatBuffer verticesBuffer, FloatBuffer textureCoordinatesBuffer) {
        super.initWithItem(curItem, verticesBuffer, textureCoordinatesBuffer);
        renew();
    }

    public void reset() {
        super.reset();
    }

    protected void renew() {
        super.renew();
        _mouthOpen = false;
        _fromOneFaceToTwoFace = false;
        _fromTwoFaceToOneFace = false;
        _currentMouthCount = 0;
        _circleCount = 0;
    }

    public void updateWithNoFaceResult() {
        super.updateWithNoFaceResult();
        renew();
    }

    public void updateWithFaceResult(List<ZZFaceResult> faceResult) {
        updateFacePoints(faceResult);
        float time = 0.0f;
        double now = System.currentTimeMillis();

        boolean curIsRaiseWhenOnlyOneFace = _item.propertyItem.isRaiseWhenOnlyOneFace();
        boolean curIsCircle = _item.propertyItem.isCircle();

        if (_faceCount >= 2) {
            if (_fromOneFaceToTwoFace) {
                renew();
            }
            time = checkItemStartWithTwoFaces(now, time, faceResult);
        } else {

            if (_faceCount == 1 && curIsRaiseWhenOnlyOneFace) {

                if (_fromTwoFaceToOneFace) {//从两张人脸变为一张人脸，更新状态位
                    renew();
                }
                if (!curIsCircle) {
                    time = checkItemWithOneFace(now, time, faceResult);
                } else {
                    //张嘴触发并循环
                    if (_item.getStart() == ZZFaceResult.ZZTwoFaceStatusAMouthOpenedToBRaise) {
                        time = checkMouthOpenedItemWithOneFace(now, time, faceResult, 0, ZZFaceResult.ZZEffectAOpenMouthRaiseB);
                        if (_mouthOpen) {
                            _fromOneFaceToTwoFace = true;
                        }
                    }
                }
            } else {
                renew();
            }
        }

        if (!_animating && _item.getIsAction() == 0) {
            time = 0;
        }
        _time = time;
    }

    //张嘴并循环
    private float checkMouthOpenedItemWithOneFace(double now, float currentTime, List<ZZFaceResult> faceResult, int index, int type) {
        int newStatus = faceResult.get(index).getFaceStatus();
        float time = currentTime;

        int curMouthCount = _item.propertyItem.getMouthCount();
        int curOpenMouthType = _item.propertyItem.getOpenMouthType();
        boolean curIsCircle = _item.propertyItem.isCircle();
        if (!_animating) {
            if (curOpenMouthType == type && newStatus == ZZFaceResult.ZZ_FACESTATUS_MOUTHOPENED && curIsCircle) {
                int[] curDivAndMod = calDivAndMod(0, 0);
                _circleCount = curDivAndMod[0];
                _currentMouthCount = curDivAndMod[1];
                if (_currentMouthCount == curMouthCount) {
                    _mouthOpen = true;
                }
            }
        } else {
            if (curOpenMouthType == type && newStatus == ZZFaceResult.ZZ_FACESTATUS_MOUTHOPENED && curIsCircle) {
                int[] curDivAndMod = calDivAndMod(0, 1);
                int currentCountDiv = curDivAndMod[0];
                int currentCountMod = curDivAndMod[1];
                if (currentCountDiv > _circleCount && currentCountMod == _currentMouthCount) {
                    _animating = false;
                    _mouthOpen = false;//....
                    _startTs = 0.0;
                }
            }
        }

        if (_mouthOpen) {
            time = setAniFlag(now, time, faceResult);
        }

        return time;
    }

    public void render() {
        if (!_animating && _item.getIsAction() == 0) {
            return;
        }
        super.render();
    }

    //一张人脸
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
        }
        return time;
    }

    private float checkItemStartWithTwoFaces(double now, float currentTime, List<ZZFaceResult> faceResult) {
        int itemStart = _item.getStart();
        float time = currentTime;
        switch (itemStart) {
            case ZZFaceResult.ZZTwoFaceStatusAMouthOpenedToBRaise://A张嘴触发B
            {
                time = checkMouthOpenedItemWithOneFace(now, time, faceResult, 0, ZZFaceResult.ZZEffectAOpenMouthRaiseB);
                time = checkMouthOpenedItemWithOneFace(now, time, faceResult, 1, ZZFaceResult.ZZEffectBOpenMouthRaiseA);
                if (_mouthOpen) {
                    _fromTwoFaceToOneFace = true;
                }
            }
            break;
            case ZZFaceResult.ZZTwoFaceStatusMouthOpenedRaise: {
                if (faceResult.get(0).getFaceStatus() == ZZFaceResult.ZZ_FACESTATUS_MOUTHOPENED ||
                        faceResult.get(1).getFaceStatus() == ZZFaceResult.ZZ_FACESTATUS_MOUTHOPENED) {
                    _mouthOpen = true;
                }
                if (_mouthOpen) {
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
                    _mouthOpen = false;
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
                    _mouthOpen = false;
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

    private int[] calDivAndMod(int key, int next) {
        int[] divAndMod = new int[2];
        String currkey = "" + key;
        int div = 0;
        int modd = 0;
        if (ZZFaceManager_v2.getZZFaceManager().mouthCount.containsKey(currkey)) {
            div = (ZZFaceManager_v2.getZZFaceManager().mouthCount.get(currkey).intValue() + next) / ZZEffectCommon.ZZEffectElementNumber;
            modd = (ZZFaceManager_v2.getZZFaceManager().mouthCount.get(currkey).intValue() + next) % ZZEffectCommon.ZZEffectElementNumber;
        }
        divAndMod[0] = div;
        divAndMod[1] = modd;
        return divAndMod;
    }

}

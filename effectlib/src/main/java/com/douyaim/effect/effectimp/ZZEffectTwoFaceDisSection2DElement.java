package com.douyaim.effect.effectimp;

import android.opengl.GLES20;
import com.douyaim.effect.face.ZZFaceManager_v2;
import com.douyaim.effect.face.ZZFaceResult;
import com.douyaim.effect.utils.UniformUtil2;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;

/**
 * Created by hj on 17/1/19.
 */

public class ZZEffectTwoFaceDisSection2DElement extends ZZEffectTwoFace2DElement {
    private static int value[] = {1, 0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 1};
    private int _reStartUniform;

    private boolean _distanceStrike;
    private boolean _fromOneFaceToTwoFace;//从一张人脸变为两张人脸
    private boolean _fromTwoFaceToOneFace;//从两张人脸变为一张人脸
    private boolean _reStart;//重新计时
    //int _randomValue;
    private int _count;
    private Random random = new Random();

    public void initWithItem(ZZEffect2DItem_v2 curItem, FloatBuffer verticesBuffer, FloatBuffer textureCoordinatesBuffer) {
        super.initWithItem(curItem, verticesBuffer, textureCoordinatesBuffer);
        renew();
        _count = 0;
    }

    public void reset() {
        super.reset();
        //renew();
    }

    protected void renew() {
        super.renew();
        _distanceStrike = false;
        _fromOneFaceToTwoFace = false;
        _fromTwoFaceToOneFace = false;
        _reStart = false;
    }

    public void updateWithFaceResult(List<ZZFaceResult> faceResult) {
        updateFacePoints(faceResult);
        float time = 0.0f;
        double now = System.currentTimeMillis();
        if (checkItemStartWithSecFace(faceResult)) {//开始条件是第二张人脸出现
            time = secFaceRaiseListener(now, time, faceResult);
        } else if (isRaiseSecEffectWithDistanceBetter(faceResult) && _isStart() && !_distanceStrike) {//两张人脸在距离范围内
            time = faceWithinScopeListener(now, time);
        } else {
            if (_fromTwoFaceToOneFace) {
                _fromTwoFaceToOneFace = false;
                _overTime();
            }
            if (_animating) { // 正在动画中
                time = (float) (now - _startTs) / 1000f;
                if (_item.getDuration() > 0.0f) { // 有动画时长限制
                    if (time > _item.getDuration()) { // 超时
                        _overTime();
                        ZZFaceManager_v2.getZZFaceManager().setRandom(-1);
                        _fromOneFaceToTwoFace = false;
                        _reStart = false;
                    } else { // 没有超时
                        // do nothing
                    }
                } else { // 没有动画时长限制
                    // do nothing
                }
            } else { // 没有在动画中
                if (_item.getIsAction() > 0) {
                    if (_startTs == 0.0) {
                        _startTs = now;
                    }
                    time = (float) (now - _startTs) / 1000f;
                    _fromOneFaceToTwoFace = true;
                }
            }
        }

        if (_item.getIsAction() == 0 && !_animating) {
            time = 0.0f;
        }
        _time = time;
    }

    private void _overTime() {
        _animating = false;
        _distanceStrike = false;
        _startTs = 0.0;
    }

    public void generateProgram() {
        super.generateProgram();
        _reStartUniform = GLES20.glGetUniformLocation(_program, "reStart");
    }

    private float secFaceRaiseListener(double now, float current, List<ZZFaceResult> faceResult) {
        float time = current;
        if (!_reStart && isRaiseSecEffectWithDistanceBetter(faceResult)) {
            if (!_reStart) {//在指定距离内，重新计时
                _startTs = now;
                _reStart = true;
            }
            time = (float) (now - _startTs) / 1000f;
            if (!_animating) {
                if (_item.getDuration() > 0.0f) { // 有动画时长限制
                    if (time > _item.getDuration()) { // 超时
                        _overTime();
                        _reStart = false;
                    } else { // 没有超时
                        _animating = true;
                    }
                } else { // 没有动画时长限制
                    _animating = true;
                }
            } else {
                if (_item.getDuration() > 0.0f) { // 有动画时长限制
                    if (time > _item.getDuration()) { // 超时
                        _overTime();
                        _reStart = false;
                    } else { // 没有超时
                        _animating = true;
                    }
                } else { // 没有动画时长限制
                    _animating = true;
                }
            }
        } else {
            if (faceResult.size() >= 2) {//在范围之外
                if (!_animating) {
                    if (_item.getIsAction() > 0) {
                        if (_startTs == 0.0) {
                            _startTs = now;
                        }
                        time = (float) (now - _startTs) / 1000f;
                    }
                } else {
                    if (_startTs == 0.0) {
                        _startTs = now;
                    }
                    time = (float) (now - _startTs) / 1000f;
                    if (_reStart) {
                        if (_item.getDuration() > 0.0f) { // 有动画时长限制
                            if (time > _item.getDuration()) { // 超时
                                _overTime();
                                _reStart = false;
                            } else { // 没有超时
                                _animating = true;
                            }
                        } else { // 没有动画时长限制
                            _animating = true;
                        }
                    }
                }
            } else {
                renew();
            }
        }
        return time;
    }

    //两张人脸在指定距离内
    private float faceWithinScopeListener(double now, float current) {
        float time = current;
        //从一张人脸状态转化为两张人脸状态
        if (_fromOneFaceToTwoFace) {
            _fromOneFaceToTwoFace = false;
            _overTime();
        }
        updateRandomValue();
        _distanceStrike = true;
        if (_animating) { // 正在动画中
            if (_startTs == 0.0) {
                _startTs = now;
            }
            time = (float) (now - _startTs) / 1000f;
            if (_item.getDuration() > 0.0f) { // 有动画时长限制
                if (time > _item.getDuration()) { // 超时
                    _overTime();
                    _fromTwoFaceToOneFace = false;
                    ZZFaceManager_v2.getZZFaceManager().setRandom(-1);
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
                    _overTime();
                    _fromTwoFaceToOneFace = false;
                    ZZFaceManager_v2.getZZFaceManager().setRandom(-1);
                } else { // 没有超时
                    _animating = true;
                }
            } else { // 没有动画时长限制
                _animating = true;
                _fromTwoFaceToOneFace = true;
            }
        }
        return time;
    }

    //更新随机数
    private void updateRandomValue() {
        if (ZZFaceManager_v2.getZZFaceManager().getRandom() == -1 && _item.getRandomType() != ZZFaceResult.RandomType_default) {
            switch (_item.getRandomType()) {
                case ZZFaceResult.RandomType_random:{
                    int randValue = random.nextInt(3);
                    ZZFaceManager_v2.getZZFaceManager().setRandom(randValue);
                }
                break;
                default:{//伪随机
                    _count = _count % 12;
                    ZZFaceManager_v2.getZZFaceManager().setRandom(value[_count]);
                    _count++;
                }
                break;
            }
        }
    }

    private boolean _isStart() {
        return _item.getStart() == ZZFaceResult.ZZTwoFaceStatusFirstFaceToSecFaceDistanceRaise;
    }

    private boolean checkItemStartWithSecFace(List<ZZFaceResult> faceResult) {
        if (faceResult.size() >= 2 && _item.getStart() == ZZFaceResult.ZZTwoFaceStatusSecFaceRaise) {
            return true;
        }
        return false;
    }

    public void render() {
        if (!_animating && _item.getIsAction() == 0) {
            return;
        }
        GLES20.glUseProgram(_program);
        UniformUtil2.setInteger(_reStartUniform, _reStart ? 1 : 0);
        super.render();
    }

    public void updateWithNoFaceResult() {
        super.updateWithNoFaceResult();
        renew();
        _count = 0;
    }

}

package com.biabiabia.effect.effectimp;

/**
 * Created by hj on 17/8/29.
 */

public class ZZEffectAction {
    ZZEffectFaceItem_v2 _item;
    int _faceStatus;
    double _startTs;
    boolean _animating;

    public void initWithItem(ZZEffectFaceItem_v2 item) {
        _item = item;
        _startTs = 0.0;
        _animating = false;
    }

    public void setFaceStatus(int status) {
        _faceStatus = status;
    }

    public void reset() {
        _startTs = 0.0;
        _animating = false;
    }

    public float commonTriggerFace(float t, double now, int newStatus) {
        float time = t;
        if ((newStatus & _item.getEnd()) != 0) { // 满足终止条件
            _startTs = 0.0;
            _animating = false;
        } else if ((newStatus & _item.getStart()) != 0) { // 满足触发条件
            if (_animating) { // 正在动画中
                if (_startTs == 0.0) {
                    _startTs = now;
                }
                time = (float) (now - _startTs) / 1000f;
                if (_item.getDuration() > 0.0f) { // 有动画时长限制
                    if (time > _item.getDuration()) { // 超时
                        _animating = false;
                        time = 0.0f;
                    } else { // 没有超时
                        _animating = true;
                    }
                } else { // 没有动画时长限制
                    _animating = true;
                }
            } else { // 没有在动画中
                if ((_faceStatus & _item.getStart()) != 0) { // 上一帧也满足触发条件
                    if (_startTs == 0.0) {
                        _startTs = now;
                    }
                    time = (float) (now - _startTs) / 1000f;
                    if (_item.getDuration() > 0.0f) { // 有动画时长限制
                        if (time > _item.getDuration()) { // 超时
                            _animating = false;
                            time = 0.0f;
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
        } else {
            if (_animating) { // 正在动画中
                time = (float) (now - _startTs) / 1000f;
                if (_item.getDuration() > 0.0f) { // 有动画时长限制
                    if (time > _item.getDuration()) { // 超时
                        _animating = false;
                        time = 0.0f;
                    } else { // 没有超时
                        // do nothing
                    }
                } else { // 没有动画时长限制
                    // do nothing
                }
            } else { // 没有在动画中
            }
        }
        return time;
    }
}

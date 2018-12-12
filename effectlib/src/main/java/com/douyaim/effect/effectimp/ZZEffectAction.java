package com.douyaim.effect.effectimp;

import android.graphics.PointF;
import com.douyaim.effect.face.ZZFaceResult;
import java.util.List;

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

    //通过距离 更改人脸变形的类型
    public int updateFaceWarpTypeByDistance(List<ZZFaceResult> faceResult, int[] indexs) {
        if (_item.getPropertyItem() == null || !_item.getPropertyItem().isTwoFaceEffect() || indexs == null) {//不是双人特效，直接返回0
            return 0;
        }
        if (faceResult.size() < 2) {//若当前检测到一个人脸，则不进行双人特效
            return 0;
        }
        boolean isOnDis = isRaiseSecEffectWithDistanceBetter(faceResult, indexs);
        if (!isOnDis) {
            return 0;
        }
        return 1;
    }

    //判断距离是否小于指定的距离，小于指定距离触发第二张人脸特效
    private boolean isRaiseSecEffectWithDistanceBetter(List<ZZFaceResult> faceResult, int[] indexs) {
        if (faceResult.size() < 2) {//只有一张人脸不触发第二张人脸的特效
            return false;
        }
        int centerIndex1 = 0;
        int centerIndex2 = 32;
        float SCALE_Z = 0.25f;
        int mouthIndex2 = 93;
        float point0ToPoint32First = ZZEffectUtils.distanceBetweenPoints(faceResult.get(0).getPoints()[centerIndex1], faceResult.get(0).getPoints()[centerIndex2]);
        float point0ToPoint32Sec = ZZEffectUtils.distanceBetweenPoints(faceResult.get(1).getPoints()[centerIndex1], faceResult.get(1).getPoints()[centerIndex2]);
        float scale = point0ToPoint32First / point0ToPoint32Sec;

        if (scale < (1.0f - SCALE_Z) || scale > (1.0f + SCALE_Z)) {
            return false;
        }
        //计算两张人脸之间的距离
        float faceToFaceDistance = ZZEffectUtils.distanceBetweenPoints(faceResult.get(0).getPoints()[mouthIndex2], faceResult.get(1).getPoints()[mouthIndex2]);

        //2017-02-06 start
        if (indexs.length == 2) {
            int firIndex = indexs[0];
            int secIndex = indexs[1];
            PointF p1 = new PointF();
            PointF p2 = new PointF();
            p1.x = (faceResult.get(0).getPoints()[firIndex].x + faceResult.get(0).getPoints()[secIndex].x) * 0.5f;
            p1.y = (faceResult.get(0).getPoints()[firIndex].y + faceResult.get(0).getPoints()[secIndex].y) * 0.5f;
            p2.x = (faceResult.get(1).getPoints()[firIndex].x + faceResult.get(1).getPoints()[secIndex].x) * 0.5f;
            p2.y = (faceResult.get(1).getPoints()[firIndex].y + faceResult.get(1).getPoints()[secIndex].y) * 0.5f;
            faceToFaceDistance = ZZEffectUtils.distanceBetweenPoints(p1, p2);
        }
        //2017-02-06  end

        float topToBottom1 = ZZEffectUtils.distanceBetweenPoints(faceResult.get(0).getPoints()[16], faceResult.get(0).getPoints()[43]);
        float topToBottom2 = ZZEffectUtils.distanceBetweenPoints(faceResult.get(1).getPoints()[16], faceResult.get(1).getPoints()[43]);

        float dis = (topToBottom1 + topToBottom2 + point0ToPoint32First + point0ToPoint32Sec) * 0.5f;
        float normalDis = _item.getPropertyItem().getScale() * dis;

        if (faceToFaceDistance < normalDis) {
            return true;
        }
        return false;
    }

}

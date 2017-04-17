package com.douyaim.effect.effectimp;

import android.media.SoundPool;
import android.support.annotation.NonNull;
import com.douyaim.effect.face.ZZFaceResult;

/**
 * Created by hj on 16/10/20.
 */

public class ZZEffectAudioPlayer_v2 {

    ZZEffectAudioItem_v2 _item;
    private double _startTs;
    private int _faceStatus;
    private boolean _animating;
    private boolean running;
    private SoundPool _soundPool;
    private int soundID = -1;
    private int streamID = -1;
    private int repeat = 0;
    public boolean isLoaded = false;

    public void initWithItem (@NonNull ZZEffectAudioItem_v2 item, @NonNull SoundPool soundPool) {
        this._item = item;
        this._soundPool = soundPool;
        _faceStatus = ZZFaceResult.ZZ_FACESTATUS_UNKNOWN;
        _startTs = 0.0;
        _animating = false;
        repeat = (_item.getRepeat() ? -1 : 0);

        String path = _item.getDirPath() + _item.getFilename();
        try {
            this.soundID = _soundPool.load(path, 1);
        } catch (Exception e) {
        }
    }

    public void updateWithFaceResult (int faceStatus) {
        double now = System.currentTimeMillis();

        if (!((faceStatus & _item.getEnd()) == 0)) { // 满足终止条件
            _startTs = 0.0;
            _animating = false;
        } else if (!((faceStatus & _item.getStart()) == 0)) { // 满足触发条件
            if (_animating) { // 正在动画中
                if (_startTs == 0.0) {
                    _startTs = now;
                }
                float time = (float)(now - _startTs)/1000f;
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
                if (!((_faceStatus & _item.getStart()) == 0)) { // 上一帧也满足触发条件
                    if (_startTs == 0.0) {
                        _startTs = now;
                    }
                    float time = (float)(now - _startTs)/1000f;
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
                    _animating = true;
                }
            }
        } else {
            if (_animating) { // 正在动画中
                float time = (float)(now - _startTs)/1000f;
                if (_item.getDuration() > 0.0f) { // 有动画时长限制
                    if (time > _item.getDuration()) { // 超时
                        _animating = false;
                    } else { // 没有超时
                        // do nothing
                    }
                } else { // 没有动画时长限制
                    // do nothing
                }
            } else { // 没有在动画中
                // do nothing
            }
        }

        _faceStatus = faceStatus;

        if (_animating) {
            play1();
        }else{
            stop1();
        }
    }

    public void play() {
        new Thread(){
            public void run(){
                while(!isLoaded){
                    try {
                        Thread.sleep(222);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                if(soundID != -1){
                    try{
                        streamID = _soundPool.play(soundID ,1, 1, 0, repeat, 1);
                    }catch (Exception e){
                    }
                }
            }
        }.start();
    }

    public void play1() {
        try{
            if(soundID != -1 && !running){
                running = true;
                streamID = _soundPool.play(soundID ,1, 1, 0, repeat, 1);
            }
        }catch (Exception e){
        }
    }

    public void stop1() {
        try{
            if(running && streamID != -1){
                _soundPool.stop(streamID);
                running = false;
            }
        }catch (Exception e){
        }
    }

    public void destroy() {
        isLoaded = true;
        soundID = -1;
    }

}

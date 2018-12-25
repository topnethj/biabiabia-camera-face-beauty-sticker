package com.biabiabia.effect.Filter;

import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.support.annotation.NonNull;
import com.biabiabia.effect.ZZEffectCommon;
import com.biabiabia.effect.effectControl.ZZEffectControl_Screen;
import com.biabiabia.effect.effectimp.ZZEffectTexCoorItem;
import com.biabiabia.effect.effectimp.ZZEffectTextureManager;
import com.biabiabia.effect.effectimp.ZZEffectTieZhiItem_v2;
import com.biabiabia.effect.face.ZZFaceResult;
import com.biabiabia.effect.model.Matrix3;
import com.biabiabia.effect.utils.OpenGlUtils;
import com.biabiabia.effect.utils.ScreenUtils;
import com.biabiabia.effect.utils.UniformUtil2;
import com.biabiabia.effect.LibApp;
import java.util.List;
import java.util.Random;

/**
 * Created by hj on 16/11/29.
 */

public class ZZEffectTieZhiFilter_v2 extends GPUImageFilter {

    private ZZEffectTieZhiItem_v2 _item;
    private int _enableUniform, _timeUniform, _facePointsUniform, _screenRatioUniform, _randxUniform,
            _randyUniform, _blendtypeUniform, _cameraFrontUniform, _texCoorMatUniform;
    private int _mGLUniformOutputImageTexture;

    private PointF[] _facePoints;
    private double _start;
    private float _time;
    private int _enable;
    private float _aspectRatio;
    private int _texture;
    private boolean _animating;
    private boolean _reStart;
    private ZZEffectControl_Screen _control;
    private Random random = new Random();

    public ZZEffectTieZhiFilter_v2(@NonNull ZZEffectTieZhiItem_v2 item) {
        super(OpenGlUtils.readShaderFromSD(LibApp.getAppContext(), item.getDirPath() + item.getVertex()),
                OpenGlUtils.readShaderFromSD(LibApp.getAppContext(), item.getDirPath() + item.getFragment()));
        this._item = item;
    }

    @Override
    public void onInit() {
        super.onInit();
        _enableUniform = GLES20.glGetUniformLocation(getProgram(), "enable");
        _timeUniform = GLES20.glGetUniformLocation(getProgram(), "time");
        _facePointsUniform = GLES20.glGetUniformLocation(getProgram(), "facePoints");
        _screenRatioUniform = GLES20.glGetUniformLocation(getProgram(), "screenRatio");
        _randxUniform = GLES20.glGetUniformLocation(getProgram(), "randx");
        _randyUniform = GLES20.glGetUniformLocation(getProgram(), "randy");
        _blendtypeUniform = GLES20.glGetUniformLocation(getProgram(), "blendtype");
        _cameraFrontUniform = GLES20.glGetUniformLocation(getProgram(), "CameraFront");
        _texCoorMatUniform = GLES20.glGetUniformLocation(getProgram(), "texCoorMat");
        _mGLUniformOutputImageTexture = GLES20.glGetUniformLocation(getProgram(), "outputImageTexture");

        _control = new ZZEffectControl_Screen();

        _facePoints = new PointF[ZZEffectCommon.ZZNumberOfFacePoints];
        for(int i = 0; i < _facePoints.length; i++){
            _facePoints[i] = new PointF();
            _facePoints[i].x = 0.0f;
            _facePoints[i].y = 0.0f;
        }

        _start = System.currentTimeMillis();

        Point point = ScreenUtils.getScreenRealSize(LibApp.getAppContext());
        _aspectRatio = (float)point.x/(float)point.y;

        ZZEffectTexCoorItem texCoorItem = ZZEffectTextureManager.getZZEffectTextureManager().getTexCoorByName(_item.getTexCoorName());
        if(texCoorItem != null){
            _texture = ZZEffectTextureManager.getZZEffectTextureManager().getTextureByPath(_item.getDirPath() + texCoorItem.getImageName());
        }

        initCameraFrameBuffer();
    }

    public void update(List<ZZFaceResult> faceResult){
        _enable = 0;
        if(faceResult.size() > 0) {
            _enable = 1;
            ZZFaceResult result = faceResult.get(0);
            for (int i=0; i<ZZEffectCommon.ZZNumberOfFacePoints; i++){
                if (result.getFaceStatus() != ZZFaceResult.ZZ_FACESTATUS_UNKNOWN) {
                    _facePoints[i].x = result.getPoints()[i].x;
                    _facePoints[i].y = result.getPoints()[i].y;
                } else {
                    _facePoints[i].x = 0.0f;
                    _facePoints[i].y = 0.0f;
                }
            }
        }
        double now = System.currentTimeMillis();
        if (!_item.isReStart()) {
            _time = (float)(now - _start) / 1000f;
        } else {
            if (!_reStart) {
                if (!_reStart) {//重新计时
                    _reStart = true;
                    _start = now;
                }
                _time = (float)(now - _start) / 1000f;
                if (!_animating) {
                    if (_item.getDuration() > 0.0f){ // 有动画时长限制
                        if (_time > _item.getDuration()){ // 超时
                            _animating = false;
                            _reStart = false;
                            _time = 0.0f;
                            _start = 0.0;
                        }else{ // 没有超时
                            _animating = true;
                        }
                    }else{
                        _animating = true;
                    }
                }else{
                    if (_item.getDuration() > 0.0f){ // 有动画时长限制
                        if (_time > _item.getDuration()){ // 超时
                            _animating = false;
                            _reStart = false;
                            _time = 0.0f;
                            _start = 0.0;
                        }else{ // 没有超时
                            _animating = true;
                            _time = (float)(now - _start) / 1000f;
                        }
                    }else{
                        _time = (float)(now - _start) / 1000f;
                    }
                }
            }else{
                if (_reStart || _animating) {
                    _time = (float)(now - _start) / 1000f;
                    if (_item.getDuration() > 0.0f){ // 有动画时长限制
                        if (_time > _item.getDuration()){ // 超时
                            _animating = false;
                            _reStart = false;
                            _start = now;
                            _time = 0.0f;
                        }else{ // 没有超时
                            _animating = true;
                        }
                    }else{ // 没有动画时长限制
                        _animating = true;
                    }
                }else{
                    _time = 0.0f;
                    _start = 0.0;
                    _animating = false;
                    _reStart = false;
                }
            }
        }
    }

    @Override
    protected void onDrawArraysPre() {
        UniformUtil2.setInteger(_enableUniform, _enable);
        UniformUtil2.setFloat(_timeUniform, _time);
        UniformUtil2.setPoints(_facePointsUniform, _facePoints);
        UniformUtil2.setFloat(_screenRatioUniform, _aspectRatio);
        UniformUtil2.setInteger(_cameraFrontUniform, 1);//TODO:

        if(_item.getEffectType() == 0) {
            //震屏
            float randxValue = (random.nextInt(3))/10.0f - 0.1f;
            float randyValue = (random.nextInt(3))/10.0f - 0.1f;
            UniformUtil2.setFloat(_randxUniform, randxValue);
            UniformUtil2.setFloat(_randyUniform, randyValue);
        }else if(_item.getEffectType() == 1) {
            Matrix3 texCoorMat = _control.texMatrixWithItemName(_item.getTexCoorName(), _time);
            UniformUtil2.setUniformMatrix3fBuffer(_texCoorMatUniform, texCoorMat.val);
            //纹理混合
            UniformUtil2.setInteger(_blendtypeUniform, _item.getBlendType());
            GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _texture);
            GLES20.glUniform1i(_mGLUniformOutputImageTexture, 4);
        }

        GLES20.glViewport(0, 0, mIntputWidth, mIntputHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
    }

    @Override
    protected void onDrawArraysAfter() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);
    }

    public void onDestroy() {
        super.onDestroy();
        destroyFramebuffers();
        _facePoints = null;
    }

}

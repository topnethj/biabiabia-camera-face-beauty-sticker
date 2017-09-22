package com.douyaim.effect.Filter;

import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.support.annotation.NonNull;

import com.douyaim.effect.ZZEffectCommon;
import com.douyaim.effect.effectimp.ZZEffectFaceItem_v2;
import com.douyaim.effect.face.ZZFaceResult;
import com.douyaim.effect.utils.OpenGlUtils;
import com.douyaim.effect.utils.ScreenUtils;
import com.douyaim.effect.utils.UniformUtil2;
import com.douyaim.qsapp.LibApp;
import java.util.List;

/**
 * Created by hj on 16/10/18.
 */

public class ZZEffectFaceFilter_v2 extends GPUImageFilter{

    private ZZEffectFaceItem_v2 _item;
    private PointF[] _facePoints;
    private float[] _extras = new float[ZZEffectCommon.ZZMaxCountOfShaderExtraArray];
    private float _screenRatio;

    private int _faceStatusUniform1;
    private int _faceStatusUniform2;
    private int _faceStatusUniform3;
    private int _faceStatusUniform4;
    private int _facePointsUniform;
    private int _extrasUniform;
    private int _faceCountUniform;
    private int _aspectRatioUniform;
    private int _leftFourEyesUniform;
    private int _rightFourEyesUniform;
    private int _eyeWidthsUniform;
    private int _eyeHeightsUniform;
    private int _faceMorphCenterUniform;
    private int _faceMorphP1Uniform;
    private int _faceMorphP3Uniform;
    private int _xiabaUniform;
    private int _triangleCenterUniform;
    private int _faceWidthUniform;

    private PointF[] _faceMorphCenterPoint;
    private PointF[] _faceMorphP1;
    private PointF[] _faceMorphP3;
    private PointF[] _triangleCenter;
    private PointF[] _xiaba;
    private float[] _leftEye;
    private float[] _rightEye;
    private float[] _widths;
    private float[] _heights;
    private float[] _widthFace;

    private int count;
    private List<ZZFaceResult> faceResult;

    public ZZEffectFaceFilter_v2(@NonNull ZZEffectFaceItem_v2 item) {
        super(OpenGlUtils.readShaderFromSD(LibApp.getAppContext(), item.getDirPath() + item.getVertexName()),
                OpenGlUtils.readShaderFromSD(LibApp.getAppContext(), item.getDirPath() + item.getFragmentName()));
        this._item = item;
        _facePoints = new PointF[ZZEffectCommon.ZZNumberOfFacePoints * 4];
    }

    @Override
    public void onInit() {
        super.onInit();
        _faceStatusUniform1 = GLES20.glGetUniformLocation(getProgram(), "faceStatus1");
        _faceStatusUniform2 = GLES20.glGetUniformLocation(getProgram(), "faceStatus2");
        _faceStatusUniform3 = GLES20.glGetUniformLocation(getProgram(), "faceStatus3");
        _faceStatusUniform4 = GLES20.glGetUniformLocation(getProgram(), "faceStatus4");
        _facePointsUniform = GLES20.glGetUniformLocation(getProgram(), "facePoints");
        _extrasUniform = GLES20.glGetUniformLocation(getProgram(), "extra");
        _faceCountUniform = GLES20.glGetUniformLocation(getProgram(), "faceCount");
        _aspectRatioUniform = GLES20.glGetUniformLocation(getProgram(), "aspectRatio");
        _leftFourEyesUniform = GLES20.glGetUniformLocation(getProgram(), "leftFourEyes");
        _rightFourEyesUniform = GLES20.glGetUniformLocation(getProgram(), "rightFourEyes");
        _eyeWidthsUniform = GLES20.glGetUniformLocation(getProgram(), "eyeWidths");
        _eyeHeightsUniform = GLES20.glGetUniformLocation(getProgram(), "eyeHeights");
        _faceMorphCenterUniform = GLES20.glGetUniformLocation(getProgram(), "faceMorphCenter");
        _faceMorphP1Uniform = GLES20.glGetUniformLocation(getProgram(), "faceMorphP1");
        _faceMorphP3Uniform = GLES20.glGetUniformLocation(getProgram(), "faceMorphP3");
        _xiabaUniform = GLES20.glGetUniformLocation(getProgram(), "xiaba");
        _triangleCenterUniform = GLES20.glGetUniformLocation(getProgram(), "triangleCenter");
        _faceWidthUniform = GLES20.glGetUniformLocation(getProgram(), "faceWidth");

        //initCameraFrameBuffer();

        for (int i = 0; i < ZZEffectCommon.ZZNumberOfFacePoints * 4; i++){
            _facePoints[i] = new PointF();
            _facePoints[i].x = 0.0f;
            _facePoints[i].y = 0.0f;
        }

        if(_item.getExtras() != null && _item.getExtras().length > 0){
            for (int i = 0; i < ZZEffectCommon.ZZMaxCountOfShaderExtraArray; i++) {
                if (i < _item.getExtras().length) {
                    _extras[i] = _item.getExtras()[i];
                } else {
                    _extras[i] = 0.0f;
                }
            }
        }

        _faceMorphCenterPoint = new PointF[4];
        _faceMorphP1 = new PointF[4];
        _faceMorphP3 = new PointF[4];
        _triangleCenter = new PointF[4];
        _xiaba = new PointF[4];

        _leftEye = new float[16];
        _rightEye = new float[16];
        _widths = new float[4];
        _heights = new float[4];
        _widthFace = new float[4];

        for(int i = 0; i < _faceMorphCenterPoint.length; i++){
            _faceMorphCenterPoint[i] = new PointF();
            _faceMorphCenterPoint[i].x = 0.0f;
            _faceMorphCenterPoint[i].y = 0.0f;
        }

        for(int i = 0; i < _faceMorphP1.length; i++){
            _faceMorphP1[i] = new PointF();
            _faceMorphP1[i].x = 0.0f;
            _faceMorphP1[i].y = 0.0f;
        }

        for(int i = 0; i < _faceMorphP3.length; i++){
            _faceMorphP3[i] = new PointF();
            _faceMorphP3[i].x = 0.0f;
            _faceMorphP3[i].y = 0.0f;
        }

        for(int i = 0; i < _triangleCenter.length; i++){
            _triangleCenter[i] = new PointF();
            _triangleCenter[i].x = 0.0f;
            _triangleCenter[i].y = 0.0f;
        }

        for(int i = 0; i < _xiaba.length; i++){
            _xiaba[i] = new PointF();
            _xiaba[i].x = 0.0f;
            _xiaba[i].y = 0.0f;
        }

        Point point = ScreenUtils.getScreenRealSize(LibApp.getAppContext());
        _screenRatio = (float)point.x/(float)point.y;
    }

    public void updateWithFaceResults(List<ZZFaceResult> faceResult) {
        count = 0;
        this.faceResult = faceResult;
        if(faceResult.size() > 0){
            for (int j = 0; j < ZZEffectCommon.ZZNumberOfFace; j++){
                if(j < faceResult.size() && j < _item.getCount()){
                    ZZFaceResult result = faceResult.get(j);
                    for (int i = 0; i < ZZEffectCommon.ZZNumberOfFacePoints; i++){
                        int t = j * ZZEffectCommon.ZZNumberOfFacePoints + i;
                        if(result.getFaceStatus() != ZZFaceResult.ZZ_FACESTATUS_UNKNOWN){
                            _facePoints[t].x = result.getPoints()[i].x;
                            _facePoints[t].y = -result.getPoints()[i].y;
                        }else{
                            _facePoints[t].x = 0.0f;
                            _facePoints[t].y = 0.0f;
                        }
                    }
                    if(result.getFaceStatus() != ZZFaceResult.ZZ_FACESTATUS_UNKNOWN){
                        count++;
                    }
                }else{
                    for (int i = 0; i < ZZEffectCommon.ZZNumberOfFacePoints; i++){
                        int t = j * ZZEffectCommon.ZZNumberOfFacePoints + i;
                        _facePoints[t].x = 0.0f;
                        _facePoints[t].y = 0.0f;
                    }
                }
            }
        }
    }

    @Override
    protected void onDrawArraysPre() {
        switch (count) {
            case ZZEffectCommon.ZZEffectFaceCount0:
                UniformUtil2.setInteger(_faceStatusUniform1, ZZFaceResult.ZZ_FACESTATUS_UNKNOWN);
                UniformUtil2.setInteger(_faceStatusUniform2, ZZFaceResult.ZZ_FACESTATUS_UNKNOWN);
                UniformUtil2.setInteger(_faceStatusUniform3, ZZFaceResult.ZZ_FACESTATUS_UNKNOWN);
                UniformUtil2.setInteger(_faceStatusUniform4, ZZFaceResult.ZZ_FACESTATUS_UNKNOWN);
                break;
            case ZZEffectCommon.ZZEffectFaceCount1:
                UniformUtil2.setInteger(_faceStatusUniform1, faceResult.get(0).getFaceStatus());
                UniformUtil2.setInteger(_faceStatusUniform2, ZZFaceResult.ZZ_FACESTATUS_UNKNOWN);
                UniformUtil2.setInteger(_faceStatusUniform3, ZZFaceResult.ZZ_FACESTATUS_UNKNOWN);
                UniformUtil2.setInteger(_faceStatusUniform4, ZZFaceResult.ZZ_FACESTATUS_UNKNOWN);
                break;
            case ZZEffectCommon.ZZEffectFaceCount2:
                UniformUtil2.setInteger(_faceStatusUniform1, faceResult.get(0).getFaceStatus());
                UniformUtil2.setInteger(_faceStatusUniform2, faceResult.get(1).getFaceStatus());
                UniformUtil2.setInteger(_faceStatusUniform3, ZZFaceResult.ZZ_FACESTATUS_UNKNOWN);
                UniformUtil2.setInteger(_faceStatusUniform4, ZZFaceResult.ZZ_FACESTATUS_UNKNOWN);
                break;
            case ZZEffectCommon.ZZEffectFaceCount3:
                UniformUtil2.setInteger(_faceStatusUniform1, faceResult.get(0).getFaceStatus());
                UniformUtil2.setInteger(_faceStatusUniform2, faceResult.get(1).getFaceStatus());
                UniformUtil2.setInteger(_faceStatusUniform3, faceResult.get(2).getFaceStatus());
                UniformUtil2.setInteger(_faceStatusUniform4, ZZFaceResult.ZZ_FACESTATUS_UNKNOWN);
                break;
            case ZZEffectCommon.ZZEffectFaceCount4:
                UniformUtil2.setInteger(_faceStatusUniform1, faceResult.get(0).getFaceStatus());
                UniformUtil2.setInteger(_faceStatusUniform2, faceResult.get(1).getFaceStatus());
                UniformUtil2.setInteger(_faceStatusUniform3, faceResult.get(2).getFaceStatus());
                UniformUtil2.setInteger(_faceStatusUniform4, faceResult.get(3).getFaceStatus());
                break;
            default:
                UniformUtil2.setInteger(_faceStatusUniform1, faceResult.get(0).getFaceStatus());
                UniformUtil2.setInteger(_faceStatusUniform2, faceResult.get(1).getFaceStatus());
                UniformUtil2.setInteger(_faceStatusUniform3, faceResult.get(2).getFaceStatus());
                UniformUtil2.setInteger(_faceStatusUniform4, faceResult.get(3).getFaceStatus());
                break;
        }

        UniformUtil2.setInteger(_faceCountUniform, count);
        UniformUtil2.setPoints(_facePointsUniform, _facePoints);
        UniformUtil2.setFloatArray(_extrasUniform, _extras);
        UniformUtil2.setFloat(_aspectRatioUniform, _screenRatio);

        //大眼睛数据
        updateLeftFourEye();
        updateRightFourEye();
        updateEyeWidth();
        updateEyeHeight();

        //瘦脸数据
        updateFacePoints();

        updateFaceWidthVec();

        //扁下巴
        updateTriangle();

        //GLES20.glViewport(0, 0, mIntputWidth, mIntputHeight);
        //GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
    }

    private void updateLeftFourEye() {
        int leftEyeIndex = 74;
        _leftEye[0] = _facePoints[leftEyeIndex + 0 * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
        _leftEye[4] = _facePoints[leftEyeIndex + 0 * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;
        _leftEye[8] = 0.0f;
        _leftEye[12] = 0.0f;
        _leftEye[1] = 0.0f;
        _leftEye[5] = 0.0f;
        _leftEye[9] = 0.0f;
        _leftEye[13] = 0.0f;
        _leftEye[2] = 0.0f;
        _leftEye[6] = 0.0f;
        _leftEye[10] = 0.0f;
        _leftEye[14] = 0.0f;
        _leftEye[3] = 0.0f;
        _leftEye[7] = 0.0f;
        _leftEye[11] = 0.0f;
        _leftEye[15] = 0.0f;
        UniformUtil2.setUniformMatrix4fBuffer(_leftFourEyesUniform, _leftEye);
    }

    private void updateRightFourEye() {
        int rightEyeIndex = 77;
        _rightEye[0] = _facePoints[rightEyeIndex + 0 * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
        _rightEye[4] = _facePoints[rightEyeIndex + 0 * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;
        _rightEye[8] = 0.0f;
        _rightEye[12] = 0.0f;
        _rightEye[1] = 0.0f;
        _rightEye[5] = 0.0f;
        _rightEye[9] = 0.0f;
        _rightEye[13] = 0.0f;
        _rightEye[2] = 0.0f;
        _rightEye[6] = 0.0f;
        _rightEye[10] = 0.0f;
        _rightEye[14] = 0.0f;
        _rightEye[3] = 0.0f;
        _rightEye[7] = 0.0f;
        _rightEye[11] = 0.0f;
        _rightEye[15] = 0.0f;
        UniformUtil2.setUniformMatrix4fBuffer(_rightFourEyesUniform, _rightEye);
    }

    //更新四张脸眼睛的宽度
    private void updateEyeWidth() {
        int index1 = 55;
        int index2 = 52;
        _widths[0] = (_facePoints[index1 + 0 * ZZEffectCommon.ZZNumberOfFacePoints].x - _facePoints[index2 + 0 * ZZEffectCommon.ZZNumberOfFacePoints].x);
        _widths[1] = 0.0f;
        _widths[2] = 0.0f;
        _widths[3] = 0.0f;
        UniformUtil2.setFloatVec4(_eyeWidthsUniform, _widths);
    }

    //更新四张脸眼睛高度
    private void updateEyeHeight() {
        int index1 = 73;
        int index2 = 72;
        _heights[0] = (_facePoints[index1 + 0 * ZZEffectCommon.ZZNumberOfFacePoints].y - _facePoints[index2 + 0 * ZZEffectCommon.ZZNumberOfFacePoints].y);
        _heights[1] = 0.0f;
        _heights[2] = 0.0f;
        _heights[3] = 0.0f;
        UniformUtil2.setFloatVec4(_eyeHeightsUniform, _heights);
    }

    private void updateFacePoints() {
        for (int i = 0; i < ZZEffectCommon.ZZNumberOfFace; i++) {
            _faceMorphCenterPoint[i].x = _facePoints[46 + i * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
            _faceMorphCenterPoint[i].y = _facePoints[46 + i * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;
            //faceMorphP1
            _faceMorphP1[i].x = _facePoints[45 + i * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
            _faceMorphP1[i].y = _facePoints[45 + i * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;
            //faceMorphP3
            _faceMorphP3[i].x = _facePoints[43 + i * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
            _faceMorphP3[i].y = _facePoints[43 + i * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;

            _xiaba[i].x = _facePoints[16 + i * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
            _xiaba[i].y = _facePoints[16 + i * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;
        }

        UniformUtil2.setPoints(_faceMorphCenterUniform, _faceMorphCenterPoint);
        UniformUtil2.setPoints(_faceMorphP1Uniform, _faceMorphP1);
        UniformUtil2.setPoints(_faceMorphP3Uniform, _faceMorphP3);
        UniformUtil2.setPoints(_xiabaUniform, _xiaba);
    }

    private void updateFaceWidthVec() {
        int index1 = 0;
        int index2 = 32;
        _widthFace[0] = getDistancePoints((index1 + 0 * ZZEffectCommon.ZZNumberOfFacePoints), (index2 + 0 * ZZEffectCommon.ZZNumberOfFacePoints), _screenRatio);
        _widthFace[1] = 0.0f;
        _widthFace[2] = 0.0f;
        _widthFace[3] = 0.0f;
        UniformUtil2.setFloatVec4(_faceWidthUniform, _widthFace);
    }

    private float getDistancePoints(int index1, int index2, float ratio) {
        float xspan = _facePoints[index1].x - _facePoints[index2].x;
        float yspan = _facePoints[index1].y/ratio - _facePoints[index2].y/ratio;
        float result = (float)Math.sqrt(Math.pow(xspan, 2.0f) + Math.pow(yspan, 2.0f));
        return result;
    }

    private void updateTriangle() {
        int centerIndex = 49;
        for (int i = 0; i < ZZEffectCommon.ZZNumberOfFace; i++) {
            //triangleCenter
            _triangleCenter[i].x = _facePoints[centerIndex + i * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
            _triangleCenter[i].y = _facePoints[centerIndex + i * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;
        }
        UniformUtil2.setPoints(_triangleCenterUniform, _triangleCenter);
    }

    @Override
    protected void onDrawArraysAfter() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);
    }

    private void dealloc() {
        faceResult = null;
        _facePoints = null;
        _extras = null;
        _faceMorphCenterPoint = null;
        _faceMorphP1 = null;
        _faceMorphP3 = null;
        _triangleCenter = null;
        _xiaba = null;
    }

    public void onDestroy() {
        super.onDestroy();
        destroyFramebuffers();
        dealloc();
    }

}

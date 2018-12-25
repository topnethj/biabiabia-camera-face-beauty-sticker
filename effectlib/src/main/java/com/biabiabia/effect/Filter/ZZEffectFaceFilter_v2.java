package com.biabiabia.effect.Filter;

import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.support.annotation.NonNull;
import com.biabiabia.effect.ZZEffectCommon;
import com.biabiabia.effect.effectimp.ZZEffectAction;
import com.biabiabia.effect.effectimp.ZZEffectFaceItem_v2;
import com.biabiabia.effect.face.ZZFaceResult;
import com.biabiabia.effect.utils.OpenGlUtils;
import com.biabiabia.effect.utils.ScreenUtils;
import com.biabiabia.effect.utils.UniformUtil2;
import com.biabiabia.effect.LibApp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hj on 16/10/18.
 */

public class ZZEffectFaceFilter_v2 extends GPUImageFilter{
    private List<ZZFaceResult> faceResult;
    private ZZEffectFaceItem_v2 _item;
    private int count;
    private PointF[] _facePoints;
    private float[] _extras = new float[ZZEffectCommon.ZZMaxCountOfShaderExtraArray];
    private float[] _values = new float[ZZEffectCommon.ZZMaxCountOfShaderExtraArray];

    private PointF[] _faceMorphCenterPoint;
    private PointF[] _faceMorphP1;
    private PointF[] _faceMorphP3;
    private PointF[] _xiaba;
    private PointF[] _triangleCenter;

    private PointF[] _pointLeftFitFace;//左边点
    private PointF[] _pointRightFitFace;//右边点
    private PointF[] _centerOval;//椭圆中心点
    private PointF[] _pointBottomFitFace;//下顶点
    private List<ZZEffectAction> _actions = new ArrayList<>();
    private float[] _times;

    private float[] _leftEye;
    private float[] _rightEye;
    private float[] _widths;
    private float[] _heights;
    private float[] _widthFace;

    private float _screenRatio;

    //2017-02-02:双人特效  start
    private ZZEffectAction _fourFaceActions;
    private int _changeFace;
    //2017-02-02:双人特效  end

    //抠图  start
    private PointF[] _framePoints;
    private float[] _frameRotates = new float[ZZEffectCommon.ZZMaxCountOfShaderExtraArray];
    private float[] _fillColor;
    private int _frameCount;//对应序列帧的帧数
    //抠图  end

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
    private int _timesUniform,_changeFaceUniform,_framePointsUniform,_frameCountUniform,_frameRotatesUniform,
            _fillColorUniform,_pointLeftFitFaceUniform,_pointRightFitFaceUniform,_centerOvalUniform,
            _pointBottomFitFaceUniform;

    public ZZEffectFaceFilter_v2(@NonNull ZZEffectFaceItem_v2 item) {
        super(OpenGlUtils.readShaderFromSD(LibApp.getAppContext(), item.getDirPath() + item.getVertexName()),
                OpenGlUtils.readShaderFromSD(LibApp.getAppContext(), item.getDirPath() + item.getFragmentName()));
        this._item = item;
        this._facePoints = new PointF[ZZEffectCommon.ZZNumberOfFacePoints * 4];
    }

    @Override
    public void onInit() {
        super.onInit();
        _faceCountUniform = GLES20.glGetUniformLocation(getProgram(), "faceCount");
        _facePointsUniform = GLES20.glGetUniformLocation(getProgram(), "facePoints");
        _aspectRatioUniform = GLES20.glGetUniformLocation(getProgram(), "aspectRatio");
        _extrasUniform = GLES20.glGetUniformLocation(getProgram(), "extra");
        _timesUniform = GLES20.glGetUniformLocation(getProgram(), "times");
        _changeFaceUniform = GLES20.glGetUniformLocation(getProgram(), "changeFace");
        _faceStatusUniform1 = GLES20.glGetUniformLocation(getProgram(), "faceStatus1");
        _faceStatusUniform2 = GLES20.glGetUniformLocation(getProgram(), "faceStatus2");
        _faceStatusUniform3 = GLES20.glGetUniformLocation(getProgram(), "faceStatus3");
        _faceStatusUniform4 = GLES20.glGetUniformLocation(getProgram(), "faceStatus4");
        _framePointsUniform = GLES20.glGetUniformLocation(getProgram(), "framePoints");
        _frameCountUniform = GLES20.glGetUniformLocation(getProgram(), "frameCount");
        _frameRotatesUniform = GLES20.glGetUniformLocation(getProgram(), "frameRotates");
        _fillColorUniform = GLES20.glGetUniformLocation(getProgram(), "fillColor");
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
        _pointLeftFitFaceUniform = GLES20.glGetUniformLocation(getProgram(), "pointLeftFitFace");
        _pointRightFitFaceUniform = GLES20.glGetUniformLocation(getProgram(), "pointRightFitFace");
        _centerOvalUniform = GLES20.glGetUniformLocation(getProgram(), "centerOval");
        _pointBottomFitFaceUniform = GLES20.glGetUniformLocation(getProgram(), "pointBottomFitFace");

        initCameraFrameBuffer();

        Point point = ScreenUtils.getScreenRealSize(LibApp.getAppContext());
        _screenRatio = (float)point.x/(float)point.y;

        for (int i = 0; i < ZZEffectCommon.ZZNumberOfFacePoints * 4; i++){
            _facePoints[i] = new PointF();
            _facePoints[i].x = -2.0f;
            _facePoints[i].y = -2.0f;
        }

        if(_item.getExtras() != null && _item.getExtras().length > 0){
            for (int i = 0; i < ZZEffectCommon.ZZMaxCountOfShaderExtraArray; i++) {
                if (i < _item.getExtras().length) {
                    _extras[i] = _item.getExtras()[i];
                } else {
                    _extras[i] = 0.0f;
                }
                if (i < _item.getExtras().length) {
                    _values[i] = _item.getExtras()[i];
                } else {
                    _values[i] = 0.0f;
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

        //===========肥脸添加   start==================
        fatFaceCreate(_item);
        //===========肥脸添加   end==================

        //2017-02-02:双人特效
        _fourFaceActions = new ZZEffectAction();
        _fourFaceActions.initWithItem(_item);
        _changeFace = 0;

        //抠图
        initCutOutData(_item);
    }

    private void initCutOutData(ZZEffectFaceItem_v2 item) {
        //序列帧的位置
        _frameCount = item.getFrameCount();
        int pointCount = 0;
        if (item.getFramePoints() != null) {
            pointCount = item.getFramePoints().length / 2;
        }
        _frameCount = _frameCount < pointCount ? _frameCount : pointCount;
        _framePoints = new PointF[ZZEffectCommon.ZZMaxCountOfShaderExtraArray];

        for (int i = 0; i < ZZEffectCommon.ZZMaxCountOfShaderExtraArray; i++) {
            _framePoints[i] = new PointF();
            if (i < _frameCount) {
                _framePoints[i].x = (item.getFramePoints()[i * 2]) / ZZEffectCommon.ZZFrameWidth;
                _framePoints[i].y = (item.getFramePoints()[i * 2 + 1]) / ZZEffectCommon.ZZFrameHeight;
            } else {
                _framePoints[i].x = 0.0f;
                _framePoints[i].y = 0.0f;
            }
        }

        //旋转角度
        boolean flagAssert = _frameCount > 0 && item.getFrameRotates() != null && item.getFrameRotates().length > 0
                && (_frameCount == item.getFrameRotates().length);
        if (flagAssert) {
            for (int i = 0; i < ZZEffectCommon.ZZMaxCountOfShaderExtraArray; i++) {
                if (i < _item.getFrameRotates().length) {
                    _frameRotates[i] = (float) -ZZEffectCommon.PI * _item.getFrameRotates()[i] / 180;
                } else {
                    _frameRotates[i] = 0.0f;
                }
            }
        }

        //填充颜色
        _fillColor = new float[4];
        _fillColor[0] = 0.0f;
        _fillColor[1] = 0.0f;
        _fillColor[2] = 0.0f;
        _fillColor[3] = 1.0f;
        if (item.getFillColor() != null && item.getFillColor().length > 0) {
            for (int i = 0; i < item.getFillColor().length; i++) {
                if (i == 0) {
                    _fillColor[0] = item.getFillColor()[0] / ZZEffectCommon.ZZColorConstant;
                }else if(i == 1){
                    _fillColor[1] = item.getFillColor()[1] / ZZEffectCommon.ZZColorConstant;
                }else{
                    _fillColor[2] = item.getFillColor()[2] / ZZEffectCommon.ZZColorConstant;
                }
            }
        }
    }

    private void fatFaceCreate(ZZEffectFaceItem_v2 item) {
        _pointLeftFitFace = new PointF[4];//左边点
        _pointRightFitFace = new PointF[4];//鼻梁
        _centerOval = new PointF[4];//0号
        _pointBottomFitFace = new PointF[4];//32号

        for (int i = 0; i < ZZEffectCommon.ZZNumberOfFaceForChange; i++) {
            ZZEffectAction action = new ZZEffectAction();
            action.initWithItem(item);
            _actions.add(action);
        }
        _times = new float[4];

        for(int i = 0; i < _pointLeftFitFace.length; i++){
            _pointLeftFitFace[i] = new PointF();
            _pointLeftFitFace[i].x = 0.0f;
            _pointLeftFitFace[i].y = 0.0f;
        }

        for(int i = 0; i < _pointRightFitFace.length; i++){
            _pointRightFitFace[i] = new PointF();
            _pointRightFitFace[i].x = 0.0f;
            _pointRightFitFace[i].y = 0.0f;
        }

        for(int i = 0; i < _centerOval.length; i++){
            _centerOval[i] = new PointF();
            _centerOval[i].x = 0.0f;
            _centerOval[i].y = 0.0f;
        }

        for(int i = 0; i < _pointBottomFitFace.length; i++){
            _pointBottomFitFace[i] = new PointF();
            _pointBottomFitFace[i].x = 0.0f;
            _pointBottomFitFace[i].y = 0.0f;
        }
    }

    public void updateWithFaceResults(List<ZZFaceResult> faceResult) {
        count = 0;
        this.faceResult = faceResult;
        for (int j = 0; j < ZZEffectCommon.ZZNumberOfFaceForChange; j++) {
            float time = 0.0f;
            double now = System.currentTimeMillis();
            if(j < faceResult.size() && j < _item.getCount()) {
                ZZFaceResult result = faceResult.get(j);
                _times[j] = _actions.get(j).commonTriggerFace(time, now, result.getFaceStatus());
                _actions.get(j).setFaceStatus(result.getFaceStatus());
                for (int i = 0; i < ZZEffectCommon.ZZNumberOfFacePoints; i++) {
                    int t = j * ZZEffectCommon.ZZNumberOfFacePoints + i;
                    _facePoints[t].x = result.getPoints()[i].x;
                    _facePoints[t].y = -result.getPoints()[i].y;
                }
                if(result.getFaceStatus() != ZZFaceResult.ZZ_FACESTATUS_UNKNOWN) {
                    count++;
                }
            } else {
                for (int i = 0; i < ZZEffectCommon.ZZNumberOfFacePoints; i++) {
                    int t = j * ZZEffectCommon.ZZNumberOfFacePoints + i;
                    _facePoints[t].x = -2.0f;
                    _facePoints[t].y = -2.0f;
                }
                _times[j] = 0.0f;
                _actions.get(j).setFaceStatus(ZZFaceResult.ZZ_FACESTATUS_UNKNOWN);
                _actions.get(j).reset();
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
        UniformUtil2.setFloatArray(_timesUniform, _times);
        UniformUtil2.setInteger(_changeFaceUniform, _changeFace);

        switch (_item.getAction()) {
            case ZZEffectCommon.FaceActionType_FitFace://胖脸
                updateFatFace();
                break;
            default: {
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
            }
            break;
        }

        processCutOut();

        GLES20.glViewport(0, 0, mIntputWidth, mIntputHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
    }

    private void updateLeftFourEye() {
        int leftEyeIndex = 74;

        _leftEye[0] = _facePoints[leftEyeIndex + 0 * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
        _leftEye[4] = _facePoints[leftEyeIndex + 0 * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;
        _leftEye[8] = 0.0f;
        _leftEye[12] = 0.0f;

        _leftEye[1] = _facePoints[leftEyeIndex + 1 * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
        _leftEye[5] = _facePoints[leftEyeIndex + 1 * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;
        _leftEye[9] = 0.0f;
        _leftEye[13] = 0.0f;

        _leftEye[2] = _facePoints[leftEyeIndex + 2 * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
        _leftEye[6] = _facePoints[leftEyeIndex + 2 * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;
        _leftEye[10] = 0.0f;
        _leftEye[14] = 0.0f;

        _leftEye[3] = _facePoints[leftEyeIndex + 3 * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
        _leftEye[7] = _facePoints[leftEyeIndex + 3 * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;
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

        _rightEye[1] = _facePoints[rightEyeIndex + 1 * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
        _rightEye[5] = _facePoints[rightEyeIndex + 1 * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;
        _rightEye[9] = 0.0f;
        _rightEye[13] = 0.0f;

        _rightEye[2] = _facePoints[rightEyeIndex + 2 * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
        _rightEye[6] = _facePoints[rightEyeIndex + 2 * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;
        _rightEye[10] = 0.0f;
        _rightEye[14] = 0.0f;

        _rightEye[3] = _facePoints[rightEyeIndex + 3 * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
        _rightEye[7] = _facePoints[rightEyeIndex + 3 * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;
        _rightEye[11] = 0.0f;
        _rightEye[15] = 0.0f;

        UniformUtil2.setUniformMatrix4fBuffer(_rightFourEyesUniform, _rightEye);
    }

    //更新四张脸眼睛的宽度
    private void updateEyeWidth() {
        int index1 = 55;
        int index2 = 52;
        _widths[0] = (_facePoints[index1 + 0 * ZZEffectCommon.ZZNumberOfFacePoints].x - _facePoints[index2 + 0 * ZZEffectCommon.ZZNumberOfFacePoints].x);
        _widths[1] = (_facePoints[index1 + 1 * ZZEffectCommon.ZZNumberOfFacePoints].x - _facePoints[index2 + 1 * ZZEffectCommon.ZZNumberOfFacePoints].x);
        _widths[2] = (_facePoints[index1 + 2 * ZZEffectCommon.ZZNumberOfFacePoints].x - _facePoints[index2 + 2 * ZZEffectCommon.ZZNumberOfFacePoints].x);;
        _widths[3] = (_facePoints[index1 + 3 * ZZEffectCommon.ZZNumberOfFacePoints].x - _facePoints[index2 + 3 * ZZEffectCommon.ZZNumberOfFacePoints].x);
        UniformUtil2.setFloatVec4(_eyeWidthsUniform, _widths);
    }

    //更新四张脸眼睛高度
    private void updateEyeHeight() {
        int index1 = 73;
        int index2 = 72;
        _heights[0] = (_facePoints[index1 + 0 * ZZEffectCommon.ZZNumberOfFacePoints].y - _facePoints[index2 + 0 * ZZEffectCommon.ZZNumberOfFacePoints].y);
        _heights[1] = (_facePoints[index1 + 1 * ZZEffectCommon.ZZNumberOfFacePoints].y - _facePoints[index2 + 1 * ZZEffectCommon.ZZNumberOfFacePoints].y);
        _heights[2] = (_facePoints[index1 + 2 * ZZEffectCommon.ZZNumberOfFacePoints].y - _facePoints[index2 + 2 * ZZEffectCommon.ZZNumberOfFacePoints].y);
        _heights[3] = (_facePoints[index1 + 3 * ZZEffectCommon.ZZNumberOfFacePoints].y - _facePoints[index2 + 3 * ZZEffectCommon.ZZNumberOfFacePoints].y);
        UniformUtil2.setFloatVec4(_eyeHeightsUniform, _heights);
    }

    private void updateFacePoints() {
        for (int i = 0; i < ZZEffectCommon.ZZNumberOfFaceForChange; i++) {
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
        _widthFace[1] = getDistancePoints((index1 + 1 * ZZEffectCommon.ZZNumberOfFacePoints), (index2 + 1 * ZZEffectCommon.ZZNumberOfFacePoints), _screenRatio);
        _widthFace[2] = getDistancePoints((index1 + 2 * ZZEffectCommon.ZZNumberOfFacePoints), (index2 + 2 * ZZEffectCommon.ZZNumberOfFacePoints), _screenRatio);
        _widthFace[3] = getDistancePoints((index1 + 3 * ZZEffectCommon.ZZNumberOfFacePoints), (index2 + 3 * ZZEffectCommon.ZZNumberOfFacePoints), _screenRatio);
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
        for (int i = 0; i < ZZEffectCommon.ZZNumberOfFaceForChange; i++) {
            //triangleCenter
            _triangleCenter[i].x = _facePoints[centerIndex + i * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
            _triangleCenter[i].y = _facePoints[centerIndex + i * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;
        }
        UniformUtil2.setPoints(_triangleCenterUniform, _triangleCenter);
    }

    private void updateFatFace() {
        PointF philtrum = new PointF();
        PointF ridgeNose = new PointF();
        PointF firstPoint = new PointF();
        PointF thirty_two_point = new PointF();

        for (int i = 0; i < ZZEffectCommon.ZZNumberOfFaceForChange; i++) {
            //_philtrum
            philtrum.x = _facePoints[49 + i * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
            philtrum.y = _facePoints[49 + i * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;

            ridgeNose.x = _facePoints[43 + i * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
            ridgeNose.y = _facePoints[43 + i * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;

            firstPoint.x = _facePoints[0 + i * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
            firstPoint.y = _facePoints[0 + i * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;

            thirty_two_point.x = _facePoints[32 + i * ZZEffectCommon.ZZNumberOfFacePoints].x * 0.5f + 0.5f;
            thirty_two_point.y = _facePoints[32 + i * ZZEffectCommon.ZZNumberOfFacePoints].y * 0.5f + 0.5f;

            float dis = getDistancePoints(0 + i * ZZEffectCommon.ZZNumberOfFacePoints, 32 + i * ZZEffectCommon.ZZNumberOfFacePoints, _screenRatio);

            float firstToThirtyTwoX = thirty_two_point.x - firstPoint.x;
            float firstToThirtyTwoY = thirty_two_point.y - firstPoint.y;

            _pointLeftFitFace[i].x = philtrum.x - firstToThirtyTwoX * 0.36f;
            _pointLeftFitFace[i].y = philtrum.y - firstToThirtyTwoY * 0.36f;

            _pointRightFitFace[i].x = philtrum.x + firstToThirtyTwoX * 0.36f;
            _pointRightFitFace[i].y = philtrum.y + firstToThirtyTwoY * 0.36f;

            _centerOval[i].x = philtrum.x;
            _centerOval[i].y = philtrum.y;

            float xSpan = (philtrum.x - ridgeNose.x);
            float ySpan = (philtrum.y - ridgeNose.y);
            float curLength = (float)Math.sqrt(xSpan * xSpan + ySpan * ySpan);
            _pointBottomFitFace[i].x = philtrum.x + (xSpan / (curLength + 0.0001f)) * dis * 0.15f;
            _pointBottomFitFace[i].y = philtrum.y + (ySpan / (curLength + 0.0001f)) * dis * 0.15f;
        }

        UniformUtil2.setPoints(_pointLeftFitFaceUniform, _pointLeftFitFace);
        UniformUtil2.setPoints(_pointRightFitFaceUniform, _pointRightFitFace);
        UniformUtil2.setPoints(_centerOvalUniform, _centerOval);
        UniformUtil2.setPoints(_pointBottomFitFaceUniform, _pointBottomFitFace);
    }

    private void processCutOut() {
        UniformUtil2.setPoints(_framePointsUniform, _framePoints);
        UniformUtil2.setFloat(_frameCountUniform, _frameCount);
        UniformUtil2.setFloatArray(_frameRotatesUniform, _frameRotates);
        UniformUtil2.setFloatVec4(_fillColorUniform, _fillColor);
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
        _values = null;
        _faceMorphCenterPoint = null;
        _faceMorphP1 = null;
        _faceMorphP3 = null;
        _triangleCenter = null;
        _xiaba = null;
        _pointLeftFitFace = null;
        _pointRightFitFace = null;
        _centerOval = null;
        _pointBottomFitFace = null;
        _times = null;
        _framePoints = null;
        _frameRotates = null;
    }

    public void onDestroy() {
        super.onDestroy();
        destroyFramebuffers();
        dealloc();
    }
}

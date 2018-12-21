package com.douyaim.effect.effectimp;

import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.GLES20;
import com.douyaim.effect.ZZEffectCommon;
import com.douyaim.effect.effectControl.ZZEffectControl_2d;
import com.douyaim.effect.face.ZZFaceResult;
import com.douyaim.effect.model.Matrix3;
import com.douyaim.effect.model.Matrix4;
import com.douyaim.effect.utils.OpenGlUtils;
import com.douyaim.effect.utils.ScreenUtils;
import com.douyaim.effect.utils.UniformUtil2;
import com.douyaim.qsapp.LibApp;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hj on 16/9/5.
 */
public class ZZEffect2DElement_v2 {

    public static final String TAG = ZZEffect2DElement_v2.class.toString();

    FloatBuffer _verticesBuffer;
    FloatBuffer _textureCoordinatesBuffer;

    ZZEffect2DItem_v2 _item;
    int _program;
    int texture;

    int _filterPositionAttribute, _filterTextureCoordinateAttribute;
    int _filterInputTextureUniform, _timeUniform, _faceStatusUniform, _facePointsUniform, _extrasUniform, _actionUniform ,_alphaUniform;
    PointF[] _facePoints;
    float[] _extras = new float[ZZEffectCommon.ZZMaxCountOfShaderExtraArray];
    double _startTs;
    int _faceStatus;
    Map<String, Integer> _faceStatusFourFace;
    float _time;
    float _random;
    boolean _animating;
    float _pitch, _yaw, _roll;
    float _aspectRatio;

    float[] _times;
    double[] _startTimes;
    int[] _animatings;
    int _curFaceIndex;

    ZZEffectControl_2d _control_2d;

    void initWithItem (ZZEffect2DItem_v2 item, FloatBuffer verticesBuffer, FloatBuffer textureCoordinatesBuffer) {
        if(item == null){
            return;
        }
        _item = item;

        _verticesBuffer = verticesBuffer;
        _textureCoordinatesBuffer = textureCoordinatesBuffer;

        generateProgram();

        // 人脸姿态
        _faceStatus = ZZFaceResult.ZZ_FACESTATUS_UNKNOWN;

        // 人脸关键点
        //_facePoints

        // 扩展字段
        if(_item.getExtras() != null && _item.getExtras().length > 0){
            for (int i = 0; i < ZZEffectCommon.ZZMaxCountOfShaderExtraArray; i++) {
                if (i < _item.getExtras().length) {
                    _extras[i] = _item.getExtras()[i];
                } else {
                    _extras[i] = 0.0f;
                }
            }
        }

        // 随机数
        _random = ((int)(Math.random()*100000))/100000.0f;
        // 动画时间
        _time = 0.0f;

        Point point = ScreenUtils.getScreenRealSize(LibApp.getAppContext());
        _aspectRatio = (float)point.x/(float)point.y;

        _times = new float[ZZEffectCommon.ZZNumberOfFaceAndScreen];

        _animatings = new int[ZZEffectCommon.ZZNumberOfFaceAndScreen];

        _startTimes = new double[ZZEffectCommon.ZZNumberOfFaceAndScreen];

        _control_2d = new ZZEffectControl_2d();
        _control_2d.initWithItem(item);

        _curFaceIndex = -1;

        _faceStatusFourFace = new HashMap<>();

        ZZEffectTexCoorItem texCoorItem = ZZEffectTextureManager.getZZEffectTextureManager().getTexCoorByName(_control_2d.getFrameName());
        if(texCoorItem != null){
            texture = ZZEffectTextureManager.getZZEffectTextureManager().getTextureByPath(_item.getDirPath() + texCoorItem.getImageName());
        }
    }

    public void updateWithFaceResult (ZZFaceResult faceResult) {
        if (_curFaceIndex == -1) {
            return;
        }
        if (faceResult.getFaceStatus() != ZZFaceResult.ZZ_FACESTATUS_UNKNOWN) {
            _facePoints = faceResult.getPoints();
            _pitch = faceResult.getPitch();
            _yaw = faceResult.getYaw();
            _roll = faceResult.getRoll();
        } else {
            _facePoints = ZZEffect2DEngine_v2.defaultPoints;
            _pitch = 0f;
            _yaw = 0f;
            _roll = 0f;
        }

        float time = 0.0f;
        double now = System.currentTimeMillis();

        int newStatus = faceResult.getFaceStatus();

        time = commonTrigger(time, now, newStatus);

        if (_item.getIsAction() == 0 && _animatings[_curFaceIndex] == 0) {
            time = 0.0f;
        }
        _times[_curFaceIndex] = time;

        //_faceStatus = newStatus;

        _faceStatusFourFace.put(""+_curFaceIndex, newStatus);

        _control_2d.updateFaceResult(_facePoints, _times[_curFaceIndex], _pitch, _yaw, _roll, _animatings[_curFaceIndex] == 0 ? false : true);
    }

    private float commonTrigger(float t, double now, int newStatus) {
        float time = t;
        if ((newStatus & _item.getEnd()) != 0) { // 满足终止条件
            _startTimes[_curFaceIndex] = 0.0f;
            _animatings[_curFaceIndex] = 0;
        } else if ((newStatus & _item.getStart()) != 0) { // 满足触发条件
            if (_animatings[_curFaceIndex] != 0) { // 正在动画中
                if (_startTimes[_curFaceIndex] == 0.0) {
                    _startTimes[_curFaceIndex] = now;
                }

                time = (float)(now - _startTimes[_curFaceIndex]) / 1000f;
                if (_item.getDuration() > 0.0f) { // 有动画时长限制
                    if (time > _item.getDuration()) { // 超时
                        _animatings[_curFaceIndex] = 0;
                    } else { // 没有超时
                        _animatings[_curFaceIndex] = 1;
                    }
                } else { // 没有动画时长限制
                    _animatings[_curFaceIndex] = 1;
                }
            } else { // 没有在动画中
                int faceStatus = _faceStatusFourFace.get(""+_curFaceIndex) == null ?
                        ZZFaceResult.ZZ_FACESTATUS_UNKNOWN : _faceStatusFourFace.get(""+_curFaceIndex);
                if ((faceStatus & _item.getStart()) != 0) { // 上一帧也满足触发条件
                    if (_startTimes[_curFaceIndex] == 0.0) {
                        _startTimes[_curFaceIndex] = now;
                    }
                    time = (float)(now - _startTimes[_curFaceIndex]) / 1000f ;
                    if (_item.getDuration() > 0.0f) { // 有动画时长限制
                        if (time > _item.getDuration()) { // 超时
                            _animatings[_curFaceIndex] = 0;
                        } else { // 没有超时
                            _animatings[_curFaceIndex] = 1;
                        }
                    } else { // 没有动画时长限制
                        _animatings[_curFaceIndex] = 1;
                    }
                } else { // 上一帧不满足触发条件
                    if(!(_item.getIsAction() == 1 && _item.isTimeNoRepeate())) {
                        _startTimes[_curFaceIndex] = now;
                    }
                    time = (float)(now - _startTimes[_curFaceIndex]) / 1000f;
                    _animatings[_curFaceIndex] = 1;
                }
            }
        } else {
            if (_animatings[_curFaceIndex] != 0) { // 正在动画中
                time = (float)(now - _startTimes[_curFaceIndex]) / 1000f;
                if (_item.getDuration() > 0.0f) { // 有动画时长限制
                    if (time > _item.getDuration()) { // 超时
                        _animatings[_curFaceIndex] = 0;
                    } else { // 没有超时
                        // do nothing
                    }
                } else { // 没有动画时长限制
                    // do nothing
                }
            } else { // 没有在动画中
                if(_item.getIsAction() > 0) {
                    if (_startTimes[_curFaceIndex] == 0.0) {
                        _startTimes[_curFaceIndex] = now;
                    }
                    time = (float)(now - _startTimes[_curFaceIndex]) / 1000f;
                }
            }
        }

        return time;
    }

    void updateWithNoFaceResult() {
        _startTs = 0.0;
        _animating = false;
        _time = 0f;
        _pitch = 0f;
        _yaw = 0f;
        _roll = 0f;
        _facePoints = ZZEffect2DEngine_v2.defaultPoints;

        //当前element对应四张人脸，如果当前element没有对应的人脸数据，则数据重置，且不渲染
        if (_curFaceIndex > -1 && _curFaceIndex < ZZEffectCommon.ZZNumberOfFace) {
            _animatings[_curFaceIndex] = 0;
            _startTimes[_curFaceIndex] = 0.0;
            _times[_curFaceIndex] = 0.0f;
            if(_faceStatusFourFace.containsKey(""+_curFaceIndex)){
                _faceStatusFourFace.put(""+_curFaceIndex, ZZFaceResult.ZZ_FACESTATUS_UNKNOWN);
            }
            _curFaceIndex = -1;
        }

        //_control_2d.updateFaceResult(null, _times[_curFaceIndex], 0.0f, 0.0f, 0.0f, false);
        _control_2d.updateFaceResult(null, 0.0f, 0.0f, 0.0f, 0.0f, false);//TODO:
    }

    private boolean checkCamera(){
        return true;
    }

    void render() {
        if(_program <= 0){
            return;
        }
        if(_curFaceIndex == -1){//表示当前element没有对应的人脸数据，因此不渲染
            return;
        }
        if(!checkCamera()){
            return;
        }

        if (_animatings[_curFaceIndex] == 0 && _item.getIsAction() == 0) {
            return;
        }

        GLES20.glUseProgram(_program);

        //计算UV坐标的矩阵
        Matrix3 texCoorMat = _control_2d.texMatrixWithItemName();
        UniformUtil2.setUniformMatrix3fBuffer(GLES20.glGetUniformLocation(_program, "texCoorMat"), texCoorMat.val);

        Matrix4 tranMat = _control_2d.getTransformMatrix();
        UniformUtil2.setUniformMatrix4fBuffer(GLES20.glGetUniformLocation(_program, "tranMat"), tranMat.getFloatValues());

        if (_item.getIsAction() > 0) {
            UniformUtil2.setInteger(_actionUniform, _animatings[_curFaceIndex]);
        }

        //_alpha
        UniformUtil2.setFloat(_alphaUniform, _control_2d.getAlpha());

        UniformUtil2.setFloat(_timeUniform, _times[_curFaceIndex]);
        int fs = _faceStatusFourFace.get(""+_curFaceIndex) != null ?
                _faceStatusFourFace.get(""+_curFaceIndex) : ZZFaceResult.ZZ_FACESTATUS_UNKNOWN;
        UniformUtil2.setInteger(_faceStatusUniform, fs);

        if(_facePoints != null){
            UniformUtil2.setPoints(_facePointsUniform, _facePoints);
        }
        //人脸旋转
        UniformUtil2.setFloat(GLES20.glGetUniformLocation(_program, "pitch"), _pitch);
        UniformUtil2.setFloat(GLES20.glGetUniformLocation(_program, "yaw"), _yaw);
        UniformUtil2.setFloat(GLES20.glGetUniformLocation(_program, "roll"), _roll);
        //时间
        UniformUtil2.setFloat(GLES20.glGetUniformLocation(_program, "duration"), _item.getDuration());

        UniformUtil2.setFloat(GLES20.glGetUniformLocation(_program, "screenRotate"), 0.0f);//TODO:

        //屏幕宽高比
        UniformUtil2.setFloat(GLES20.glGetUniformLocation(_program, "screenRatio"), _aspectRatio);
        //随机数
        UniformUtil2.setFloat(GLES20.glGetUniformLocation(_program, "random"), _random);
        //扩展字段
        UniformUtil2.setFloatArray(_extrasUniform, _extras);

        _verticesBuffer.position(0);
        GLES20.glVertexAttribPointer(_filterPositionAttribute, 2, GLES20.GL_FLOAT, false, 0, _verticesBuffer);
        GLES20.glEnableVertexAttribArray(_filterPositionAttribute);

        _textureCoordinatesBuffer.position(0);
        GLES20.glVertexAttribPointer(_filterTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0,
                _textureCoordinatesBuffer);
        GLES20. glEnableVertexAttribArray(_filterTextureCoordinateAttribute);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(_filterInputTextureUniform, 4);

        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisableVertexAttribArray(_filterPositionAttribute);
        GLES20.glDisableVertexAttribArray(_filterTextureCoordinateAttribute);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    public void generateProgram() {
        String vshPath = _item.getDirPath() + _item.getVertexName();
        String fshPath = _item.getDirPath() + _item.getFragmentName();
        String vsh = OpenGlUtils.readShaderFromSD(LibApp.getAppContext(), vshPath);
        String fsh = OpenGlUtils.readShaderFromSD(LibApp.getAppContext(), fshPath);
        if(vsh == null || fsh == null){
            return;
        }

        _program = OpenGlUtils.loadProgram(vsh, fsh);
        _filterPositionAttribute = GLES20.glGetAttribLocation(_program, "position");
        _filterTextureCoordinateAttribute = GLES20.glGetAttribLocation(_program, "inputTextureCoordinate");
        _filterInputTextureUniform = GLES20.glGetUniformLocation(_program, "inputImageTexture");
        _timeUniform = GLES20.glGetUniformLocation(_program, "time");
        _faceStatusUniform = GLES20.glGetUniformLocation(_program, "faceStatus");
        _facePointsUniform = GLES20.glGetUniformLocation(_program, "facePoints");
        _extrasUniform = GLES20.glGetUniformLocation(_program, "extra");
        _actionUniform = GLES20.glGetUniformLocation(_program, "isAction");
        _alphaUniform = GLES20.glGetUniformLocation(_program, "alphaFactor");
    }

    void reset() {
        _animating = false;
        _faceStatus = ZZFaceResult.ZZ_FACESTATUS_UNKNOWN;
        _startTs = 0.0;
        _time = 0.0f;
        _facePoints = null;
        _extras = null;
        _verticesBuffer = null;
        _textureCoordinatesBuffer = null;
        GLES20.glDeleteProgram(_program);
        _program = -1;
        _times = null;
        _startTimes = null;
        _animatings = null;
        _curFaceIndex = -1;
        if (_faceStatusFourFace != null) {
            _faceStatusFourFace.clear();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        reset();
    }
}

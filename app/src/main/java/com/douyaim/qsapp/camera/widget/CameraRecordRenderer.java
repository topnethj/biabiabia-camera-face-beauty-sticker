package com.douyaim.qsapp.camera.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import com.douyaim.effect.Filter.GPUImageFilter;
import com.douyaim.effect.Filter.ZZEffectFilter_v2;
import com.douyaim.effect.face.ZZFaceManager_v2;
import com.douyaim.effect.face.ZZFaceResult;
import com.douyaim.effect.model.AndroidSize;
import com.douyaim.effect.utils.Rotation;
import com.douyaim.effect.utils.TextureRotationUtil;
import com.douyaim.qsapp.camera.camerautil.CameraController;
import com.douyaim.qsapp.camera.filter.QSFilterManager;
import com.douyaim.qsapp.camera.gles.FullFrameRect;
import com.douyaim.qsapp.camera.gles.GlUtil;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class CameraRecordRenderer implements GLSurfaceView.Renderer {

    private final Context mApplicationContext;
    private final CameraSurfaceView.CameraHandler mCameraHandler;

    private int mTextureId = GlUtil.NO_TEXTURE;
    private int my_ttid = GlUtil.NO_TEXTURE;
    private int[] tx_ttid = {GlUtil.NO_TEXTURE, GlUtil.NO_TEXTURE, GlUtil.NO_TEXTURE};

    private FullFrameRect mFullScreen;
    private FullFrameRect mFullScreenNormal;
    private FullFrameRect mFullScreenMeiyan;
    private GPUImageFilter txFilter;
    private boolean isMeiYan = false;

    public QSFilterManager.FilterType mCurrentFilterType;
    public String mCurrentConfigPath;

    private SurfaceTexture mSurfaceTexture;
    private final float[] mSTMatrix = new float[16];

    public boolean isTxAction;

    public int mSurfaceWidth, mSurfaceHeight;
    public int mIncomingWidth, mIncomingHeight;
    public int mFrameWidth, mFrameHeight;

    private float mMvpScaleX = 1f, mMvpScaleY = 1f;
    private FloatBuffer gLCubeBuffer, gLTextureBuffer;
    protected ScaleType scaleType = ScaleType.CENTER_CROP;

    private static final String MEIYAN = "Meiyan";

    public CameraRecordRenderer(Context applicationContext, CameraSurfaceView.CameraHandler cameraHandler,
                                int height, int width) {
        mApplicationContext = applicationContext;
        mCameraHandler = cameraHandler;

        gLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        gLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

        gLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        gLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);

        mCurrentFilterType = QSFilterManager.FilterType.Normal;
        isMeiYan = false;

        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Matrix.setIdentityM(mSTMatrix, 0);

        mFullScreen = new FullFrameRect(
                QSFilterManager.getCameraFilter(mCurrentFilterType, mApplicationContext));
        mFullScreenNormal = new FullFrameRect(
                QSFilterManager.getCameraFilter(QSFilterManager.FilterType.Normal, mApplicationContext));
        mFullScreenMeiyan = new FullFrameRect(
                QSFilterManager.getCameraFilter(QSFilterManager.FilterType.Meiyan, mApplicationContext));

        mTextureId = GlUtil.createTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mSurfaceTexture = new SurfaceTexture(mTextureId);

        mCameraHandler.sendMessage(
                mCameraHandler.obtainMessage(CameraSurfaceView.CameraHandler.SETUP_CAMERA, mSurfaceWidth,
                        mSurfaceHeight, mSurfaceTexture));
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (gl != null) {
            gl.glViewport(0, 0, mSurfaceWidth, mSurfaceHeight);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mSurfaceTexture == null) {
            return;
        }

        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mSTMatrix);

        if (mFullScreenNormal.getFilter() != null) {
            mFullScreenNormal.getFilter().extraInit();
        }

        if (mFullScreenMeiyan.getFilter() != null) {
            mFullScreenMeiyan.getFilter().init();
        }

        List<ZZFaceResult> faceResult = ZZFaceManager_v2.getZZFaceManager().getFaceResult();

        if (mCurrentFilterType == QSFilterManager.FilterType.TX) {
            if (isMeiYan) {
                my_ttid = mFullScreenMeiyan.drawTexture(mTextureId, mSTMatrix);
            } else {
                my_ttid = mFullScreenNormal.drawTexture(mTextureId, mSTMatrix);

            }
            tx_ttid = ((ZZEffectFilter_v2) txFilter).onDrawFrame(
                    ZZFaceManager_v2.getZZFaceManager().currentTimeMillis, faceResult, my_ttid);
            ((ZZEffectFilter_v2) txFilter).blend(new int[]{tx_ttid[0], tx_ttid[1], tx_ttid[2]}, null, null);
            action(faceResult);
        }else {
            mFullScreen.drawFrame(mTextureId, mSTMatrix, false);
        }
    }

    public void setCameraPreviewSize(int width, int height) {
        mFrameWidth = width;
        mFrameHeight = height;

        Camera.CameraInfo info = CameraController.getInstance().getCameraInfo();
        if (info.orientation == 90 || info.orientation == 270) {
            mIncomingWidth = height;
            mIncomingHeight = width;
        } else {
            mIncomingWidth = width;
            mIncomingHeight = height;
        }

        float scaleHeight = mSurfaceWidth / (width * 1f / height * 1f);
        float scaleWidth = mSurfaceHeight / (width * 1f / height * 1f);
        float surfaceHeight = mSurfaceHeight;
        float surfaceWidth = mSurfaceWidth;

        if (mFullScreenNormal != null && mFullScreenMeiyan != null) {
//            mMvpScaleX = 1f;
//            mMvpScaleY = scaleHeight / surfaceHeight;
//            mMvpScaleX = sc;
//            mMvpScaleY = 1f;

            float tt = mSurfaceWidth / 9;
            float tH = mSurfaceHeight / tt;
            float tW = mSurfaceWidth / tt;
            float temp = (float) (tH / 16.0);

            mMvpScaleX = 1f;
            mMvpScaleY = 1f;

            mFullScreen.scaleMVPMatrix(temp, 1);
            mFullScreen.getFilter().onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
            mFullScreen.getFilter().setTextureSize(mIncomingWidth, mIncomingHeight);

            mFullScreenNormal.scaleMVPMatrix(temp, 1);
            mFullScreenNormal.getFilter().onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
            mFullScreenNormal.getFilter().setTextureSize(mIncomingWidth, mIncomingHeight);

            mFullScreenMeiyan.scaleMVPMatrix(temp, 1);
            mFullScreenMeiyan.getFilter().onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
            mFullScreenMeiyan.getFilter().setTextureSize(mIncomingWidth, mIncomingHeight);
        }

        boolean isFront = CameraController.getInstance().getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT;
        adjustSize(info.orientation, isFront, false);
    }

    private void adjustSize(int rotation, boolean flipHorizontal, boolean flipVertical) {
        float[] textureCords = TextureRotationUtil.getRotation(Rotation.fromInt(rotation),
                flipHorizontal, flipVertical);
        float[] cube = TextureRotationUtil.CUBE;
        float ratio1 = (float) mSurfaceWidth / mIncomingWidth;
        float ratio2 = (float) mSurfaceHeight / mIncomingHeight;
        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(mIncomingWidth * ratioMax);
        int imageHeightNew = Math.round(mIncomingHeight * ratioMax);

        float ratioWidth = imageWidthNew / (float) mSurfaceWidth;
        float ratioHeight = imageHeightNew / (float) mSurfaceHeight;

        if (scaleType == ScaleType.CENTER_INSIDE) {
            cube = new float[]{
                    TextureRotationUtil.CUBE[0] / ratioHeight, TextureRotationUtil.CUBE[1] / ratioWidth,
                    TextureRotationUtil.CUBE[2] / ratioHeight, TextureRotationUtil.CUBE[3] / ratioWidth,
                    TextureRotationUtil.CUBE[4] / ratioHeight, TextureRotationUtil.CUBE[5] / ratioWidth,
                    TextureRotationUtil.CUBE[6] / ratioHeight, TextureRotationUtil.CUBE[7] / ratioWidth,
            };
        } else if (scaleType == ScaleType.FIT_XY) {
        } else if (scaleType == ScaleType.CENTER_CROP) {
            float distHorizontal = (1 - 1 / ratioWidth) / 2;
            float distVertical = (1 - 1 / ratioHeight) / 2;
            textureCords = new float[]{
                    addDistance(textureCords[0], distVertical), addDistance(textureCords[1], distHorizontal),
                    addDistance(textureCords[2], distVertical), addDistance(textureCords[3], distHorizontal),
                    addDistance(textureCords[4], distVertical), addDistance(textureCords[5], distHorizontal),
                    addDistance(textureCords[6], distVertical), addDistance(textureCords[7], distHorizontal),
            };
        }

        gLCubeBuffer.clear();
        gLCubeBuffer.put(cube).position(0);
        gLTextureBuffer.clear();
        gLTextureBuffer.put(textureCords).position(0);
    }

    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }

    public enum ScaleType {
        CENTER_INSIDE,
        CENTER_CROP,
        FIT_XY
    }

    public void changeC() {
        mCameraHandler.sendMessage(
                mCameraHandler.obtainMessage(CameraSurfaceView.CameraHandler.CHANGE_CAMERA, mSurfaceWidth,
                        mSurfaceHeight, mSurfaceTexture));
    }

    public void notifyPausing() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
    }

    public void changeFilter(String configPath, boolean isTxAction, String actionInfo, boolean isFace) {
        filterDestroyed(false);
        if (!TextUtils.isEmpty(configPath) && !configPath.equals(mCurrentConfigPath)) {
            txFilter = new ZZEffectFilter_v2(configPath, new AndroidSize(mSurfaceWidth, mSurfaceHeight),
                    new AndroidSize(mIncomingWidth, mIncomingHeight), isFace);
            ((ZZEffectFilter_v2) txFilter).install();
            mCurrentFilterType = QSFilterManager.FilterType.TX;
        }else{
            if (!isMeiYan) {
                mCurrentFilterType = QSFilterManager.FilterType.Normal;
            } else {
                mCurrentFilterType = QSFilterManager.FilterType.Meiyan;
            }
            if (mFullScreen != null) {
                mFullScreen.setFilter(QSFilterManager.getCameraFilter(mCurrentFilterType, mApplicationContext));
                mFullScreen.getFilter().onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
                mFullScreen.getFilter().setTextureSize(mIncomingWidth, mIncomingHeight);
                mFullScreen.getFilter().init();
            }
        }
        mCurrentConfigPath = configPath;
        this.isTxAction = isTxAction;

        Message message = mCameraHandler.obtainMessage(
                CameraSurfaceView.CameraHandler.AFTER_INITEFFECT);
        Bundle bundle = new Bundle();
        bundle.putString("actionInfo", actionInfo);
        message.setData(bundle);
        mCameraHandler.sendMessage(message);
    }

    private void action(List<ZZFaceResult> faceResult) {
        if (isTxAction) {
            for (ZZFaceResult result : faceResult) {
                if (result.getFaceStatus() == ZZFaceResult.ZZ_FACESTATUS_MOUTHOPENED ||
                        result.getFaceStatus() == ZZFaceResult.ZZ_FACESTATUS_EYEBROWSRAISED) {
                    mCameraHandler.sendMessage(mCameraHandler.obtainMessage(
                            CameraSurfaceView.CameraHandler.AFTER_INITEFFECT));
                    isTxAction = false;
                    break;
                }
            }
        }
    }

    public void setMeiYan(boolean isMeiYan) {
        this.isMeiYan = isMeiYan;
    }

    public boolean getMeiYan() {
        return this.isMeiYan;
    }

    public void filterDestroyed(boolean all) {
        //ZZFaceManager_v2.getZZFaceManager().reset(false);
        if (txFilter != null) {
            ((ZZEffectFilter_v2) txFilter).uninstall();
            txFilter = null;
        }
        if (mFullScreen != null && all) {
            mFullScreen.release(true);
        }
        if (mFullScreenNormal != null && all) {
            mFullScreenNormal.release(true);
        }
        if (mFullScreenMeiyan != null && all) {
            mFullScreenMeiyan.release(true);
        }
        //mCurrentConfigPath = null;
        this.isTxAction = false;
    }

}

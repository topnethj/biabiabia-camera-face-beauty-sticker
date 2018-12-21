package com.douyaim.qsapp.camera.camerautil;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CameraController implements Camera.AutoFocusCallback,
        Camera.ErrorCallback, CommonHandlerListener {
    private static final String TAG = "CameraController";

    public static final String BROADCAST_ACTION_OPEN_CAMERA_ERROR =
            "CameraController.BROADCAST_ACTION_OPEN_CAMERA_ERROR";

    public static final String TYPE_OPEN_CAMERA_ERROR_TYPE =
            "CameraController.TYPE_OPEN_CAMERA_ERROR_TYPE";

    public static final int TYPE_OPEN_CAMERA_ERROR_UNKNOWN = 0;
    public static final int TYPE_OPEN_CAMERA_ERROR_PERMISSION_DISABLE = 1;
    private static final int DEFAULT_PIXEL_FORMAT = ImageFormat.NV21;

    private static volatile CameraController sInstance;

    public final static float sCameraRatio = 16f / 9f;
    private final CameraControllerHandler mHandler;

    public Camera mCamera = null;
    public Camera.CameraInfo mCameraInfo;
    public Camera.Size mCameraPictureSize;
    public Camera.Size mCameraPreviewSize;
    private PixelFormat mPixelFormat = new PixelFormat();
    private Camera.PreviewCallback previewCallback;

    //cameraID
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    //保存大视频cameraIndex
    private int mCameraIndexBig = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private boolean mAutoFocusLocked = false;
    private boolean mIsSupportAutoFocus = false;
    private boolean mIsSupportAutoFocusContinuousPicture = false;
    private boolean mIsSupportFontFacingCamera = false;

    private final Object mLock = new Object();

    SoftReference<SurfaceTexture> mSurfaceTexture;
    SoftReference<Context> mContext;

    int desiredPictureWidth2;

    private CameraPictureSizeComparator mCameraPictureSizeComparator = new CameraPictureSizeComparator();

    private CameraController() {
        mHandler = new CameraControllerHandler(this);
    }

    public static CameraController getInstance() {
        if (sInstance == null) {
            synchronized (CameraController.class) {
                if (sInstance == null) {
                    sInstance = new CameraController();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化相机
     *
     * @param surfaceTexture
     * @param context
     * @param desiredPictureWidth
     */
    public void setupCamera(SurfaceTexture surfaceTexture, Context context,
                            int desiredPictureWidth) {
        synchronized (mLock) {
            try {
                mContext = new SoftReference<Context>(context);
                mSurfaceTexture = new SoftReference<SurfaceTexture>(surfaceTexture);

                desiredPictureWidth2 = desiredPictureWidth;

                if (mCamera != null) {
                    release();
                }

                if (Camera.getNumberOfCameras() > 1) {
                    mCameraId = mCameraIndexBig;
                    if (mCamera == null) {
                        mCamera = Camera.open(mCameraId);
                    }
                } else {
                    mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    mCamera = Camera.open(mCameraId);
                }

                //对lg nexus 5x的适配
//                Build build = new Build();
                String model = Build.MODEL;
                if (model.equals("Nexus 5X")) {
                    if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        mCamera.setDisplayOrientation(270);
                    } else {
                        mCamera.setDisplayOrientation(90);
                    }
                } else {
                    mCamera.setDisplayOrientation(90);
                }

                mCamera.setPreviewTexture(mSurfaceTexture.get());

                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(mCameraId, cameraInfo);
                this.mCameraInfo = cameraInfo;
            } catch (Exception e) {
                e.printStackTrace();
                release();
            }

            if (mCamera == null) {
                return;
            }

            try {//尝试寻找最接近期望的预览分辨率
                findCameraSupportValue(desiredPictureWidth);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取相机信息
     *
     * @return
     */
    public Camera.CameraInfo getCameraInfo() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        if (Camera.getNumberOfCameras() > 1) {
            Camera.getCameraInfo(mCameraId, cameraInfo);
        } else {
            Camera.getCameraInfo(0, cameraInfo);
        }
        return cameraInfo;
    }

    /**
     * 判断当前摄像头的方向（前置／后置）
     *
     * @return
     */
    public int getCameraFacing() {
        return getCameraInfo().facing;
    }

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        this.previewCallback = previewCallback;
    }

    public Camera.Size getmCameraPreviewSize() {
        return mCameraPreviewSize;
    }

    /**
     * 配置相机参数
     *
     * @param previewSize
     */
    public void configureCameraParameters(Camera.Size previewSize) {
        try {
            Camera.Parameters cp = getCameraParameters();
            if (cp == null || mCamera == null) {
                return;
            }
            synchronized (mLock) {
                // 对焦模式
                List<String> focusModes = cp.getSupportedFocusModes();
                if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    mIsSupportAutoFocusContinuousPicture = true;
                    cp.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 自动连续对焦
                } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    mIsSupportAutoFocus = true;
                    cp.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);// 自动对焦
                } else {
                    mIsSupportAutoFocusContinuousPicture = false;
                    mIsSupportAutoFocus = false;
                }
                PixelFormat.getPixelFormatInfo(DEFAULT_PIXEL_FORMAT, mPixelFormat);
                mCameraPreviewSize = previewSize;
                // 预览尺寸
                if (previewSize != null) {
                    cp.setPreviewSize(previewSize.width, previewSize.height);
                }
                cp.setPreviewFormat(DEFAULT_PIXEL_FORMAT);

                //拍照尺寸
                cp.setPictureSize(mCameraPictureSize.width, mCameraPictureSize.height);

                mCamera.setParameters(cp);
                mCamera.setErrorCallback(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAutoFocusLocked = false;
    }

    /**
     * 设置相机焦距索引
     *
     * @param targetZoom [0,Camera.Parameters.getMaxZoom()]
     */
    public void setZoom(int targetZoom) {
        if (mCamera != null) {
            synchronized (mLock) {
                try {
                    Camera.Parameters mParams = mCamera.getParameters();
                    if (!mParams.isZoomSupported()) {
                        return;
                    }
                    int maxZoom = mParams.getMaxZoom();
                    targetZoom = (targetZoom < 0) ? 0 : ((targetZoom > maxZoom) ? maxZoom : targetZoom);

                    int current = mParams.getZoom();
                    if (current == targetZoom) {
                        return;
                    }
                    if (mParams.isSmoothZoomSupported()) {
                        mCamera.startSmoothZoom(targetZoom);
                    } else {
                        mParams.setZoom(targetZoom);
                        mCamera.setParameters(mParams);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 获取当前的焦距索引
     *
     * @return [0, Camera.Parameters.getMaxZoom()]
     */
    public int getCurrentZoom() {
        Camera.Parameters cameraParameters = getCameraParameters();
        if (cameraParameters != null) {
            return cameraParameters.getZoom();
        }
        return 0;
    }

    public int getMaxZoom() {
        Camera.Parameters cameraParameters = getCameraParameters();
        if (cameraParameters != null) {
            return cameraParameters.getMaxZoom();
        }
        return 0;
    }

    public void addCallbackBuffer(byte[] buff) {
        if (mCamera != null) {
            synchronized (mLock) {
                if (mCamera != null) {
                    try {
                        mCamera.addCallbackBuffer(buff);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 开始预览
     *
     * @return
     */
    public void startCameraPreview() {
        if (mCamera != null) {
            synchronized (mLock) {
                if (mCamera != null) {
                    try {
                        if (previewCallback != null) {
//                            mCamera.setPreviewCallback(previewCallback);
                            int bufsize = mCameraPreviewSize.width * mCameraPreviewSize.height * mPixelFormat.bitsPerPixel / 8;
                            byte[] buff = null;
                            for (int i = 0; i < 3; i++) {
                                buff = new byte[bufsize];
                                addCallbackBuffer(buff);
                            }
                            mCamera.setPreviewCallbackWithBuffer(previewCallback);
                        } else {
                            mCamera.setPreviewCallback(null);
                        }
                        if (mIsSupportAutoFocusContinuousPicture) {
                            mCamera.cancelAutoFocus();
                        }
                        mCamera.startPreview();
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            }
        }
    }

    /**
     * 停止预览
     *
     * @return
     */
    public boolean stopCameraPreview() {
        if (mCamera != null) {
            synchronized (mLock) {
                if (mCamera != null) {
                    try {
                        mCamera.setPreviewCallback(null);
                        mCamera.stopPreview();
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return false;
    }

    /**
     * 释放相机资源
     */
    public void release() {
        if (mCamera != null) {
            synchronized (mLock) {
                if (mCamera != null) {
                    try {
                        mCamera.setPreviewCallback(null);
                        mCamera.stopPreview();
                        mCamera.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mCamera = null;
                    }
                }
            }
        }
    }

    /**
     * 切换前后摄像头
     */
    public void changeCamera() {
        if (mCameraIndexBig == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mCameraIndexBig = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else {
            mCameraIndexBig = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        setupCamera(mSurfaceTexture.get(), mContext.get(), desiredPictureWidth2);
    }

    /**
     * 打开闪光灯
     */
    public void openLight() {
        if (mCamera == null) {
            return;
        }
        try {
            final Camera.Parameters p = mCamera.getParameters();
            if (p.getFlashMode() != null) {
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭闪光灯
     */
    public void closeLight() {
        if (mCamera == null) {
            return;
        }
        try {
            final Camera.Parameters p = mCamera.getParameters();
            if (p.getFlashMode() != null) {
                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取闪光灯状态
     */
    public boolean getLightStatus() {
        if (mCamera == null) {
            return false;
        }
        try {
            final Camera.Parameters p = mCamera.getParameters();
            if (p.getFlashMode() != null) {
                return p.getFlashMode().equals(Camera.Parameters.FLASH_MODE_TORCH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 开始自动对焦
     *
     * @param autoFocusCallback
     * @return
     */
    public boolean startAutoFocus(Camera.AutoFocusCallback autoFocusCallback) {
        if ((mIsSupportAutoFocus || mIsSupportAutoFocusContinuousPicture) && mCamera != null) {
            try {

                String focusMode = getCameraParameters().getFocusMode();

                if (!TextUtils.isEmpty(focusMode) && focusMode.
                        equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {  // 如果是连续自动对焦, 来一次对焦处理
                    mCamera.autoFocus(autoFocusCallback);
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        return false;
    }

    /**
     * 获取当前相机参数
     *
     * @return
     */
    public Camera.Parameters getCameraParameters() {
        if (mCamera != null) {
            synchronized (mLock) {
                if (mCamera != null) {
                    try {
                        return mCamera.getParameters();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

    /**
     * 获取相机支持的显示分辨率
     *
     * @param desiredWidth
     */
    private void findCameraSupportValue(int desiredWidth) {
        Camera.Parameters cp = getCameraParameters();
        List<Camera.Size> cs = cp.getSupportedPictureSizes();
        if (cs != null && !cs.isEmpty()) {
            Collections.sort(cs, mCameraPictureSizeComparator);
            for (Camera.Size size : cs) {
                if (size.width < desiredWidth && size.height < desiredWidth) {
                    break;
                }
                float ratio = (float) size.width / size.height;
                if (ratio == sCameraRatio) {
                    mCameraPictureSize = size;
                }
            }
        }
    }

    /**
     * 触摸时自动对焦
     *
     * @param v
     * @param event
     */
    public void startTouchAutoFocus(View v, MotionEvent event) {
        if ((mIsSupportAutoFocus || mIsSupportAutoFocusContinuousPicture)
                && mCamera != null
                && !mAutoFocusLocked) {
            try {
                mAutoFocusLocked = true;

                Camera.Parameters parameters = getCameraParameters();
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                if (parameters.getMaxNumFocusAreas() > 0) {
                    Rect focusRect =
                            CameraHelper.calculateTapArea(v, event.getX(), event.getY(), 1f);
                    List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
                    focusAreas.add(new Camera.Area(focusRect, 1000));
                    parameters.setFocusAreas(focusAreas);
                }

                if (parameters.getMaxNumMeteringAreas() > 0) {
                    Rect meteringRect =
                            CameraHelper.calculateTapArea(v, event.getX(), event.getY(), 1.5f);
                    List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                    meteringAreas.add(new Camera.Area(meteringRect, 1000));
                    parameters.setMeteringAreas(meteringAreas);
                }

                mCamera.setParameters(parameters);
                mCamera.autoFocus(this);
            } catch (Exception e) {
                e.printStackTrace();
                mAutoFocusLocked = false;
            }
        }
    }

    /**
     * 拍照
     *
     * @param shutter
     * @param raw
     * @param jpeg
     */
    public void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw,
                            Camera.PictureCallback jpeg) {
        if (mCamera != null) {
            mCamera.takePicture(shutter, raw, jpeg);
        }
    }

    public boolean checkSupportFontFacingCamera() {
        try {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            int cameraCount = Camera.getNumberOfCameras();
            for (int i = 0; i < cameraCount; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    mIsSupportFontFacingCamera = true;
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //////////////////// implements ////////////////////

    //AutoFocusCallback
    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        mHandler.sendEmptyMessageDelayed(RESET_TOUCH_FOCUS, RESET_TOUCH_FOCUS_DELAY);
        mAutoFocusLocked = false;
    }

    //ErrorCallback
    @Override
    public void onError(int error, Camera camera) {
    }

    //PictureCallback
    //@Override public void onPictureTaken(byte[] data, Camera camera) {
    //    mIsTakingPicture = false;
    //    //try {
    //    //    Camera.Parameters ps = camera.getParameters();
    //    //    if (ps.getPictureFormat() == ImageFormat.JPEG) {
    //    //        //CommonUtil.executeAsyncTask(new SquareBitmapTask(data, mCameraMirrored) {
    //    //        //    @Override protected void onPostExecute(PublishBean newPost) {
    //    //        //        super.onPostExecute(newPost);
    //    //        //        mIsTakingPicture = false;
    //    //        //        if (mPictureCallback != null) {
    //    //        //            mPictureCallback.onPictureTaken(newPost);
    //    //        //        }
    //    //        //    }
    //    //        //});
    //    //    }
    //    //} catch (Exception e) {
    //    //    e.printStackTrace();
    //    //}
    //}

    //public boolean onClickEvent(View v, MotionEvent event) {
    //    if (mClickGestureDetector.onTouchEvent(event)) {
    //        L.e("onClickEvent", "onClickEvent 进入了 onSingleTapUp");
    //        startTouchAutoFocus(v, event);
    //        return true;
    //    }
    //
    //    return false;
    //}

    //////////////////// Getter & Setter ////////////////////

    public Camera getCamera() {
        return mCamera;
    }

    public int getCameraId() {
        return mCameraId;
    }

    public void setCameraIndex(int cameraId) {
        this.mCameraId = cameraId;
    }

    public boolean isSupportFontFacingCamera() {
        return mIsSupportFontFacingCamera;
    }

    private static final int RESET_TOUCH_FOCUS = 301;
    private static final int RESET_TOUCH_FOCUS_DELAY = 3000;

    /**
     * 相机控制消息管理器
     */
    private static class CameraControllerHandler extends Handler {
        private CommonHandlerListener listener;

        public CameraControllerHandler(CommonHandlerListener listener) {
            super(Looper.getMainLooper());
            this.listener = listener;
        }

        @Override
        public void handleMessage(Message msg) {
            listener.handleMessage(msg);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case RESET_TOUCH_FOCUS:// 触摸式自动对焦
                if (mCamera == null || mAutoFocusLocked) {
                    return;
                }
                mHandler.removeMessages(RESET_TOUCH_FOCUS);
                try {
                    if (mIsSupportAutoFocusContinuousPicture) {
                        Camera.Parameters cp = getCameraParameters();
                        if (cp != null) {
                            cp.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                            mCamera.setParameters(cp);
                        }
                    }
                    mCamera.cancelAutoFocus();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }
}

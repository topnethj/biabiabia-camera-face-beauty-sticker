package com.biabiabia.app.camera.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.biabiabia.app.camera.camerautil.CameraController;
import com.biabiabia.app.camera.camerautil.CameraHelper;
import com.biabiabia.app.camera.camerautil.CommonHandlerListener;
import com.biabiabia.app.camera.filter.QSFilterManager;

public class CameraSurfaceView extends GLSurfaceView
        implements CommonHandlerListener, SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = "CameraSurfaceView";
    private CameraHandler mBackgroundHandler;
    private HandlerThread mHandlerThread;
    private CameraRecordRenderer mCameraRenderer;
    private Context context;
    private Handler mHandler;

    private Camera.PreviewCallback previewCallback;
    public android.graphics.Matrix matrix = new android.graphics.Matrix();

    public CameraSurfaceView(Context context) {
        super(context);
        this.context = context;
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void init(Context context, int height, int width) {
        setEGLContextClientVersion(2);
        //setEGLConfigChooser(true);

        mHandlerThread = new HandlerThread("CameraHandlerThread");
        mHandlerThread.start();

        mBackgroundHandler = new CameraHandler(mHandlerThread.getLooper(), this);
        mCameraRenderer = new CameraRecordRenderer(context.getApplicationContext(), mBackgroundHandler, height, width);

        setRenderer(mCameraRenderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
    }

    public CameraRecordRenderer getRenderer() {
        return mCameraRenderer;
    }

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        this.previewCallback = previewCallback;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        mBackgroundHandler.removeCallbacksAndMessages(null);
        CameraController.getInstance().stopCameraPreview();
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCameraRenderer.notifyPausing();
                synchronized (CameraSurfaceView.class) {
                    mCameraRenderer.filterDestroyed(true);
                }
            }
        });
        requestRender();
        super.onPause();
    }

    public void onDestroy() {
        CameraController.getInstance().release();
        mBackgroundHandler.removeCallbacksAndMessages(null);
        if (!mHandlerThread.isInterrupted()) {
            try {
                mHandlerThread.quit();
                mHandlerThread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void changeFilter(final String configPath, final boolean isTxAction, final String actionInfo, final boolean isFace) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                synchronized (CameraSurfaceView.class) {
                    mCameraRenderer.changeFilter(configPath, isTxAction, actionInfo, isFace);
                }
            }
        });
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }

    public static class CameraHandler extends Handler {
        public static final int SETUP_CAMERA = 1001;
        public static final int CONFIGURE_CAMERA = 1002;
        public static final int START_CAMERA_PREVIEW = 1003;
        //public static final int STOP_CAMERA_PREVIEW = 1004;
        public static final int CHANGE_CAMERA = 1009;
        public static final int AFTER_INITEFFECT = 1100;
        public static final int TRACK_FACE_ING = 1101;
        public static final int WHEN_EFFECTSCROLL = 1102;
        public static final int CHANGE_FILTER = 1103;

        private CommonHandlerListener listener;

        public CameraHandler(Looper looper, CommonHandlerListener listener) {
            super(looper);
            this.listener = listener;
        }

        @Override
        public void handleMessage(Message msg) {
            listener.handleMessage(msg);
        }
    }

    @Override
    public void handleMessage(final Message msg) {
        switch (msg.what) {
            case CameraHandler.SETUP_CAMERA: {
                final int width = msg.arg1;
                final int height = msg.arg2;
                final SurfaceTexture surfaceTexture = (SurfaceTexture) msg.obj;
                if (surfaceTexture == null) {
                    break;
                }
                surfaceTexture.setOnFrameAvailableListener(this);
                mBackgroundHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        CameraController.getInstance().setPreviewCallback(previewCallback);
                        CameraController.getInstance().setupCamera(surfaceTexture, getContext().getApplicationContext(), width);
                        mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(
                                CameraSurfaceView.CameraHandler.CONFIGURE_CAMERA, width, height));
                    }
                });
            }
            break;
            case CameraHandler.CONFIGURE_CAMERA: {
                final int width = msg.arg1;
                final int height = msg.arg2;
                Camera.Size previewSize = CameraHelper.getOptimalPreviewSize(
                        CameraController.getInstance().getCameraParameters(),
                        CameraController.getInstance().getCameraInfo(), width, height, false);

                CameraController.getInstance().configureCameraParameters(previewSize);
                if (previewSize != null) {
                    mCameraRenderer.setCameraPreviewSize(previewSize.width, previewSize.height);
                    matrix.setScale(width / (float) previewSize.height, height / (float) previewSize.width);
                }
                mBackgroundHandler.sendEmptyMessage(CameraHandler.START_CAMERA_PREVIEW);
            }
            break;
            case CameraHandler.START_CAMERA_PREVIEW:
                CameraController.getInstance().startCameraPreview();
                break;
            case CameraHandler.CHANGE_CAMERA:
                final int width = msg.arg1;
                final int height = msg.arg2;
                if (msg.obj == null) {
                    return;
                }
                final SurfaceTexture surfaceTexture = (SurfaceTexture) msg.obj;
                surfaceTexture.setOnFrameAvailableListener(this);
                mBackgroundHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        CameraController.getInstance().setPreviewCallback(previewCallback);
                        CameraController.getInstance().changeCamera();
                        mBackgroundHandler.sendMessage(mBackgroundHandler.obtainMessage(
                                CameraSurfaceView.CameraHandler.CONFIGURE_CAMERA, width, height));
                    }
                });
                break;
            case CameraHandler.CHANGE_FILTER:
                break;
            case CameraHandler.AFTER_INITEFFECT:
                if (mHandler != null) {
                    Message message = mBackgroundHandler.obtainMessage(
                            CameraSurfaceView.CameraHandler.AFTER_INITEFFECT);
                    message.setData(msg.getData());
                    mHandler.sendMessage(message);
                }
                break;
            case CameraHandler.TRACK_FACE_ING:
                if (mHandler != null) {
                    mHandler.sendMessage(mBackgroundHandler.obtainMessage(CameraHandler.TRACK_FACE_ING, msg.obj));
                }
                break;
            default:
                break;
        }
    }

    public void change() {
        mCameraRenderer.changeC();
    }

    public int getSurfaceWidth() {
        if (mCameraRenderer != null) {
            return mCameraRenderer.mSurfaceWidth;
        } else {
            return 0;
        }
    }

    public int getSurfaceHeight() {
        if (mCameraRenderer != null) {
            return mCameraRenderer.mSurfaceHeight;
        } else {
            return 0;
        }
    }

    public int getIncomingWidth() {
        if (mCameraRenderer != null) {
            return mCameraRenderer.mIncomingWidth;
        } else {
            return 0;
        }
    }

    public int getIncomingHeight() {
        if (mCameraRenderer != null) {
            return mCameraRenderer.mIncomingHeight;
        } else {
            return 0;
        }
    }

    public int getFrameWidth() {
        if (mCameraRenderer != null) {
            return mCameraRenderer.mFrameWidth;
        } else {
            return 0;
        }
    }

    public int getFrameHeight() {
        if (mCameraRenderer != null) {
            return mCameraRenderer.mFrameHeight;
        } else {
            return 0;
        }
    }

    public void setMeiYan(boolean isMeiYan) {
        if(mCameraRenderer != null){
            mCameraRenderer.setMeiYan(isMeiYan);
            if(mCameraRenderer.mCurrentFilterType != QSFilterManager.FilterType.TX){
                changeFilter(null, false, "", false);
            }
        }
    }

    public boolean getMeiYan() {
        return mCameraRenderer.getMeiYan();
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
    }

}
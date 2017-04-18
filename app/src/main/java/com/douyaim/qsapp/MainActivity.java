package com.douyaim.qsapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.douyaim.effect.ZZEffectCommon;
import com.douyaim.effect.face.ZZFaceManager_v2;
import com.douyaim.effect.face.ZZFaceResult;
import com.douyaim.qsapp.camera.camerautil.CameraController;
import com.douyaim.qsapp.camera.widget.CameraSurfaceView;
import com.multitrack106.Accelerometer;
import com.sensetime.stmobileapi.AuthCallback;
import com.sensetime.stmobileapi.STMobile106;
import com.sensetime.stmobileapi.STMobileFaceAction;
import com.sensetime.stmobileapi.STMobileMultiTrack106;
import com.sensetime.stmobileapi.STUtils;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private static final int ST_MOBILE_TRACKING_MULTI_THREAD = 0x00000000; ///< 多线程，功耗较多，卡顿较少
    private static final int ST_MOBILE_TRACKING_SINGLE_THREAD = 0x00010000;  ///< 单线程，功耗较少，对于性能弱的手机，会偶尔有卡顿现象
    private static final int ST_MOBILE_TRACKING_ENABLE_DEBOUNCE = 0x00000010; ///< 打开去抖动
    private static final int ST_MOBILE_TRACKING_ENABLE_FACE_ACTION = 0x00000020; ///< 检测脸部动作：张嘴、眨眼、抬眉、点头、摇头
    private static final int ST_MOBILE_TRACKING_DEFAULT_CONFIG = ST_MOBILE_TRACKING_MULTI_THREAD | ST_MOBILE_TRACKING_ENABLE_DEBOUNCE;

    @BindView(R.id.specific_tip)
    protected TextView specificTip;

    @BindView(R.id.tv_loadTx)
    protected TextView loadTx;

    @BindView(R.id.btn_switch_meiyan)
    protected View isMeiYanView;

    @BindView(R.id.btn_switch_front)
    protected ImageView btnSwitchFront;

    @BindView(R.id.btn_switch_flash)
    protected View btnSwitchSplash;

    @BindView(R.id.camera)
    protected CameraSurfaceView mCameraSurfaceView;

    private Unbinder unbinder;

    //绘制人脸点
    @BindView(R.id.surfaceViewOverlap)
    protected SurfaceView mOverlap;
    private Matrix matrix = new Matrix();
    private Paint mPaint;
    private boolean isFirst = true;

    //人脸识别
    private static final int MESSAGE_DRAW_POINTS = 999;
    public static int fps;
    private boolean isTrackerPaused = false;
    private Object lockObj = new Object();
    private STMobileMultiTrack106 tracker = null;
    private HandlerThread trackerHandlerThread;
    private Handler trackerHandler;
    private byte[] nv21;
    private byte[] tmp;
    private Accelerometer acc;
    private List<Long> timeCounter;
    private int timeStart = 0;

    private int realWidth;
    private int realHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        initCamera();

        acc = new Accelerometer(this.getApplicationContext());

        timeCounter = new ArrayList<>();
        trackerHandlerThread = new HandlerThread("DrawFacePointsThread");
        trackerHandlerThread.start();
        trackerHandler = new Handler(trackerHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DRAW_POINTS) {
                    synchronized (lockObj) {
                        if (!isTrackerPaused) {
                            faceDetect();
                        }
                    }
                }
            }
        };

        mOverlap.setZOrderOnTop(true);
        mOverlap.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mPaint = new Paint();
        mPaint.setColor(Color.rgb(57, 138, 243));
        //int strokeWidth = Math.max(PREVIEW_HEIGHT / 240, 2);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.FILL);
    }

    private void initCamera() {
        Display display = this.getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getRealSize(outSize);
        realWidth = outSize.x;
        realHeight = outSize.y;

        ViewGroup.LayoutParams params = mCameraSurfaceView.getLayoutParams();
        params.height = realHeight;
        params.width = realWidth;
        mCameraSurfaceView.setLayoutParams(params);

        if (Camera.getNumberOfCameras() > 1) {
            if (CameraController.getInstance().getCameraFacing() == Camera.CameraInfo.CAMERA_FACING_BACK) {
                btnSwitchFront.setSelected(true);
            } else {
                btnSwitchFront.setSelected(false);
            }
        } else {
            btnSwitchFront.setVisibility(View.INVISIBLE);
        }

        isMeiYanView.setSelected(false);
        mCameraSurfaceView.setPreviewCallback(new MyCameraPreviewCallBack(this));
        mCameraSurfaceView.setHandler(new MyHandler());
        mCameraSurfaceView.init(this, realHeight, realWidth);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraSurfaceView.onResume();
        specificTip.setText("");

        //启动人脸识别
        isTrackerPaused = false;
        if (timeCounter != null) {
            timeCounter.clear();
            timeStart = 0;
        }
        if (acc != null) {
            acc.start();
        }
        if (tracker == null) {
            //long start_init = System.currentTimeMillis();
            AuthCallback authCallback = new AuthCallback() {
                @Override
                public void onAuthResult(boolean succeed, String errMessage) {
                }
            };
            int config = ST_MOBILE_TRACKING_DEFAULT_CONFIG;
            tracker = new STMobileMultiTrack106(this, config, authCallback);
            int max = ZZEffectCommon.ZZNumberOfFace;
            tracker.setMaxDetectableFaces(max);
            //long end_init = System.currentTimeMillis();
            //L.i("track106", "init cost " + (end_init - start_init) + " ms");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mCameraSurfaceView.onPause();

        isTrackerPaused = true;
        trackerHandler.removeMessages(MESSAGE_DRAW_POINTS);
        if (acc != null) {
            acc.stop();
        }
        synchronized (lockObj) {
            if (tracker != null) {
                tracker.destory();
                tracker = null;
            }
        }
    }

    private static class MyCameraPreviewCallBack implements Camera.PreviewCallback {
        SoftReference<MainActivity> mSoftReference;

        public MyCameraPreviewCallBack(MainActivity activity) {
            this.mSoftReference = new SoftReference<>(activity);
        }

        MainActivity getMA() {
            if (mSoftReference != null) {
                return mSoftReference.get();
            } else {
                return null;
            }
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            CameraController.getInstance().addCallbackBuffer(data);
            if (getMA() == null || data == null) {
                return;
            }
            if (!ZZFaceManager_v2.getZZFaceManager().canTrack) {
                return;
            }
            int l = getMA().mCameraSurfaceView.getFrameWidth() * getMA().mCameraSurfaceView.getFrameHeight() * 2;
            if (getMA().nv21 == null) {
                if (l >= data.length) {
                    getMA().nv21 = new byte[l];
                } else {
                    getMA().nv21 = new byte[data.length];
                }
            } else {
                if (getMA().nv21.length < data.length) {
                    getMA().nv21 = new byte[data.length];
                }
            }
            if (getMA().tmp == null) {
                if (l >= data.length) {
                    getMA().tmp = new byte[l];
                } else {
                    getMA().tmp = new byte[data.length];
                }
            } else {
                if (getMA().tmp.length < data.length) {
                    getMA().tmp = new byte[data.length];
                }
            }
            synchronized (getMA().nv21) {
                System.arraycopy(data, 0, getMA().nv21, 0, data.length);
            }

            if(getMA().isFirst){
                getMA().matrix.setScale(getMA().mCameraSurfaceView.getSurfaceWidth() / (float) getMA().mCameraSurfaceView.getFrameHeight(),
                        getMA().mCameraSurfaceView.getSurfaceHeight() / (float) getMA().mCameraSurfaceView.getFrameWidth());
                getMA().isFirst = false;
            }

            getMA().trackerHandler.removeMessages(MESSAGE_DRAW_POINTS);
            getMA().trackerHandler.sendEmptyMessage(MESSAGE_DRAW_POINTS);
        }
    }

    //人脸识别
    private void faceDetect() {
        if (!ZZFaceManager_v2.getZZFaceManager().canTrack) {
            return;
        }
        synchronized (nv21) {
            System.arraycopy(nv21, 0, tmp, 0, nv21.length);
        }

        boolean frontCamera = CameraController.getInstance().getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT;
        Camera.CameraInfo info = CameraController.getInstance().mCameraInfo;

        /**
         * 获取重力传感器返回 方向
         */
        int dir = Accelerometer.getDirection();

        //在使用后置摄像头，且传感器方向为0或2时，后置摄像头与前置orentation相反
        if (!frontCamera && dir == 0) {
            dir = 2;
        } else if (!frontCamera && dir == 2) {
            dir = 0;
        }

        /**
         * 请注意前置摄像头与后置摄像头旋转定义不同
         * 请注意不同手机摄像头旋转定义不同
         */
        if (((info.orientation == 270 && (dir & 1) == 1) ||
                (info.orientation == 90 && (dir & 1) == 0))) {
            dir = (dir ^ 2);
        }

        /**
         * 调 实时人脸检测函数，返回当前人脸信息
         */
        //long start_track = System.currentTimeMillis();
        STMobileFaceAction[] faceActions = tracker.trackFaceAction(tmp, dir, mCameraSurfaceView.getFrameWidth(), mCameraSurfaceView.getFrameHeight());
        //long end_track = System.currentTimeMillis();
        //L.i("track106", "track cost " + (end_track - start_track) + " ms");
        /*
        long timer = System.currentTimeMillis();
        timeCounter.add(timer);
        while (timeStart < timeCounter.size()
                && timeCounter.get(timeStart) < timer - 1000) {
            timeStart++;
        }
        fps = timeCounter.size() - timeStart;
        //L.i("track106", "fps " + fps);
        if (timeStart > 100) {
            timeCounter = timeCounter.subList(timeStart, timeCounter.size() - 1);
            timeStart = 0;
        }*/

        if (faceActions != null) {
            if (!mOverlap.getHolder().getSurface().isValid()) {
                return;
            }
            Canvas canvas = mOverlap.getHolder().lockCanvas();
            if (canvas == null){
                return;
            }
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            canvas.setMatrix(matrix);

            boolean rotate270 = info.orientation == 270;
            List<ZZFaceResult> faceResults = new ArrayList<>();
            for (int j = 0; j < faceActions.length; j++) {
                STMobile106 r = faceActions[j].getFace();

                Rect rect;
                if (rotate270) {
                    rect = STUtils.RotateDeg270(r.getFaceRect(), mCameraSurfaceView.getFrameWidth(), mCameraSurfaceView.getFrameHeight());
                } else {
                    rect = STUtils.RotateDeg90(r.getFaceRect(), mCameraSurfaceView.getFrameWidth(), mCameraSurfaceView.getFrameHeight());
                }

                PointF[] points = r.getPointsArray();
                for (int i = 0; i < points.length; i++) {
                    if (rotate270) {
                        points[i] = STUtils.RotateDeg270(points[i], mCameraSurfaceView.getFrameWidth(), mCameraSurfaceView.getFrameHeight());
                    } else {
                        points[i] = STUtils.RotateDeg90(points[i], mCameraSurfaceView.getFrameWidth(), mCameraSurfaceView.getFrameHeight());
                    }
                }

                STUtils.drawFaceRect(canvas, rect, mCameraSurfaceView.getFrameHeight(),
                        mCameraSurfaceView.getFrameWidth(), frontCamera);
                STUtils.drawPoints(canvas, mPaint, points, mCameraSurfaceView.getFrameHeight(),
                        mCameraSurfaceView.getFrameWidth(), frontCamera);

                /*
                PointF[] var10 = points;
                int var9 = points.length;
                for (int var8 = 0; var8 < var9; ++var8) {
                    PointF point = var10[var8];
                    if (frontCamera) {
                        point.x = (float) mCameraSurfaceView.getFrameHeight() - point.x;
                    }
                }*/

                ZZFaceResult faceResult = new ZZFaceResult(frontCamera, mCameraSurfaceView.getFrameWidth(), mCameraSurfaceView.getFrameHeight());
                faceResult.turn(points, realWidth, realHeight, j);
                faceResults.add(faceResult);
            }

            ZZFaceManager_v2.getZZFaceManager().updateZZFaceResults(faceResults);
            mOverlap.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void switchCameraFacing() {
        mCameraSurfaceView.change();
        btnSwitchFront.setSelected(!btnSwitchFront.isSelected());
    }

    //美颜开关操作
    private void changeMeiYan() {
        boolean isMy = mCameraSurfaceView.getMeiYan();
        mCameraSurfaceView.setMeiYan(!isMy);
        if (isMy) {
            isMeiYanView.setSelected(false);
        } else {
            isMeiYanView.setSelected(true);
        }
    }

    public void closeFlash() {
        CameraController.getInstance().closeLight();
        btnSwitchSplash.setSelected(false);
    }

    @OnClick({R.id.btn_switch_flash, R.id.btn_switch_front, R.id.btn_switch_meiyan, R.id.tv_loadTx})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_switch_flash:
                //前置摄像头不可开启闪光灯
                if (CameraController.getInstance().getCameraFacing() == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    if(btnSwitchSplash.isSelected()){
                        CameraController.getInstance().closeLight();
                        btnSwitchSplash.setSelected(false);
                    }else{
                        CameraController.getInstance().openLight();
                        btnSwitchSplash.setSelected(true);
                    }
                }
                break;
            case R.id.btn_switch_front:
                if (Camera.getNumberOfCameras() > 1) {
                    switchCameraFacing();
                }
                break;
            case R.id.btn_switch_meiyan:
                changeMeiYan();
                break;
            case R.id.tv_loadTx:
                break;
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CameraSurfaceView.CameraHandler.AFTER_INITEFFECT:
                    Bundle bundle = msg.getData();
                    String actionInfo = "";
                    if (bundle != null) {
                        actionInfo = (bundle.getString("actionInfo") == null ? "" : bundle.getString("actionInfo"));
                    }
                    specificTip.setText(actionInfo);
                    break;
                case CameraSurfaceView.CameraHandler.TRACK_FACE_ING:
                    break;
                case CameraSurfaceView.CameraHandler.WHEN_EFFECTSCROLL:
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCameraSurfaceView.onDestroy();
        if (trackerHandlerThread != null) {
            trackerHandlerThread.quitSafely();
        }
        unbinder.unbind();
        closeFlash();
    }

}

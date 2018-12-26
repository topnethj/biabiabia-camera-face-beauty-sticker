package com.biabiabia.app;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.biabiabia.effect.LibApp;
import com.biabiabia.effect.effectimp.ZZEffectConfig_v2;
import com.biabiabia.effect.face.ZZFaceManager_v2;
import com.biabiabia.effect.face.ZZFaceResult;
import com.biabiabia.effect.camera.camerautil.CameraController;
import com.biabiabia.effect.camera.widget.CameraSurfaceView;
import com.hulu.sdk.Faces;
import com.hulu.sdk.HuluUtils;
import com.biabiabia.effect.Accelerometer;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private static final String EFFECT_TUZI = "effect/tuzi";
    //private static final String EFFECT_PX = "effect/piaoxin";
    //private static final String EFFECT_FW = "effect/xinfeiwen";

    @BindView(R.id.specific_tip)
    protected TextView specificTip;

    @BindView(R.id.b_loadTx)
    protected Button loadTx;

    @BindView(R.id.b_unloadTx)
    protected Button unLoadTx;

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
    private boolean isFaceDebug = false;//绘制人脸点
    private static final int MESSAGE_DRAW_POINTS = 999;
    private boolean isTrackerPaused = false;
    private HandlerThread trackerHandlerThread;
    private Handler trackerHandler;
    private byte[] nv21;
    private byte[] tmp;
    private Accelerometer acc;

    private int realWidth;
    private int realHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        initCamera();

        acc = new Accelerometer(this.getApplicationContext());

        trackerHandlerThread = new HandlerThread("DrawFacePointsThread");
        trackerHandlerThread.start();
        trackerHandler = new Handler(trackerHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DRAW_POINTS) {
                    faceDetect();
                }
            }
        };

        mOverlap.setZOrderOnTop(true);
        mOverlap.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mPaint = new Paint();
        mPaint.setColor(Color.rgb(57, 138, 243));
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
        ZZFaceManager_v2.getZZFaceManager().reset(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCameraSurfaceView.onResume();
        specificTip.setText("");

        //启动人脸识别
        isTrackerPaused = false;
        if (acc != null) {
            acc.start();
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
            if (getMA() == null || data == null) {
                return;
            }
            CameraController.getInstance().addCallbackBuffer(data);
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
        if (isTrackerPaused) {
            return;
        } else {
            if (!ZZFaceManager_v2.getZZFaceManager().canTrack) {
                return;
            }
            synchronized (nv21) {
                System.arraycopy(nv21, 0, tmp, 0, nv21.length);
            }

            /**
             * 调 实时人脸检测函数，返回当前人脸信息
             */
            boolean frontCamera = CameraController.getInstance().getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT;
            Camera.CameraInfo info = CameraController.getInstance().getCameraInfo();

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

            float[] faces = Faces.getInstance(LibApp.getAppContext()).detect(tmp, mCameraSurfaceView.getFrameWidth(), mCameraSurfaceView.getFrameHeight(), dir);
            float[] bound = Faces.getInstance(LibApp.getAppContext()).getRect();
            prepareCanvas(faces, bound, frontCamera);
        }
    }

    private void prepareCanvas(float[] faces, float[] boundRect, boolean frontCamera) {
        List<ZZFaceResult> faceResults = new ArrayList<>();
        if(faces.length >= 212) {
            PointF[] points = HuluUtils.getPoints(faces, mCameraSurfaceView.getFrameWidth(), mCameraSurfaceView.getFrameHeight(), frontCamera);

            ZZFaceResult faceResult = new ZZFaceResult(frontCamera, mCameraSurfaceView.getFrameWidth(), mCameraSurfaceView.getFrameHeight());
            faceResult.turn(points, realWidth, realHeight, 0);
            faceResults.add(faceResult);
        }

        ZZFaceManager_v2.getZZFaceManager().updateZZFaceResults(faceResults);

        if(isFaceDebug && faces.length >= 212) {
            if (!mOverlap.getHolder().getSurface().isValid()) {
                return;
            }
            Canvas canvas = mOverlap.getHolder().lockCanvas();
            if (canvas != null) {
                canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                canvas.setMatrix(matrix);
                HuluUtils.drawPoints(canvas, mPaint, faces, mCameraSurfaceView.getFrameWidth(), mCameraSurfaceView.getFrameHeight(), frontCamera);
                mOverlap.getHolder().unlockCanvasAndPost(canvas);
            }
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
        //btnSwitchSplash.setSelected(false);
    }

    @OnClick({R.id.btn_switch_flash, R.id.btn_switch_front, R.id.btn_switch_meiyan, R.id.b_loadTx, R.id.b_unloadTx})
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
                    //btnSwitchSplash.setSelected(false);
                }
                break;
            case R.id.btn_switch_meiyan:
                changeMeiYan();
                break;
            case R.id.b_loadTx:
                loadTx.setEnabled(false);
                String ePath = ZZEffectConfig_v2.effectConfigCopy(this.getApplicationContext(), EFFECT_TUZI);
                if(ePath != null){
                    mCameraSurfaceView.changeFilter(ePath, true, "", true);
                }else{
                    loadTx.setEnabled(true);
                }
                break;
            case R.id.b_unloadTx:
                mCameraSurfaceView.changeFilter(null, false, "", false);
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
                    loadTx.setEnabled(true);
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
        //closeFlash();
    }
}

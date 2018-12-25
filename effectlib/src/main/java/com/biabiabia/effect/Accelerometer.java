package com.biabiabia.effect;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Accelerometer 用于开启重力传感器，以获得当前手机朝向
 */
public class Accelerometer {
    /**
     * CLOCKWISE_ANGLE为手机旋转角度
     * 其Deg0定义如下图所示
     * ___________________
     * | +--------------+  |
     * | |              |  |
     * | |              |  |
     * | |              | O|
     * | |              |  |
     * | |______________|  |
     * ---------------------
     * 顺时针旋转后得到Deg90，即手机竖屏向上，如下图所示
     * ___________
     * |           |
     * |+---------+|
     * ||         ||
     * ||         ||
     * ||         ||
     * ||         ||
     * ||         ||
     * |+---------+|
     * |_____O_____|
     */
    public enum CLOCKWISE_ANGLE {
        Deg0(0), Deg90(1), Deg180(2), Deg270(3);
        private int value;

        private CLOCKWISE_ANGLE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private SensorManager mSensorManager = null;

    private boolean mHasStarted = false;

    private static CLOCKWISE_ANGLE sRotation;

    /**
     * @param ctx 用Activity初始化获得传感器
     */
    public Accelerometer(Context ctx) {
        mSensorManager = (SensorManager) ctx
                .getSystemService(Context.SENSOR_SERVICE);
        sRotation = CLOCKWISE_ANGLE.Deg0;
    }

    /**
     * 开始对传感器的监听
     */
    public void start() {
        if (mHasStarted) return;
        mHasStarted = true;
        sRotation = CLOCKWISE_ANGLE.Deg0;
        mSensorManager.registerListener(accListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * 结束对传感器的监听
     */
    public void stop() {
        if (!mHasStarted) return;
        mHasStarted = false;
        mSensorManager.unregisterListener(accListener);
    }

    /**
     * @return 返回当前手机转向
     */
    static public int getDirection() {
        return sRotation.getValue();
    }

    /**
     * 传感器与手机转向之间的逻辑
     */
    private SensorEventListener accListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        public void onSensorChanged(SensorEvent arg0) {
            if (arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = arg0.values[0];
                float y = arg0.values[1];
                float z = arg0.values[2];
                if (Math.abs(x) > 3 || Math.abs(y) > 3) {
                    if (Math.abs(x) > Math.abs(y)) {
                        if (x > 0) {
                            sRotation = CLOCKWISE_ANGLE.Deg0;
                        } else {
                            sRotation = CLOCKWISE_ANGLE.Deg180;
                        }
                    } else {
                        if (y > 0) {
                            sRotation = CLOCKWISE_ANGLE.Deg90;
                        } else {
                            sRotation = CLOCKWISE_ANGLE.Deg270;
                        }
                    }
                }
            }
        }
    };
}

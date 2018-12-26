package com.biabiabia.effect.camera.camerautil;

import android.graphics.Rect;
import android.hardware.Camera;
import android.view.View;

import java.util.Collections;
import java.util.List;

public class CameraHelper {
    private static final String TAG = CameraHelper.class.getSimpleName();


    //
    public static Camera.Size getOptimalPreviewSize(Camera.Parameters parameters,
                                                    Camera.CameraInfo info, int viewWidth, int viewHeight, boolean isLittle) {

        if (parameters == null) {
            return null;
        }
        //先从config里去取，如果没有数据再去
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Collections.sort(sizes, new CameraPreviewSizeComparator());

        final double ASPECT_TOLERANCE = 0.05;
        //目标尺寸比例
        double targetRatio;
        if (isLittle) {
            targetRatio = (double) 4 / 3;
        } else {
            targetRatio = (double) 16 / 9;
        }

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        //目标height
        int targetHeight;
        if (isLittle) {
            targetHeight = 320;
        } else {
            targetHeight = 720;
        }
        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;

            if (optimalSize != null && size.height > viewHeight) {
                break;
            }

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }

    //  这里只使用于旋转了90度
    public static Rect calculateTapArea(View v, float oldx, float oldy, float coefficient) {

        float x = oldy;
        float y = v.getHeight() - oldx;

        float focusAreaSize = 300;

        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) (x / v.getWidth() * 2000 - 1000);
        int centerY = (int) (y / v.getHeight() * 2000 - 1000);

        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int right = clamp(left + areaSize, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        int bottom = clamp(top + areaSize, -1000, 1000);

        return new Rect(left, top, right, bottom);
    }

    private static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }
}

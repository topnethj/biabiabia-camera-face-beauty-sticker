package com.douyaim.effect.face;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import com.douyaim.effect.ZZEffectCommon;

/**
 * Created by hj on 17/7/10.
 */

public class ZZFaceDeformResult {
    public PointF[] points;

    public ZZFaceDeformResult() {
        this.points = new PointF[ZZEffectCommon.ZZNumberOfDeformPoints];
        for(int i = 0; i < points.length; i++){
            points[i] = new PointF();
            points[i].x = 0.0f;
            points[i].y = 0.0f;
        }
    }

    public ZZFaceDeformResult(@NonNull PointF[] deformPoints, int surfaceWidth, int surfaceHeight, int frameWidth, int frameHeight) {
        this.points = deformPoints;
        turn(deformPoints, surfaceWidth, surfaceHeight, frameWidth, frameHeight);
    }

    private void turn(@NonNull PointF[] deformPoints, int surfaceWidth, int surfaceHeight, int frameWidth, int frameHeight) {
    }

}

package com.douyaim.effect.model;

import android.support.annotation.NonNull;

/**
 * Created by hj on 17/3/20.
 */

public class Matrix {

    public static Vector4 multiply(@NonNull Matrix4 matrix, @NonNull Vector4 vector) {
        float[] vecArray = vector.toArray();
        float[] matrixArray = matrix.getFloatValues();
        float[] newVec = new float[4];
        float newEntrie = 0;
        for (int i = 0; i < 4; i++) {
            newEntrie = 0;
            for (int j = 0; j < 4; j++) {
                newEntrie = newEntrie + matrixArray[i + j * 4] * vecArray[j];
            }
            newVec[i] = newEntrie;
        }
        return new Vector4(newVec[0], newVec[1], newVec[2], newVec[3]);
    }

}

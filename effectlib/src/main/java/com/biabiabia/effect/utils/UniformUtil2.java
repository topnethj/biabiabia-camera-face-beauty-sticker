package com.biabiabia.effect.utils;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.support.annotation.NonNull;

import java.nio.FloatBuffer;

/**
 * Created by hj on 16/9/29.
 */
public class UniformUtil2 {

    public static void setInteger(final int location, final int intValue) {
        GLES20.glUniform1i(location, intValue);
    }

    public static void setFloat(final int location, final float floatValue) {
        GLES20.glUniform1f(location, floatValue);
    }

    public static void setFloatVec2(final int location, final float[] arrayValue) {
        GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
    }

    public static void setFloatVec3(final int location, final float[] arrayValue) {
        GLES20.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue));
    }

    public static void setFloatVec4(final int location, final float[] arrayValue) {
        GLES20.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue));
    }

    public static void setFloatArray(final int location, final float[] arrayValue) {
        GLES20.glUniform1fv(location, arrayValue.length, FloatBuffer.wrap(arrayValue));
    }

    public static void setPoint(final int location, final PointF point) {
        float[] vec2 = new float[2];
        vec2[0] = point.x;
        vec2[1] = point.y;
        GLES20.glUniform2fv(location, 1, vec2, 0);
    }

    public static void setPoints(final int location, @NonNull final PointF[] points) {
        if (points==null) {
            return;
        }
        float[] vec2 = new float[points.length*2];
        for(int i = 0; i < points.length; i++){
            vec2[i*2] = points[i].x;
            vec2[i*2+1] = points[i].y;
        }
        GLES20.glUniform2fv(location, points.length, FloatBuffer.wrap(vec2));
        vec2 = null;
    }

    public static void setUniformMatrix3f(final int location, final float[] matrix) {
        GLES20.glUniformMatrix3fv(location, 1, false, matrix, 0);
    }

    public static void setUniformMatrix3fBuffer(final int location, final float[] matrix) {
        GLES20.glUniformMatrix3fv(location, 1, false, FloatBuffer.wrap(matrix));
    }

    public static void setUniformMatrix4f(final int location, final float[] matrix) {
        GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0);
    }

    public static void setUniformMatrix4fBuffer(final int location, final float[] matrix) {
        GLES20.glUniformMatrix4fv(location, 1, false, FloatBuffer.wrap(matrix));
    }

}

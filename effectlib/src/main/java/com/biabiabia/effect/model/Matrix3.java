package com.biabiabia.effect.model;

/**
 * Created by hj on 17/3/22.
 */

public class Matrix3 {
    private static final long serialVersionUID = 7907569533774959788L;
    public static final int M00 = 0;
    public static final int M01 = 3;
    public static final int M02 = 6;
    public static final int M10 = 1;
    public static final int M11 = 4;
    public static final int M12 = 7;
    public static final int M20 = 2;
    public static final int M21 = 5;
    public static final int M22 = 8;
    public float[] val = new float[9];

    public Matrix3() {
        idt();
    }

    public Matrix3 (float[] values) {
        this.set(values);
    }

    public Matrix3 idt () {
        float[] val = this.val;
        val[M00] = 1;
        val[M10] = 0;
        val[M20] = 0;
        val[M01] = 0;
        val[M11] = 1;
        val[M21] = 0;
        val[M02] = 0;
        val[M12] = 0;
        val[M22] = 1;
        return this;
    }

    public Matrix3 set (float[] values) {
        System.arraycopy(values, 0, val, 0, val.length);
        return this;
    }

    public Matrix3 mul (Matrix3 m) {
        float[] val = this.val;

        float v00 = val[M00] * m.val[M00] + val[M01] * m.val[M10] + val[M02] * m.val[M20];
        float v01 = val[M00] * m.val[M01] + val[M01] * m.val[M11] + val[M02] * m.val[M21];
        float v02 = val[M00] * m.val[M02] + val[M01] * m.val[M12] + val[M02] * m.val[M22];

        float v10 = val[M10] * m.val[M00] + val[M11] * m.val[M10] + val[M12] * m.val[M20];
        float v11 = val[M10] * m.val[M01] + val[M11] * m.val[M11] + val[M12] * m.val[M21];
        float v12 = val[M10] * m.val[M02] + val[M11] * m.val[M12] + val[M12] * m.val[M22];

        float v20 = val[M20] * m.val[M00] + val[M21] * m.val[M10] + val[M22] * m.val[M20];
        float v21 = val[M20] * m.val[M01] + val[M21] * m.val[M11] + val[M22] * m.val[M21];
        float v22 = val[M20] * m.val[M02] + val[M21] * m.val[M12] + val[M22] * m.val[M22];

        val[M00] = v00;
        val[M10] = v10;
        val[M20] = v20;
        val[M01] = v01;
        val[M11] = v11;
        val[M21] = v21;
        val[M02] = v02;
        val[M12] = v12;
        val[M22] = v22;

        return this;
    }

    public Matrix3 mulLeft (Matrix3 m) {
        float[] val = this.val;

        float v00 = m.val[M00] * val[M00] + m.val[M01] * val[M10] + m.val[M02] * val[M20];
        float v01 = m.val[M00] * val[M01] + m.val[M01] * val[M11] + m.val[M02] * val[M21];
        float v02 = m.val[M00] * val[M02] + m.val[M01] * val[M12] + m.val[M02] * val[M22];

        float v10 = m.val[M10] * val[M00] + m.val[M11] * val[M10] + m.val[M12] * val[M20];
        float v11 = m.val[M10] * val[M01] + m.val[M11] * val[M11] + m.val[M12] * val[M21];
        float v12 = m.val[M10] * val[M02] + m.val[M11] * val[M12] + m.val[M12] * val[M22];

        float v20 = m.val[M20] * val[M00] + m.val[M21] * val[M10] + m.val[M22] * val[M20];
        float v21 = m.val[M20] * val[M01] + m.val[M21] * val[M11] + m.val[M22] * val[M21];
        float v22 = m.val[M20] * val[M02] + m.val[M21] * val[M12] + m.val[M22] * val[M22];

        val[M00] = v00;
        val[M10] = v10;
        val[M20] = v20;
        val[M01] = v01;
        val[M11] = v11;
        val[M21] = v21;
        val[M02] = v02;
        val[M12] = v12;
        val[M22] = v22;

        return this;
    }

}

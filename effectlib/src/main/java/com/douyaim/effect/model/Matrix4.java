/**
 * Copyright 2013 Dennis Ippel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.douyaim.effect.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;

import java.util.Arrays;

/**
 * Encapsulates a column major 4x4 Matrix.
 *
 * This class is not thread safe and must be confined to a single thread or protected by
 * some external locking mechanism if necessary. All static methods are thread safe.
 *
 * Rewritten August 8, 2013 by Jared Woolston (jwoolston@tenkiv.com) with heavy influence from libGDX
 *
 * @author dennis.ippel
 * @author Jared Woolston (jwoolston@tenkiv.com)
 * @see <a href="https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Matrix4.java">
 * https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Matrix4.java</a>
 */
public final class Matrix4 implements Cloneable {

    //Matrix indices as column major notation (Row x Column)
    /*
    M00 M01 M02 M03
	M10 M11 M12 M13
	M20 M21 M22 M23
	M30 M31 M32 M33
	 */
    public static final int M00 = 0;
    public static final int M01 = 4;
    public static final int M02 = 8;
    public static final int M03 = 12;
    public static final int M10 = 1;
    public static final int M11 = 5;
    public static final int M12 = 9;
    public static final int M13 = 13;
    public static final int M20 = 2;
    public static final int M21 = 6;
    public static final int M22 = 10;
    public static final int M23 = 14;
    public static final int M30 = 3;
    public static final int M31 = 7;
    public static final int M32 = 11;
    public static final int M33 = 15;

    @NonNull
    @Size(16)
    private double[] m = new double[16]; //The matrix values

    //The following scratch variables are intentionally left as members
    //and not static to ensure that this class can be utilized by multiple threads
    //in a safe manner without the overhead of synchronization. This is a tradeoff of
    //speed for memory and it is considered a small enough memory increase to be acceptable.
    @NonNull @Size(16) private double[]   mTmp   = new double[16]; //A scratch matrix
    @NonNull @Size(16) private float[]    mFloat = new float[16]; //A float copy of the values, used for sending to GL.
    @Nullable private Matrix4 mMatrix; //A scratch Matrix4

    /**
     * Constructs a default identity {@link Matrix4}.
     */
    public Matrix4() {
        identity();
    }

    /**
     * Constructs a new {@link Matrix4} based on the given matrix.
     *
     * @param matrix {@link Matrix4} The matrix to clone.
     */
    public Matrix4(@NonNull Matrix4 matrix) {
        setAll(matrix);
    }

    /**
     * Constructs a new {@link Matrix4} based on the provided double array. The array length
     * must be greater than or equal to 16 and the array will be copied from the 0 index.
     *
     * @param matrix double array containing the values for the matrix in column major order.
     *               The array is not modified or referenced after this constructor completes.
     */
    public Matrix4(@NonNull @Size(min = 16) double[] matrix) {
        setAll(matrix);
    }


    /**
     * Sets this {@link Matrix4} to an identity matrix.
     *
     * @return A reference to this {@link Matrix4} to facilitate chaining.
     */
    @NonNull
    public Matrix4 identity() {
        // @formatter:off
        m[M00] = 1;	m[M10] = 0;	m[M20] = 0;	m[M30] = 0;
        m[M01] = 0;	m[M11] = 1;	m[M21] = 0;	m[M31] = 0;
        m[M02] = 0;	m[M12] = 0;	m[M22] = 1;	m[M32] = 0;
        m[M03] = 0;	m[M13] = 0;	m[M23] = 0;	m[M33] = 1;
        return this;
        // @formatter:on
    }

    /**
     * Constructs a new {@link Matrix4} based on the provided float array. The array length
     * must be greater than or equal to 16 and the array will be copied from the 0 index.
     *
     * @param matrix float array containing the values for the matrix in column major order.
     *               The array is not modified or referenced after this constructor completes.
     */
    public Matrix4(@NonNull @Size(min = 16) float[] matrix) {
        this(convertFloatsToDoubles(matrix));
    }

    /**
     * Multiplies this {@link Matrix4} with the given one, storing the result in this {@link Matrix}.
     * <pre>
     * A.multiply(B) results in A = AB.
     * </pre>
     *
     * @param matrix {@link Matrix4} The RHS {@link Matrix4}.
     *
     * @return A reference to this {@link Matrix4} to facilitate chaining.
     */
    @NonNull
    public Matrix4 multiply(@NonNull Matrix4 matrix) {
        System.arraycopy(m, 0, mTmp, 0, 16);
        multiplyMM(m, 0, mTmp, 0, matrix.getDoubleValues(), 0);
        return this;
    }

    /**
     * Left multiplies this {@link Matrix4} with the given one, storing the result in this {@link Matrix}.
     * <pre>
     * A.leftMultiply(B) results in A = BA.
     * </pre>
     *
     * @param matrix {@link Matrix4} The LHS {@link Matrix4}.
     *
     * @return A reference to this {@link Matrix4} to facilitate chaining.
     */
    @NonNull
    public Matrix4 leftMultiply(@NonNull Matrix4 matrix) {
        System.arraycopy(m, 0, mTmp, 0, 16);
        multiplyMM(m, 0, matrix.getDoubleValues(), 0, mTmp, 0);
        return this;
    }

    /**
     * Multiplies each element of this {@link Matrix4} by the provided factor.
     *
     * @param value double The multiplication factor.
     *
     * @return A reference to this {@link Matrix4} to facilitate chaining.
     */
    @NonNull
    public Matrix4 multiply(double value) {
        for (int i = 0; i < m.length; ++i) {
            m[i] *= value;
        }
        return this;
    }

    /**
     * Multiply two 4x4 matrices together and store the result in a third 4x4
     * matrix. In matrix notation: result = lhs x rhs. Due to the way
     * matrix multiplication works, the result matrix will have the same
     * effect as first multiplying by the rhs matrix, then multiplying by
     * the lhs matrix. This is the opposite of what you might expect.
     *
     * The same double array may be passed for result, lhs, and/or rhs. However,
     * the result element values are undefined if the result elements overlap
     * either the lhs or rhs elements.
     *
     * @param result The double array that holds the result.
     * @param resultOffset The offset into the result array where the result is
     *        stored.
     * @param lhs The double array that holds the left-hand-side matrix.
     * @param lhsOffset The offset into the lhs array where the lhs is stored
     * @param rhs The double array that holds the right-hand-side matrix.
     * @param rhsOffset The offset into the rhs array where the rhs is stored.
     *
     * @throws IllegalArgumentException if result, lhs, or rhs are null, or if
     * resultOffset + 16 > result.length or lhsOffset + 16 > lhs.length or
     * rhsOffset + 16 > rhs.length.
     */
    public static void multiplyMM(double[] result, int resultOffset,
                                  double[] lhs, int lhsOffset, double[] rhs, int rhsOffset) {
        String message = null;
        if (result == null) {
            message = "Result matrix can not be null.";
        } else if (lhs == null) {
            message = "Left hand side matrix can not be null.";
        } else if (rhs == null) {
            message = "Right hand side matrix can not be null.";
        } else if ((resultOffset + 16) > result.length) {
            message = "Specified result offset would overflow the passed result matrix.";
        } else if ((lhsOffset + 16) > lhs.length) {
            message = "Specified left hand side offset would overflow the passed lhs matrix.";
        } else if ((rhsOffset + 16) > rhs.length) {
            message = "Specified right hand side offset would overflow the passed rhs matrix.";
        }
        if (message != null) {
            throw new IllegalArgumentException(message);
        }

        double sum = 0;
        for (int i = 0; i < 4; ++i) { //Row
            for (int j = 0; j < 4; ++j) { //Column
                sum = 0;
                for (int k = 0; k < 4; ++k) {
                    sum += lhs[i+4*k+lhsOffset] * rhs[4*j+k+rhsOffset];
                }
                result[i+4*j+resultOffset] = sum;
            }
        }
    }

    /**
     * Copies the backing array of this {@link Matrix4} into a float array and returns it.
     *
     * @return float array containing a copy of the backing array. The returned array is owned
     * by this {@link Matrix4} and is subject to change as the implementation sees fit.
     */
    @NonNull
    @Size(16)
    public float[] getFloatValues() {
        convertDoublesToFloats(m, mFloat);
        return mFloat;
    }

    /**
     * Returns the backing array of this {@link Matrix4}.
     *
     * @return double array containing the backing array. The returned array is owned
     * by this {@link Matrix4} and is subject to change as the implementation sees fit.
     */
    @NonNull
    @Size(16)
    public double[] getDoubleValues() {
        return m;
    }

    /**
     * Sets the elements of this {@link Matrix4} based on the elements of the provided {@link Matrix4}.
     *
     * @param matrix {@link Matrix4} to copy.
     *
     * @return A reference to this {@link Matrix4} to facilitate chaining.
     */
    @NonNull
    public Matrix4 setAll(@NonNull Matrix4 matrix) {
        matrix.toArray(m);
        return this;
    }

    /**
     * Sets the elements of this {@link Matrix4} based on the provided double array.
     * The array length must be greater than or equal to 16 and the array will be copied
     * from the 0 index.
     *
     * @param matrix double array containing the values for the matrix in column major order.
     *               The array is not modified or referenced after this constructor completes.
     *
     * @return A reference to this {@link Matrix4} to facilitate chaining.
     */
    @NonNull
    public Matrix4 setAll(@NonNull @Size(min = 16) double[] matrix) {
        System.arraycopy(matrix, 0, m, 0, 16);
        return this;
    }

    @NonNull
    public Matrix4 setAll(@NonNull @Size(min = 16) float[] matrix) {
        // @formatter:off
		m[0] = matrix[0];	m[1] = matrix[1];	m[2] = matrix[2];	m[3] = matrix[3];
		m[4] = matrix[4];	m[5] = matrix[5];	m[6] = matrix[6];	m[7] = matrix[7];
		m[8] = matrix[8];	m[9] = matrix[9];	m[10] = matrix[10];	m[11] = matrix[11];
		m[12] = matrix[12];	m[13] = matrix[13];	m[14] = matrix[14];	m[15] = matrix[15];
		return this;
        // @formatter:on
    }

    /**
     * Create and return a copy of this {@link Matrix4}.
     *
     * @return {@link Matrix4} The copy.
     */
    @NonNull
    @Override
    public Matrix4 clone() {
        return new Matrix4(this);
    }

    /**
     * Copies the backing array of this {@link Matrix4} into the provided double array.
     *
     * @param doubleArray double array to store the copy in. Must be at least 16 elements long.
     *                    Entries will be placed starting at the 0 index.
     */
    public void toArray(@NonNull @Size(min = 16) double[] doubleArray) {
        System.arraycopy(m, 0, doubleArray, 0, 16);
    }

    /**
     * Copies the backing array of this {@link Matrix4} into the provided float array.
     *
     * @param floatArray float array to store the copy in. Must be at least 16 elements long.
     *                   Entries will be placed starting at the 0 index.
     */
    public void toFloatArray(@NonNull @Size(min = 16) float[] floatArray) {
        // @formatter:off
		floatArray[0] = (float)m[0];	floatArray[1] = (float)m[1];	floatArray[2] = (float)m[2];	floatArray[3]
                = (float)m[3];
		floatArray[4] = (float)m[4];	floatArray[5] = (float)m[5];	floatArray[6] = (float)m[6];	floatArray[7]
                = (float)m[7];
		floatArray[8] = (float)m[8];	floatArray[9] = (float)m[9];	floatArray[10] = (float)m[10];	floatArray[11]
                = (float)m[11];
		floatArray[12] = (float)m[12];	floatArray[13] = (float)m[13];	floatArray[14] = (float)m[14];	floatArray[15]
                = (float)m[15];
        // @formatter:on
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Matrix4 matrix4 = (Matrix4) o;
        return Arrays.equals(m, matrix4.m);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(m);
    }

    @NonNull
    @Override
    public String toString() {
        return "[\n"
               // @formatter:off
               + m[M00] + "|" + m[M01] + "|" + m[M02] + "|" + m[M03] + "]\n["
               + m[M10] + "|" + m[M11] + "|" + m[M12] + "|" + m[M13] + "]\n["
               + m[M20] + "|" + m[M21] + "|" + m[M22] + "|" + m[M23] + "]\n["
               + m[M30] + "|" + m[M31] + "|" + m[M32] + "|" + m[M33] + "]\n";
               // @formatter:on
    }

    /**
     * Converts an array of floats to an array of doubles, using the provided output array.
     *
     * @param input float[] array to be converted.
     * @param output double[] array to store the result in.
     * @return float[] a reference to output. Returned for convenience.
     */
    public static double[] convertFloatsToDoubles(float[] input, double[] output) {
        if (input == null || output == null) return output;
        for (int i = 0; i < input.length; ++i) {
            output[i] = (double) input[i];
        }
        return output;
    }

    /**
     * Converts an array of floats to an array of doubles, allocating a new array.
     *
     * @param input double[] array to be converted.
     * @return float[] array with the result. Will be null if input was null.
     */
    public static double[] convertFloatsToDoubles(float[] input) {
        if (input == null) return null;
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; ++i) {
            output[i] = (double) input[i];
        }
        return output;
    }

    /**
     * Converts an array of doubles to an array of floats, using the provided output array.
     *
     * @param input double[] array to be converted.
     * @param output float[] array to store the result in.
     * @return float[] a reference to output. Returned for convenience.
     */
    public static float[] convertDoublesToFloats(double[] input, float[] output) {
        if (input == null || output == null) return output;
        for (int i = 0; i < input.length; ++i) {
            output[i] = (float) input[i];
        }
        return output;
    }

    /**
     * Converts an array of doubles to an array of floats, allocating a new array.
     *
     * @param input double[] array to be converted.
     * @return float[] array with the result. Will be null if input was null.
     */
    public static float[] convertDoublesToFloats(double[] input) {
        if (input == null) return null;
        float[] output = new float[input.length];
        for (int i = 0; i < input.length; ++i) {
            output[i] = (float) input[i];
        }
        return output;
    }
}

package com.biabiabia.effect.model;

/**
 * Created by hj on 17/3/9.
 */

public class Vector4 {
    public float one;
    public float two;
    public float three;
    public float four;

    public Vector4() {};

    public Vector4(float one, float two, float three, float four) {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
    }

    public float[] toArray() {
        return new float[]{one, two, three, four};
    }
}

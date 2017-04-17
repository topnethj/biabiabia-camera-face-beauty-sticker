package com.douyaim.effect.model;

/**
 * Created by hj on 17/3/9.
 */

public class Vector3 {
    public float one;
    public float two;
    public float three;

    public Vector3(){};

    public Vector3(float one, float two, float three) {
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public float[] toArray() {
        return new float[]{one, two, three};
    }
}

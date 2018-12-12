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

    public static Vector3 subtract (Vector3 v1, Vector3 v2) {
        Vector3 r = new Vector3();
        r.one = v1.one - v2.one;
        r.two = v1.two - v2.two;
        r.three = v1.three - v2.three;
        return r;
    }

    public Vector3 divide(float value) {
        one /= value;
        two /= value;
        three /= value;
        return this;
    }
}

package com.biabiabia.effect.model;

/**
 * Created by hj on 17/3/9.
 */

public class Vector2 {
    public float one;
    public float two;

    public Vector2(){};

    public Vector2(float one, float two){
        this.one = one;
        this.two = two;
    }

    public float[] toArray() {
        return new float[]{one, two};
    }
}

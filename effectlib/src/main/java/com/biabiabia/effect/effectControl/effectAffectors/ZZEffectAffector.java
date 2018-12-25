package com.biabiabia.effect.effectControl.effectAffectors;

/**
 * Created by hj on 17/3/17.
 */

public abstract class ZZEffectAffector {

    public final static int eAffectorType_Default = -1;
    public final static int eAffectorType_Position = 0;
    public final static int eAffectorType_SizeScale = 1;
    public final static int eAffectorType_Alpha = 2;
    public final static int eAffectorType_Rotate = 3;
    public final static int eAffectorType_Frame = 4;
    public final static int eAffectorType_PositionWithFace = 5;
    public final static int eAffectorType_SizeScaleWithFace = 6;

    protected int m_type;
    protected float m_startTime;
    protected float m_endTime;
    protected float m_totalTime;
    protected float m_loopTime;

    public boolean update(float time) {
        return false;
    }

    public void reset() {}

    public boolean updateAction(float time, float actionTime, int itemStart) {
        return false;
    }

    public int getM_type() {
        return m_type;
    }

    public void setM_type(int m_type) {
        this.m_type = m_type;
    }

    public float getM_startTime() {
        return m_startTime;
    }

    public void setM_startTime(float m_startTime) {
        this.m_startTime = m_startTime;
    }

    public float getM_endTime() {
        return m_endTime;
    }

    public void setM_endTime(float m_endTime) {
        this.m_endTime = m_endTime;
    }

    public float getM_totalTime() {
        return m_totalTime;
    }

    public void setM_totalTime(float m_totalTime) {
        this.m_totalTime = m_totalTime;
    }

    public float getM_loopTime() {
        return m_loopTime;
    }

    public void setM_loopTime(float m_loopTime) {
        this.m_loopTime = m_loopTime;
    }

}

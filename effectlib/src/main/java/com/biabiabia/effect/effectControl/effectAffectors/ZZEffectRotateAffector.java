package com.biabiabia.effect.effectControl.effectAffectors;

import com.biabiabia.effect.model.Vector3;

/**
 * Created by hj on 17/3/20.
 */

public class ZZEffectRotateAffector extends ZZEffectAffector {

    private Vector3 m_vStartRotate = new Vector3();
    private Vector3 m_vEndRotate = new Vector3();
    private Vector3 m_vCurrentRotate = new Vector3();
    private Vector3 m_vSpeed = new Vector3();
    private Vector3 m_vForwardSpeed = new Vector3();
    private Vector3 m_vReverseSpeed = new Vector3();
    private Vector3 m_vForwardStartRotate = new Vector3();
    private Vector3 m_vReverseStartRotate = new Vector3();
    private boolean m_bNeedReverse;
    private boolean m_bReverse;
    private float m_effectTime;

    public ZZEffectRotateAffector() {
        this.m_type = eAffectorType_Rotate;
    }

    @Override
    public boolean update(float time) {
        if (time < m_startTime) {
            return false;
        }
        float currentTime = time % m_totalTime;
        if (currentTime > m_startTime && currentTime <= m_endTime) {
            float frametime = currentTime - m_startTime;
            if (m_bNeedReverse) {
                if (frametime >= m_effectTime / 2.0f && !m_bReverse) {
                    m_bReverse = true;
                    m_vSpeed = m_vReverseSpeed;
                    m_vStartRotate = m_vReverseStartRotate;
                }

                if (frametime < m_effectTime / 2.0f && m_bReverse) {
                    m_bReverse = false;
                    m_vSpeed = m_vForwardSpeed;
                    m_vStartRotate = m_vForwardStartRotate;
                }
            }

            frametime = m_bReverse ? (frametime - m_effectTime / 2.0f) : frametime;
            m_vCurrentRotate.one = m_vStartRotate.one + m_vSpeed.one * frametime;
            m_vCurrentRotate.two = m_vStartRotate.two + m_vSpeed.two * frametime;
            m_vCurrentRotate.three = m_vStartRotate.three + m_vSpeed.three * frametime;

            return true;
        }

        return false;
    }

    @Override
    public void reset() {
        m_vCurrentRotate = m_vStartRotate;
        Vector3 offset = new Vector3();
        offset.one = m_vEndRotate.one - m_vStartRotate.one;
        offset.two = m_vEndRotate.two - m_vStartRotate.two;
        offset.three = m_vEndRotate.three - m_vStartRotate.three;
        m_effectTime = m_endTime - m_startTime;
        float updateTime = m_effectTime;
        m_bReverse = false;
        if (m_bNeedReverse) {
            updateTime = updateTime / 2.0f;
        }
        m_vSpeed.one = offset.one / updateTime;
        m_vSpeed.two = offset.two / updateTime;
        m_vSpeed.three = offset.three / updateTime;
        if (m_bNeedReverse) {
            m_vForwardSpeed = m_vSpeed;
            m_vReverseSpeed.one = m_vSpeed.one * -1.0f;
            m_vReverseSpeed.two = m_vSpeed.two * -1.0f;
            m_vReverseSpeed.three = m_vSpeed.three * -1.0f;
            m_vForwardStartRotate = m_vStartRotate;
            m_vReverseStartRotate.one = m_vStartRotate.one + m_vSpeed.one * updateTime;
            m_vReverseStartRotate.two = m_vStartRotate.two + m_vSpeed.two * updateTime;
            m_vReverseStartRotate.three = m_vStartRotate.three + m_vSpeed.three * updateTime;
        }
    }

    public Vector3 getM_vStartRotate() {
        return m_vStartRotate;
    }

    public void setM_vStartRotate(Vector3 m_vStartRotate) {
        this.m_vStartRotate = m_vStartRotate;
    }

    public Vector3 getM_vEndRotate() {
        return m_vEndRotate;
    }

    public void setM_vEndRotate(Vector3 m_vEndRotate) {
        this.m_vEndRotate = m_vEndRotate;
    }

    public Vector3 getM_vCurrentRotate() {
        return m_vCurrentRotate;
    }

    public void setM_vCurrentRotate(Vector3 m_vCurrentRotate) {
        this.m_vCurrentRotate = m_vCurrentRotate;
    }

    public Vector3 getM_vSpeed() {
        return m_vSpeed;
    }

    public void setM_vSpeed(Vector3 m_vSpeed) {
        this.m_vSpeed = m_vSpeed;
    }

    public Vector3 getM_vForwardSpeed() {
        return m_vForwardSpeed;
    }

    public void setM_vForwardSpeed(Vector3 m_vForwardSpeed) {
        this.m_vForwardSpeed = m_vForwardSpeed;
    }

    public Vector3 getM_vReverseSpeed() {
        return m_vReverseSpeed;
    }

    public void setM_vReverseSpeed(Vector3 m_vReverseSpeed) {
        this.m_vReverseSpeed = m_vReverseSpeed;
    }

    public Vector3 getM_vForwardStartRotate() {
        return m_vForwardStartRotate;
    }

    public void setM_vForwardStartRotate(Vector3 m_vForwardStartRotate) {
        this.m_vForwardStartRotate = m_vForwardStartRotate;
    }

    public Vector3 getM_vReverseStartRotate() {
        return m_vReverseStartRotate;
    }

    public void setM_vReverseStartRotate(Vector3 m_vReverseStartRotate) {
        this.m_vReverseStartRotate = m_vReverseStartRotate;
    }

    public boolean isM_bNeedReverse() {
        return m_bNeedReverse;
    }

    public void setM_bNeedReverse(boolean m_bNeedReverse) {
        this.m_bNeedReverse = m_bNeedReverse;
    }

    public boolean isM_bReverse() {
        return m_bReverse;
    }

    public void setM_bReverse(boolean m_bReverse) {
        this.m_bReverse = m_bReverse;
    }

    public float getM_effectTime() {
        return m_effectTime;
    }

    public void setM_effectTime(float m_effectTime) {
        this.m_effectTime = m_effectTime;
    }

}

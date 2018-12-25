package com.biabiabia.effect.effectControl.effectAffectors;

import com.biabiabia.effect.model.Vector3;

/**
 * Created by hj on 17/3/20.
 */

public class ZZEffectSizeScaleAffector extends ZZEffectAffector {

    private Vector3 m_vStartSize = new Vector3();
    private Vector3 m_vEndSize = new Vector3();
    private Vector3 m_vCurrentSize = new Vector3();
    private Vector3 m_vSpeed = new Vector3();
    private Vector3 m_vForwardSpeed = new Vector3();
    private Vector3 m_vReverseSpeed = new Vector3();
    private Vector3 m_vForwardStartSize = new Vector3();
    private Vector3 m_vReverseStartSize = new Vector3();
    private boolean m_bNeedReverse;
    private boolean m_bReverse;
    private float m_effectTime;

    public ZZEffectSizeScaleAffector() {
        this.m_type = eAffectorType_SizeScale;
    }

    public ZZEffectSizeScaleAffector(int atype) {
        this.m_type = atype;
    }

    @Override
    public boolean update(float time) {
        if (time < m_startTime) {
            return false;
        }
        float currentTime = time % m_totalTime;
        if (currentTime > m_startTime && currentTime <= m_endTime) {
            float frametime = currentTime - m_startTime;
            if (m_loopTime > 0.0f) {
                frametime = frametime % m_loopTime;
            }

            if (m_bNeedReverse) {
                if (frametime >= m_effectTime / 2.0f && !m_bReverse) {
                    m_bReverse = true;
                    m_vSpeed = m_vReverseSpeed;
                    m_vStartSize = m_vReverseStartSize;
                }

                if (frametime < m_effectTime / 2.0f && m_bReverse) {
                    m_bReverse = false;
                    m_vSpeed = m_vForwardSpeed;
                    m_vStartSize = m_vForwardStartSize;
                }
            }
            frametime = m_bReverse ? (frametime - m_effectTime / 2.0f) : frametime;
            m_vCurrentSize.one = m_vStartSize.one + m_vSpeed.one * frametime;
            m_vCurrentSize.two = m_vStartSize.two + m_vSpeed.two * frametime;
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        m_vCurrentSize = m_vStartSize;
        Vector3 sizeOffset = Vector3.subtract(m_vEndSize, m_vStartSize);
        m_effectTime = m_endTime - m_startTime;
        if (m_loopTime > 0.0f) {
            m_effectTime = m_loopTime;
        }
        float updateTime = m_effectTime;
        m_bReverse = false;
        if (m_bNeedReverse) {
            updateTime = updateTime / 2.0f;
        }
        assert(updateTime > 0.0f);
        m_vSpeed = sizeOffset.divide(updateTime);
        if (m_bNeedReverse) {
            m_vForwardSpeed = m_vSpeed;
            m_vReverseSpeed.one = m_vSpeed.one * -1.0f;
            m_vReverseSpeed.two = m_vSpeed.two * -1.0f;
            m_vForwardStartSize = m_vStartSize;
            m_vReverseStartSize.one = m_vStartSize.one + m_vSpeed.one * updateTime;
            m_vReverseStartSize.two = m_vStartSize.two + m_vSpeed.two * updateTime;
        }
    }

    public Vector3 getM_vStartSize() {
        return m_vStartSize;
    }

    public void setM_vStartSize(Vector3 m_vStartSize) {
        this.m_vStartSize = m_vStartSize;
    }

    public Vector3 getM_vEndSize() {
        return m_vEndSize;
    }

    public void setM_vEndSize(Vector3 m_vEndSize) {
        this.m_vEndSize = m_vEndSize;
    }

    public Vector3 getM_vCurrentSize() {
        return m_vCurrentSize;
    }

    public void setM_vCurrentSize(Vector3 m_vCurrentSize) {
        this.m_vCurrentSize = m_vCurrentSize;
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

    public Vector3 getM_vForwardStartSize() {
        return m_vForwardStartSize;
    }

    public void setM_vForwardStartSize(Vector3 m_vForwardStartSize) {
        this.m_vForwardStartSize = m_vForwardStartSize;
    }

    public Vector3 getM_vReverseStartSize() {
        return m_vReverseStartSize;
    }

    public void setM_vReverseStartSize(Vector3 m_vReverseStartSize) {
        this.m_vReverseStartSize = m_vReverseStartSize;
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


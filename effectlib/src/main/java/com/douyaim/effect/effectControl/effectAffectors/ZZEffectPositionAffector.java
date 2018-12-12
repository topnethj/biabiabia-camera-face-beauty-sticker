package com.douyaim.effect.effectControl.effectAffectors;

import com.douyaim.effect.model.Vector3;

/**
 * Created by hj on 17/3/17.
 */

public class ZZEffectPositionAffector extends ZZEffectAffector {

    private Vector3 m_vStartPos = new Vector3();
    private Vector3 m_vEndPos = new Vector3();
    private Vector3 m_vCurrentPos = new Vector3();
    private Vector3 m_vSpeed = new Vector3();
    private Vector3 m_vForwardSpeed = new Vector3();
    private Vector3 m_vReverseSpeed = new Vector3();
    private Vector3 m_vForwardStartPos = new Vector3();
    private Vector3 m_vReverseStartPos = new Vector3();
    private boolean m_bNeedReverse;
    private boolean m_bReverse;
    private float m_effectTime;

    public ZZEffectPositionAffector(){
        this.m_type = eAffectorType_Position;
    }

    @Override
    public boolean update(float time) {
        if (time < m_startTime) {
            return false;
        }
        float currentTime;
        if((m_totalTime - 0.001) < 0) {
            currentTime = time;
        } else {
            currentTime = time % m_totalTime;
        }
        if (currentTime >= m_startTime && currentTime < m_endTime) {
            float frameTime = currentTime - m_startTime;
            if (m_bNeedReverse) {
                if (frameTime >= m_effectTime / 2.0f && !m_bReverse) {
                    m_bReverse = true;
                    m_vSpeed = m_vReverseSpeed;
                    m_vStartPos = m_vReverseStartPos;
                }
                if (frameTime < m_effectTime / 2.0f && m_bReverse) {
                    m_bReverse = false;
                    m_vSpeed = m_vForwardSpeed;
                    m_vStartPos = m_vForwardStartPos;
                }
            }
            frameTime = m_bReverse ? (frameTime - m_effectTime / 2.0f) : frameTime;
            m_vCurrentPos.one = m_vStartPos.one + m_vSpeed.one * frameTime;
            m_vCurrentPos.two = m_vStartPos.two + m_vSpeed.two * frameTime;
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        m_vCurrentPos = m_vStartPos;
        Vector3 vdis = Vector3.subtract(m_vEndPos, m_vStartPos);
        m_effectTime = m_endTime - m_startTime;
        float updateTime = m_effectTime;
        m_bReverse = false;
        if (m_bNeedReverse) {
            updateTime = updateTime / 2.0f;
        }
        assert(updateTime > 0.0f);
        m_vSpeed = vdis.divide(updateTime);
        if (m_bNeedReverse) {
            m_vForwardSpeed = m_vSpeed;
            m_vReverseSpeed.one = m_vSpeed.one * -1.0f;
            m_vReverseSpeed.two = m_vSpeed.two * -1.0f;
            m_vForwardStartPos = m_vStartPos;
            m_vReverseStartPos.one = m_vStartPos.one + m_vSpeed.one * updateTime;
            m_vReverseStartPos.two = m_vStartPos.two + m_vSpeed.two * updateTime;

        }
    }

    public Vector3 getM_vStartPos() {
        return m_vStartPos;
    }

    public void setM_vStartPos(Vector3 m_vStartPos) {
        this.m_vStartPos = m_vStartPos;
    }

    public Vector3 getM_vEndPos() {
        return m_vEndPos;
    }

    public void setM_vEndPos(Vector3 m_vEndPos) {
        this.m_vEndPos = m_vEndPos;
    }

    public Vector3 getM_vCurrentPos() {
        return m_vCurrentPos;
    }

    public void setM_vCurrentPos(Vector3 m_vCurrentPos) {
        this.m_vCurrentPos = m_vCurrentPos;
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

    public Vector3 getM_vForwardStartPos() {
        return m_vForwardStartPos;
    }

    public void setM_vForwardStartPos(Vector3 m_vForwardStartPos) {
        this.m_vForwardStartPos = m_vForwardStartPos;
    }

    public Vector3 getM_vReverseStartPos() {
        return m_vReverseStartPos;
    }

    public void setM_vReverseStartPos(Vector3 m_vReverseStartPos) {
        this.m_vReverseStartPos = m_vReverseStartPos;
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

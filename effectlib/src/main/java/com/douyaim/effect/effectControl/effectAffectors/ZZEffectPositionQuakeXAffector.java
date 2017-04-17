package com.douyaim.effect.effectControl.effectAffectors;

/**
 * Created by hj on 17/3/22.
 */

public class ZZEffectPositionQuakeXAffector extends ZZEffectAffector {

    private float m_vStartPosX;
    private float m_vEndPosX;
    private float m_vCurrentPosX;
    private float m_vSpeedX;
    private float m_vForwardSpeedX;
    private float m_vReverseSpeedX;
    private float m_vForwardStartPosX;
    private float m_vReverseStartPosX;
    private boolean m_bNeedReverse;
    private boolean m_bReverse;
    private float m_effectTime;

    public ZZEffectPositionQuakeXAffector() {
        this.m_type = eAffectorType_PositionQuakeX;
    }

    @Override
    public boolean update(float time) {
        if (time < m_startTime) {
            return false;
        }
        float currentTime = time % m_totalTime;
        if (currentTime >= m_startTime && currentTime < m_endTime) {
            float frameTime = currentTime - m_startTime;
            if (m_bNeedReverse) {
                if (frameTime >= m_effectTime / 2.0f && !m_bReverse) {
                    m_bReverse = true;
                    m_vSpeedX = m_vReverseSpeedX;
                    m_vStartPosX = m_vReverseStartPosX;
                }

                if (frameTime < m_effectTime / 2.0f && m_bReverse) {
                    m_bReverse = false;
                    m_vSpeedX = m_vForwardSpeedX;
                    m_vStartPosX = m_vForwardStartPosX;
                }
            }

            frameTime = m_bReverse ? (frameTime - m_effectTime / 2.0f) : frameTime;
            m_vCurrentPosX = m_vStartPosX + m_vSpeedX * frameTime;
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        m_vCurrentPosX = m_vStartPosX;
        float xDis = m_vEndPosX - m_vStartPosX;
        m_effectTime = m_endTime - m_startTime;
        float updateTime = m_effectTime;
        m_bReverse = false;
        if (m_bNeedReverse) {
            updateTime = updateTime / 2.0f;
        }
        m_vSpeedX = xDis / updateTime;
        if (m_bNeedReverse) {
            m_vForwardSpeedX = m_vSpeedX;
            m_vReverseSpeedX = m_vSpeedX * -1.0f;
            m_vForwardStartPosX = m_vStartPosX;
            m_vReverseStartPosX = m_vStartPosX + m_vSpeedX * updateTime;
        }
    }

}

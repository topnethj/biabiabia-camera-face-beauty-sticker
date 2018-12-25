package com.biabiabia.effect.effectControl.effectAffectors;

/**
 * Created by hj on 17/3/17.
 */

public class ZZEffectAlphaAffector extends ZZEffectAffector {

    protected float m_StartAlpha;
    protected float m_EndAlpha;
    protected float m_CurrentAlpha;
    protected float m_Speed;
    protected float m_ForwardSpeed;
    protected float m_ReverseSpeed;
    protected float m_ForwardStartAlpha;
    protected float m_ReverseStartAlpha;
    protected boolean m_bNeedReverse;
    protected boolean m_bReverse;
    protected float m_effectTime;

    public ZZEffectAlphaAffector(){
        this.m_type = eAffectorType_Alpha;
    }

    @Override
    public boolean update(float time) {
        if (time < m_startTime) {
            return false;
        }
        float currenttime;
        if ((m_totalTime - 0.001) < 0.0) {
            currenttime = time;
        } else {
            currenttime = time % m_totalTime;
        }
        if (currenttime >= m_startTime && currenttime < m_endTime) {
            float frametime = currenttime - m_startTime;
            if (m_bNeedReverse) {
                if (frametime >= m_effectTime / 2.0f && !m_bReverse) {
                    m_bReverse = true;
                    m_Speed = m_ReverseSpeed;
                    m_StartAlpha = m_ReverseStartAlpha;
                }

                if (frametime < m_effectTime / 2.0f && m_bReverse) {
                    m_bReverse = false;
                    m_Speed = m_ForwardSpeed;
                    m_StartAlpha = m_ForwardStartAlpha;
                }
            }
            frametime = m_bReverse ? (frametime - m_effectTime / 2.0f) : frametime;
            m_CurrentAlpha = m_StartAlpha + m_Speed * frametime;
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        m_CurrentAlpha = m_StartAlpha;
        float alphaOffset = m_EndAlpha - m_StartAlpha;
        m_effectTime = m_endTime - m_startTime;
        float updateTime = m_effectTime;
        m_bReverse = false;
        if (m_bNeedReverse) {
            updateTime = updateTime / 2.0f;
        }
        m_Speed = alphaOffset / updateTime;

        if (m_bNeedReverse) {
            m_ForwardSpeed = m_Speed;
            m_ReverseSpeed = m_Speed * -1.0f;
            m_ForwardStartAlpha = m_StartAlpha;
            m_ReverseStartAlpha = m_StartAlpha + m_Speed * updateTime;
        }
    }

    public float getM_StartAlpha() {
        return m_StartAlpha;
    }

    public void setM_StartAlpha(float m_StartAlpha) {
        this.m_StartAlpha = m_StartAlpha;
    }

    public float getM_EndAlpha() {
        return m_EndAlpha;
    }

    public void setM_EndAlpha(float m_EndAlpha) {
        this.m_EndAlpha = m_EndAlpha;
    }

    public float getM_CurrentAlpha() {
        return m_CurrentAlpha;
    }

    public void setM_CurrentAlpha(float m_CurrentAlpha) {
        this.m_CurrentAlpha = m_CurrentAlpha;
    }

    public float getM_Speed() {
        return m_Speed;
    }

    public void setM_Speed(float m_Speed) {
        this.m_Speed = m_Speed;
    }

    public float getM_ForwardSpeed() {
        return m_ForwardSpeed;
    }

    public void setM_ForwardSpeed(float m_ForwardSpeed) {
        this.m_ForwardSpeed = m_ForwardSpeed;
    }

    public float getM_ReverseSpeed() {
        return m_ReverseSpeed;
    }

    public void setM_ReverseSpeed(float m_ReverseSpeed) {
        this.m_ReverseSpeed = m_ReverseSpeed;
    }

    public float getM_ForwardStartAlpha() {
        return m_ForwardStartAlpha;
    }

    public void setM_ForwardStartAlpha(float m_ForwardStartAlpha) {
        this.m_ForwardStartAlpha = m_ForwardStartAlpha;
    }

    public float getM_ReverseStartAlpha() {
        return m_ReverseStartAlpha;
    }

    public void setM_ReverseStartAlpha(float m_ReverseStartAlpha) {
        this.m_ReverseStartAlpha = m_ReverseStartAlpha;
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

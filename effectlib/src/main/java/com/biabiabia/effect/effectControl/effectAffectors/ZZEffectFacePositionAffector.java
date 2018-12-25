package com.biabiabia.effect.effectControl.effectAffectors;

import com.biabiabia.effect.model.Vector2;

/**
 * Created by hj on 17/3/17.
 */

public class ZZEffectFacePositionAffector extends ZZEffectAffector {

    private String m_StartBindPoint;
    private String m_EndBindPoint;
    private Vector2 m_vStartOffset = new Vector2();
    private Vector2 m_vEndOffset = new Vector2();
    private Vector2 m_vStartPos = new Vector2();
    private Vector2 m_vEndPos = new Vector2();
    private Vector2 m_vCurrentPos = new Vector2();
    private Vector2 m_vSpeed = new Vector2();
    private Vector2 m_vForwardSpeed = new Vector2();
    private Vector2 m_vReverseSpeed = new Vector2();
    private Vector2 m_vForwardStartPos = new Vector2();
    private Vector2 m_vReverseStartPos = new Vector2();
    private int m_StartFacePoint;
    private int m_EndFacePoint;
    private boolean m_bNeedReverse;
    private boolean m_bReverse;
    private float m_effectTime;

    public ZZEffectFacePositionAffector() {
        this.m_type = eAffectorType_PositionWithFace;
    }

    public boolean update(float time) {
        if (time < m_startTime)
            return false;
        float currentTime = time % m_totalTime;
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

    public void updateProperty() {
        float xDis = m_vEndPos.one - m_vStartPos.one;
        float yDis = m_vEndPos.two - m_vStartPos.two;

        float updateTime = m_bNeedReverse ? m_effectTime / 2.0f : m_effectTime;
        Vector2 vSpeed = new Vector2();
        vSpeed.one = xDis / updateTime;
        vSpeed.two = yDis / updateTime;
        if (m_bNeedReverse) {
            m_vForwardSpeed = vSpeed;
            m_vReverseSpeed.one = vSpeed.one * -1.0f;
            m_vReverseSpeed.two = vSpeed.two * -1.0f;

            m_vForwardStartPos = m_vStartPos;
            m_vReverseStartPos.one = m_vStartPos.one + vSpeed.one * updateTime;
            m_vReverseStartPos.two = m_vStartPos.two + vSpeed.two * updateTime;

            if (m_bReverse) {
                m_vSpeed = m_vReverseSpeed;
                m_vStartPos = m_vReverseStartPos;
            } else {
                m_vSpeed = m_vForwardSpeed;
            }
        } else {
            m_vSpeed = vSpeed;
        }
    }

    public void reset() {
        m_vCurrentPos = m_vStartPos;
        m_effectTime = m_endTime - m_startTime;
        m_bReverse = false;
        float xDis = m_vEndPos.one - m_vStartPos.one;
        float yDis = m_vEndPos.two - m_vStartPos.two;
        float updateTime = m_bNeedReverse ? m_effectTime / 2.0f : m_effectTime;
        m_vSpeed.one = xDis / updateTime;
        m_vSpeed.two = yDis / updateTime;
        if (m_bNeedReverse) {
            m_vForwardSpeed = m_vSpeed;
            m_vReverseSpeed.one = m_vSpeed.one * -1.0f;
            m_vReverseSpeed.two = m_vSpeed.two * -1.0f;
            m_vForwardStartPos = m_vStartPos;
            m_vReverseStartPos.one = m_vStartPos.one + m_vSpeed.one * updateTime;
            m_vReverseStartPos.two = m_vStartPos.two + m_vSpeed.two * updateTime;
        }
    }

    public String getM_StartBindPoint() {
        return m_StartBindPoint;
    }

    public void setM_StartBindPoint(String m_StartBindPoint) {
        this.m_StartBindPoint = m_StartBindPoint;
    }

    public String getM_EndBindPoint() {
        return m_EndBindPoint;
    }

    public void setM_EndBindPoint(String m_EndBindPoint) {
        this.m_EndBindPoint = m_EndBindPoint;
    }

    public Vector2 getM_vStartOffset() {
        return m_vStartOffset;
    }

    public void setM_vStartOffset(Vector2 m_vStartOffset) {
        this.m_vStartOffset = m_vStartOffset;
    }

    public Vector2 getM_vEndOffset() {
        return m_vEndOffset;
    }

    public void setM_vEndOffset(Vector2 m_vEndOffset) {
        this.m_vEndOffset = m_vEndOffset;
    }

    public Vector2 getM_vStartPos() {
        return m_vStartPos;
    }

    public void setM_vStartPos(Vector2 m_vStartPos) {
        this.m_vStartPos = m_vStartPos;
    }

    public Vector2 getM_vEndPos() {
        return m_vEndPos;
    }

    public void setM_vEndPos(Vector2 m_vEndPos) {
        this.m_vEndPos = m_vEndPos;
    }

    public Vector2 getM_vCurrentPos() {
        return m_vCurrentPos;
    }

    public void setM_vCurrentPos(Vector2 m_vCurrentPos) {
        this.m_vCurrentPos = m_vCurrentPos;
    }

    public Vector2 getM_vSpeed() {
        return m_vSpeed;
    }

    public void setM_vSpeed(Vector2 m_vSpeed) {
        this.m_vSpeed = m_vSpeed;
    }

    public Vector2 getM_vForwardSpeed() {
        return m_vForwardSpeed;
    }

    public void setM_vForwardSpeed(Vector2 m_vForwardSpeed) {
        this.m_vForwardSpeed = m_vForwardSpeed;
    }

    public Vector2 getM_vReverseSpeed() {
        return m_vReverseSpeed;
    }

    public void setM_vReverseSpeed(Vector2 m_vReverseSpeed) {
        this.m_vReverseSpeed = m_vReverseSpeed;
    }

    public Vector2 getM_vForwardStartPos() {
        return m_vForwardStartPos;
    }

    public void setM_vForwardStartPos(Vector2 m_vForwardStartPos) {
        this.m_vForwardStartPos = m_vForwardStartPos;
    }

    public Vector2 getM_vReverseStartPos() {
        return m_vReverseStartPos;
    }

    public void setM_vReverseStartPos(Vector2 m_vReverseStartPos) {
        this.m_vReverseStartPos = m_vReverseStartPos;
    }

    public int getM_StartFacePoint() {
        return m_StartFacePoint;
    }

    public void setM_StartFacePoint(int m_StartFacePoint) {
        this.m_StartFacePoint = m_StartFacePoint;
    }

    public int getM_EndFacePoint() {
        return m_EndFacePoint;
    }

    public void setM_EndFacePoint(int m_EndFacePoint) {
        this.m_EndFacePoint = m_EndFacePoint;
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

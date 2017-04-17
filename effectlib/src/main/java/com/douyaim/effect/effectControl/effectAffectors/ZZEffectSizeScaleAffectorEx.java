package com.douyaim.effect.effectControl.effectAffectors;

import com.douyaim.effect.model.Vector2;

/**
 * Created by hj on 17/3/20.
 */

public class ZZEffectSizeScaleAffectorEx extends ZZEffectAffector{

    private Vector2 m_vStartSize = new Vector2();
    private Vector2 m_vEndSize = new Vector2();
    private Vector2 m_vCurrentSize = new Vector2();
    private float m_effectTime;

    public ZZEffectSizeScaleAffectorEx(){
        this.m_type = eAffectorType_SizeScaleEx;
    }

    @Override
    public boolean update(float time) {
        if(time < m_startTime){
            return false;
        }
        float currentTime = time % m_totalTime;
        if(currentTime > m_startTime && currentTime <= m_endTime)
        {
            float frameTime = currentTime - m_startTime;
            float f = frameTime / m_loopTime;
            boolean bReverse = f % 2.0f > 0;
            m_vCurrentSize = bReverse ? m_vEndSize : m_vStartSize;
            return true;
        }
        return false;
    }

    @Override
    public void reset() {
        m_vCurrentSize = m_vStartSize;
        //vec2f sizeoffset = m_vEndSize - m_vStartSize;
        //m_effectTime = m_endTime - m_startTime;
        //if(m_loopTime > 0.0f)
        {
            //m_effectTime = m_loopTime;
        }
    }

    public Vector2 getM_vStartSize() {
        return m_vStartSize;
    }

    public void setM_vStartSize(Vector2 m_vStartSize) {
        this.m_vStartSize = m_vStartSize;
    }

    public Vector2 getM_vEndSize() {
        return m_vEndSize;
    }

    public void setM_vEndSize(Vector2 m_vEndSize) {
        this.m_vEndSize = m_vEndSize;
    }

    public Vector2 getM_vCurrentSize() {
        return m_vCurrentSize;
    }

    public void setM_vCurrentSize(Vector2 m_vCurrentSize) {
        this.m_vCurrentSize = m_vCurrentSize;
    }

    public float getM_effectTime() {
        return m_effectTime;
    }

    public void setM_effectTime(float m_effectTime) {
        this.m_effectTime = m_effectTime;
    }

}

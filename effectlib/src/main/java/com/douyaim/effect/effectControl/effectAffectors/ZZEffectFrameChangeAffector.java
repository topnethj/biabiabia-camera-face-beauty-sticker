package com.douyaim.effect.effectControl.effectAffectors;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hj on 17/3/20.
 */

public class ZZEffectFrameChangeAffector extends ZZEffectAffector {

    private List<FrameInfo> m_vFrameInfos = new ArrayList<>();
    private FrameInfo m_CurrentFrame;
    private float m_effectTime;

    public ZZEffectFrameChangeAffector() {
        this.m_type = eAffectorType_Frame;
    }

    @Override
    public boolean update(float time) {
        if (time < m_startTime) {
            return false;
        }
        float currentTime = time % m_totalTime;
        for (int i = 0; i < m_vFrameInfos.size(); i++) {
            if (currentTime >= m_vFrameInfos.get(i).starttime && currentTime < m_vFrameInfos.get(i).endtime) {
                m_CurrentFrame = m_vFrameInfos.get(i);
                m_effectTime = currentTime - m_vFrameInfos.get(i).starttime;
                return true;
            }
        }
        return false;
    }

    @Override
    public void reset() {
        m_CurrentFrame = m_vFrameInfos.get(0);
        m_effectTime = 0.0f;
    }

    public List<FrameInfo> getM_vFrameInfos() {
        return m_vFrameInfos;
    }

    public void setM_vFrameInfos(List<FrameInfo> m_vFrameInfos) {
        this.m_vFrameInfos = m_vFrameInfos;
    }

    public FrameInfo getM_CurrentFrame() {
        return m_CurrentFrame;
    }

    public void setM_CurrentFrame(FrameInfo m_CurrentFrame) {
        this.m_CurrentFrame = m_CurrentFrame;
    }

    public float getM_effectTime() {
        return m_effectTime;
    }

    public void setM_effectTime(float m_effectTime) {
        this.m_effectTime = m_effectTime;
    }

    public static class FrameInfo {
        public String framename;
        public float starttime;
        public float endtime;
    }

}

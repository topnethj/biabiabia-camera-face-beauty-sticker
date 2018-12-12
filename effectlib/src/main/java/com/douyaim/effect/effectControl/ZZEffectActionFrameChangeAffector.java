package com.douyaim.effect.effectControl;

import com.douyaim.effect.effectControl.effectAffectors.ZZEffectAffector;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hj on 17/8/28.
 */

public class ZZEffectActionFrameChangeAffector extends ZZEffectAffector {

    private List<ActionInfo> m_vActionFrameInfos = new ArrayList<>();
    private ActionInfo m_CurrentActionFrame;
    private float m_effectActionTime;

    public ZZEffectActionFrameChangeAffector() {
        m_type = eAffectorType_StrikeChangeFrame;
    }

    public boolean updateAction(float time, float actionTime, int itemStart) {
        if (time < m_startTime || time < actionTime) {
            return false;
        }
        for (int i = 0; i < m_vActionFrameInfos.size(); i++) {
            ActionInfo mActionInfo = m_vActionFrameInfos.get(i);
            int actionInt = Integer.parseInt(mActionInfo.action);
            float totalTime = Float.parseFloat(mActionInfo.totalTime);
            if ((actionInt & itemStart) != 0 && !mActionInfo.isAction) {
                mActionInfo.isAction = true;
            }
            if (mActionInfo.isAction) {//匹配动作
                float currentTime = time - actionTime;
                if (currentTime < totalTime) {
                    m_effectActionTime = currentTime;
                    return true;
                } else {
                    mActionInfo.isAction = false;
                    return false;
                }
            } else {
                m_effectActionTime = (totalTime > 0.0) ? time % totalTime : time;
                return true;
            }
        }
        return false;
    }

    public void reset() {
        assert (m_vActionFrameInfos.size() > 0);
        m_CurrentActionFrame = m_vActionFrameInfos.get(0);
        m_effectActionTime = 0;
        m_CurrentActionFrame.isAction = false;
    }

    public List<ActionInfo> getM_vActionFrameInfos() {
        return m_vActionFrameInfos;
    }

    public void setM_vActionFrameInfos(List<ActionInfo> m_vActionFrameInfos) {
        this.m_vActionFrameInfos = m_vActionFrameInfos;
    }

    public ActionInfo getM_CurrentActionFrame() {
        return m_CurrentActionFrame;
    }

    public void setM_CurrentActionFrame(ActionInfo m_CurrentActionFrame) {
        this.m_CurrentActionFrame = m_CurrentActionFrame;
    }

    public float getM_effectActionTime() {
        return m_effectActionTime;
    }

    public void setM_effectActionTime(float m_effectActionTime) {
        this.m_effectActionTime = m_effectActionTime;
    }

    public static class ActionInfo {
        String frameName;
        String action;
        String totalTime;
        boolean isAction;
    }
}

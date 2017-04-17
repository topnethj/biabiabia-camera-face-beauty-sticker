package com.douyaim.effect.effectimp;

/**
 * Created by hj on 17/3/23.
 */

public class ZZEffectElement {

    ZZEffect2DElement_v2 element2d;
    int faceIndex;
    int renderOrder;

    public void initWithElement2d(ZZEffect2DElement_v2 e, int faceIndex, int renderOrder) {
        this.element2d = e;
        this.faceIndex = faceIndex;
        this.renderOrder = renderOrder;
    }

}

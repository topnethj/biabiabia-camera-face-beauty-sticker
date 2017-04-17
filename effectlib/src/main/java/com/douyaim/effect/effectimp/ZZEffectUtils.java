package com.douyaim.effect.effectimp;

import android.graphics.Point;
import com.douyaim.effect.model.Vector2;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hj on 17/3/21.
 */

public class ZZEffectUtils {

    public static Map<String, Integer> facePointIndexs = new HashMap<>();

    static {
        facePointIndexs.put("faceLeft", 0);
        facePointIndexs.put("faceRight", 32);
        facePointIndexs.put("faceBottom", 16);
        facePointIndexs.put("leftEyebrow", 37);
        facePointIndexs.put("rightEyebrow", 38);
        facePointIndexs.put("rightEyebrow2", 41);
        facePointIndexs.put("leftEyebrow2", 35);
        facePointIndexs.put("leftEyebrow3", 34);
        facePointIndexs.put("rightEyebrow3", 41);
        facePointIndexs.put("leftEar", 5);
        facePointIndexs.put("rightEar", 27);
        facePointIndexs.put("leftEar2", 3);
        facePointIndexs.put("rightEar2", 29);
        facePointIndexs.put("leftEar3", 4);
        facePointIndexs.put("rightEar3", 28);
        facePointIndexs.put("leftEye", 74);
        facePointIndexs.put("rightEye", 77);
        facePointIndexs.put("rightEyeBottom", 76);
        facePointIndexs.put("leftEyeBottom", 73);
        facePointIndexs.put("bridgeOfNose1", 43);
        facePointIndexs.put("bridgeOfNose2", 44);
        facePointIndexs.put("nose1", 45);
        facePointIndexs.put("nose2", 46);
        facePointIndexs.put("philtrum", 49);
        facePointIndexs.put("topMouth", 87);
        facePointIndexs.put("bottomMouth", 93);
        facePointIndexs.put("leftMouth", 84);
        facePointIndexs.put("rightMouth", 90);
        facePointIndexs.put("centerMouth", 98);
        facePointIndexs.put("nose3", 47);
        facePointIndexs.put("faceLeftBottom", 10);
        facePointIndexs.put("faceRightBottom", 22);
    }

    public static float distanceBetweenPoints(Point p1, Point p2) {
        float dis = (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
        return (float)Math.sqrt(dis);
    }

    public static float distanceBetweenPoints(Vector2 v1, Vector2 v2) {
        float dis = (v1.one - v2.one) * (v1.one - v2.one) + (v1.two - v2.two) * (v1.two - v2.two);
        return (float)Math.sqrt(dis);
    }

    public static Map<String, Integer> sharedFacePointIndexs() {
        return facePointIndexs;
    }

}

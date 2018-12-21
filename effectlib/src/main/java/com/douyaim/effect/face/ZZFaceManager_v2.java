package com.douyaim.effect.face;

import android.graphics.PointF;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by hj on 16/9/21.
 */

public class ZZFaceManager_v2 {
    static {
        System.loadLibrary("rotestt");
    }

    private Object o1 = new Object();
    private Object o2 = new Object();

    public static ZZFaceManager_v2 faceManager;

    public int maxStackNum = 24;
    public boolean canTrack = false;
    public long currentTimeMillis;

    public List<ZZFaceResult> currentFaceResults = new ArrayList<>();
    public Stack<List<ZZFaceResult>> faceStack = new Stack<List<ZZFaceResult>>();

    public int randomValue = -1;

    public Map<String, Integer> mouthCount = new HashMap<>();
    public double start = 0;

    private ZZFaceManager_v2(){};

    public static ZZFaceManager_v2 getZZFaceManager(){
        if (faceManager == null) {
            synchronized (ZZFaceManager_v2.class) {
                if (faceManager == null) {
                    faceManager = new ZZFaceManager_v2();
                }
            }
        }
        return faceManager;
    }

    public void updateZZFaceResults(List<ZZFaceResult> faceResults) {
        //if(!canTrack || faceResults.size() < 1){
        //    return;
        //}
        if(!canTrack || faceResults == null){
            return;
        }
        for(ZZFaceResult current : faceResults) {
            if (current.getFaceStatus() != ZZFaceResult.ZZ_FACESTATUS_UNKNOWN) {
                for (PointF point : current.getPoints()) {
                    point.x = point.x * 2.0f - 1.0f;
                    point.y = point.y * 2.0f - 1.0f;
                }
            }
        }
        currentFaceResults = faceResults;
        /*
        synchronized (o1){
            int size = faceStack.size();
            if(size > maxStackNum){
                faceStack.remove(size - 1);
            }
            faceStack.push(faceResults);
        }*/
    }

    public List<ZZFaceResult> getFaceResult() {
        return currentFaceResults;
        /*
        List<ZZFaceResult> results = null;
        synchronized (o1){
            try{
                results = this.faceStack.pop();
            }catch (Exception e){
            }
        }
        if(results == null){
            return new ArrayList<ZZFaceResult>();
        }
        for(ZZFaceResult faceResult : results){
            faceResult.setFaceStatus(getFaceStatus(faceResult));
        }
        return results;*/
    }

    public int getRandom() {
        return randomValue;
    }

    public void setRandom (int curValue) {
        randomValue = curValue;
    }

    public void reset(boolean canTrack){
        this.canTrack = canTrack;
        currentTimeMillis = System.currentTimeMillis();
        faceStack.clear();
        mouthCount.clear();
        start = 0;
        randomValue = -1;
        currentFaceResults = new ArrayList<>();
    }

    public native double[] rotestimate2Native(double[] points, float screenRatio);
}

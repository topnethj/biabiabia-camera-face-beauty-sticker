package com.biabiabia.effect.effectimp;

import java.io.Serializable;

/**
 * Created by hj on 16/9/22.
 */
public class ZZEffectAudioItem_v2 implements Serializable {

    private String name;
    private String filename;
    private int start;
    private int end;
    private float duration;
    private String repeat;
    private String dirPath;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public boolean getRepeat() {
        boolean isRepeat = false;
        try{
            int temp = Integer.parseInt(repeat);
            isRepeat = (temp == 1 ? true : false);
        }catch(Exception e){
            isRepeat = Boolean.parseBoolean(repeat);
        }
        return isRepeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getDirPath() {
        return dirPath;
    }

    public void setDirPath(String dirPath) {
        this.dirPath = dirPath;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

}

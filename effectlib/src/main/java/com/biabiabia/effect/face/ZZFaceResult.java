package com.biabiabia.effect.face;

import android.graphics.PointF;
import android.support.annotation.NonNull;

import com.biabiabia.effect.ZZEffectCommon;

import java.text.DecimalFormat;

/**
 * Created by hj on 16/9/1.
 */
public class ZZFaceResult {

    public static final int ZZ_FACESTATUS_NONE = 0x0;//没有状态
    public static final int ZZ_FACESTATUS_UNKNOWN = 0x1;//没有检测到脸
    public static final int ZZ_FACESTATUS_NORMAL = 0x2;//检测到脸
    public static final int ZZ_FACESTATUS_MOUTHOPENED = 0x4;//张嘴
    public static final int ZZ_FACESTATUS_EYEBROWSRAISED = 0x8;//挑眉毛
    public static final int ZZ_FACESTATUS_ALL = 0xF;//所有状态

    /**
     * 检测到第二张人脸  10
     */
    public static final int ZZTwoFaceStatusSecFaceRaise = 0xA;

    /**
     * 检测到第一张脸和第二张脸的距离小于指定距离触发  11
     */
    public static final int ZZTwoFaceStatusFirstFaceToSecFaceDistanceRaise = 0xB;

    /**
     * A-B,A张嘴触发B 12
     */
    public static final int ZZTwoFaceStatusAMouthOpenedToBRaise = 0xC;

    /**
     * A-B中有一个人张嘴即可 16
     */
    public static final int ZZTwoFaceStatusMouthOpenedRaise = 0x10;

    /**
     * 第二张人脸消失 17
     */
    public static final int ZZTwoFaceStatusSecFaceGone = 0x11;

    /**
     * 检测到第一张脸和第二张脸的距离小于指定距离触发  13
     */
    public static final int ZZTwoFaceStatusFirstFaceToSecFaceDistanceRaiseEx = 0xD;

    public static final int ZZEffectAOpenMouthRaiseB = 1;//A张嘴触发B
    public static final int ZZEffectAOpenMouthRaiseA = 2;//A张嘴触发A
    public static final int ZZEffectBOpenMouthRaiseA = 3;//B张嘴触发A
    public static final int ZZEffectBOpenMouthRaiseB = 4;//B张嘴触发B

    public static final int ZZEffectElementTwoFaceRaiseType = 0;//第二张人脸
    public static final int ZZEffectElementDistanceType = 1;//距离
    public static final int ZZEffectElementOpenMouthType = 2;//张嘴
    public static final int ZZEffectElementDistanceSectionType = 3;//距离触发——分段特效
    public static final int ZZEffectElementDistanceExType = 4;//距离触发——连续特效

    //随机类型
    public static final int RandomType_default = 0;
    public static final int RandomType_pseudo_random = 1;
    public static final int RandomType_random = 2;

    private static final int[] _INDEXS = {32,31,30,29,28,27,26,25,24,23,22,21,20,19,18,17,16,15,14,13,12,11,10,9,8,7,6,5,4,3,
            2,1,0,42,41,40,39,38,37,36,35,34,33,43,44,45,46,51,50,49,48,47,61,60,59,58,63,62,55,54,53,52,57,56,71,70,69,
            68,67,66,65,64,75,76,77,72,73,74,79,78,81,80,83,82,90,89,88,87,86,85,84,95,94,93,92,91,100,99,98,97,96,103,
            102,101,105,104};

    private static final float mouthOpenThresholdValue = 0.1f;
    private static final float browsRaiseThresholdValue = 0.35f;

    private int faceStatus;
    private boolean isFront;
    private long timestamp;//当前时间秒
    private PointF[] points;
    private float width;//预览宽
    private float height;//预览高
    private float pitch;
    private float yaw;
    private float roll;

    public ZZFaceResult(boolean isFront, float width, float height){
        this.isFront = isFront;
        this.width = width;
        this.height = height;
        timestamp = System.currentTimeMillis()/1000;
        this.faceStatus = ZZ_FACESTATUS_UNKNOWN;
    }


    public void turn(@NonNull PointF[] facePoints, int sWith, int sHeight, int faceIndex){
        float screenScale = (float) sWith / (float) sHeight;

        int plen = facePoints.length;
        points = new PointF[plen];

        this.faceStatus = ZZ_FACESTATUS_UNKNOWN;

        for(int i = 0; i < plen; i++){
            turnPoint(facePoints[i], i);
        }

        this.faceStatus = judgeFaceStatusWithPoints(points, faceIndex);

        /*
        PointF p1 = points[46];
        PointF p2 = points[43];
        PointF p3 = points[93];

        float rotX = getYawOrPitch(p1, p2, p3);

        p1 = points[43];
        p2 = points[0];
        p3 = points[32];

        float rotY = getYawOrPitch(p1, p2, p3);

        p1 = points[16];
        p2 = points[43];

        float rotZ = getRoll(p1, p2, screenScale);*/

        double[] points_d = new double[ZZEffectCommon.ZZNumberOfFacePoints * 2];
        for(int i = 0; i < ZZEffectCommon.ZZNumberOfFacePoints; i++) {
            int yIndex = i + 106;
            points_d[i] = points[i].x;
            points_d[yIndex] = points[i].y;
        }
        double[] rtst = ZZFaceManager_v2.getZZFaceManager().rotestimate2Native(points_d, screenScale);

        this.pitch = (float)rtst[0];
        this.yaw = (float)rtst[1];
        this.roll = -(float)rtst[2];
    }

    public void turn(@NonNull PointF[] facePoints){
        int plen = facePoints.length;
        points = new PointF[plen];
        for(int i = 0; i < plen; i++){
            points[i] = new PointF();
            //points[i].x = (facePoints[i].x - width / 2) / 2 / width;
            //points[i].y = (height / 2 - facePoints[i].y) / 2 / height;
            points[i].x = facePoints[i].x / width;
            points[i].y = facePoints[i].y / height;
        }
    }

    private void turnPoint(PointF cvFacePoint, int i){
        if(isFront){
            points[_INDEXS[i]] = new PointF();
            points[_INDEXS[i]].y = cvFacePoint.y / width;
            points[_INDEXS[i]].x = cvFacePoint.x / height;
        } else {
            points[i] = new PointF();
            points[i].y = cvFacePoint.y / width;
            points[i].x = cvFacePoint.x / height;
        }
    }

    private int judgeFaceStatusWithPoints (PointF[] points, int faceIndex) {
        double now = System.currentTimeMillis();
        float mouthDisV = distanceBetweenPoints(points[98], points[102]);
        float mouthDisH = distanceBetweenPoints(points[84], points[90]);
        float mouthValue = mouthDisV / mouthDisH;
        if (mouthValue > mouthOpenThresholdValue) {
            if ((float)(now - ZZFaceManager_v2.getZZFaceManager().start)/1000f > 1.2f) {
                String currkey = "" + faceIndex;
                int _count = 0;
                Integer count = ZZFaceManager_v2.getZZFaceManager().mouthCount.get(currkey);
                if(count != null){
                    _count = count.intValue();
                }
                _count++;
                ZZFaceManager_v2.getZZFaceManager().mouthCount.put(currkey, _count);
                ZZFaceManager_v2.getZZFaceManager().start = now;
            }
            return ZZ_FACESTATUS_MOUTHOPENED;
        }

        float leftBrowsDisV = distanceBetweenPoints(points[65], points[74]);
        float leftBrowsDisH = distanceBetweenPoints(points[33], points[37]);
        float leftBrowsValue = leftBrowsDisV / leftBrowsDisH;
        float rightBrowsDisV = distanceBetweenPoints(points[70], points[77]);
        float rightBrowsDisH = distanceBetweenPoints(points[38], points[42]);
        float rightBrowsValue = rightBrowsDisV / rightBrowsDisH;
        float browsValue = (leftBrowsValue + rightBrowsValue) / 2.0f;
        if (browsValue > browsRaiseThresholdValue) {
            return ZZ_FACESTATUS_EYEBROWSRAISED;
        }
        return ZZ_FACESTATUS_NORMAL;
    }

    private float getYawOrPitch(PointF point1, PointF point2, PointF point3) {
        float v1 = getDistance2DWithPoint(point1, point2);
        float v2 = getDistance2DWithPoint(point1, point3);
        return (float)(Math.atan(v1/v2)- Math.PI/4.0);
    }

    private float distanceBetweenPoints(PointF p1, PointF p2) {
        float dis = (p1.x - p2.x)*(p1.x - p2.x) + (p1.y-p2.y)*(p1.y-p2.y);
        return (float) Math.sqrt(dis);
    }

    private float getDistance2DWithPoint(PointF p1, PointF p2) {
        return (float) Math.sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
    }

    //获取roll
    private float getRoll1(PointF point1, PointF point2, float sceneScale) {
        PointF point = new PointF();
        point.x = point2.x - point1.x;
        point.y = point2.y - point1.y;
        return (float) Math.atan(point.x/(point.y/sceneScale));
    }

    private float getRoll(PointF point1, PointF point2, float sceneScale) {
        PointF point = new PointF();
        point.x = point1.x - point2.x;
        point.y = point1.y - point2.y;
        return -(float)Math.atan(point.x / (point.y / sceneScale));
    }

    private float FFormat(float f){
        DecimalFormat df = new DecimalFormat("0.00");
        return Float.parseFloat(df.format(f));
    }

    public int getFaceStatus() {
        return faceStatus;
    }

    public void setFaceStatus(int faceStatus) {
        this.faceStatus = faceStatus;
    }

    public boolean isFront() {
        return isFront;
    }

    public void setFront(boolean front) {
        isFront = front;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public PointF[] getPoints() {
        return points;
    }

    public void setPoints(PointF[] points) {
        this.points = points;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

}

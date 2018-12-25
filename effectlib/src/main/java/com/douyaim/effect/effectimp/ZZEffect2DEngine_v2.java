package com.douyaim.effect.effectimp;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.support.annotation.NonNull;
import com.douyaim.effect.ZZEffectCommon;
import com.douyaim.effect.face.ZZFaceResult;
import com.douyaim.effect.model.AndroidSize;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by hj on 16/9/5.
 */
public class ZZEffect2DEngine_v2 {
    public int[] mFrameBuffers = null;
    public int[] mFrameBufferTextures = null;

    static PointF[] defaultPoints;

    static final float vertices[] = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            -1.0f,  1.0f,
            1.0f,  1.0f
    };

    static final float textureCoordinates[] = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
    };

    AndroidSize bufferSize;
    FloatBuffer verticesBuffer;
    FloatBuffer textureCoordinatesBuffer;

    ZZFaceResult _fullStickerResult;
    List<ZZEffectElement> _elements;

    public ZZEffect2DEngine_v2(@NonNull AndroidSize bufferSize){
        this.bufferSize = bufferSize;
        initCameraFrameBuffer();
        synchronized (ZZEffect2DEngine_v2.class){
            if(defaultPoints == null){
                defaultPoints = new PointF[ZZEffectCommon.ZZNumberOfFacePoints];
                for(int i = 0; i < defaultPoints.length; i++){
                    defaultPoints[i] = new PointF();
                    defaultPoints[i].x = 0.0f;
                    defaultPoints[i].y = 0.0f;
                }
            }
        }
    }

    public void initWithItems (@NonNull List<ZZEffect2DItem_v2> items) {
        verticesBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        verticesBuffer.put(vertices).position(0);

        textureCoordinatesBuffer = ByteBuffer.allocateDirect(textureCoordinates.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        textureCoordinatesBuffer.put(textureCoordinates).position(0);

        _fullStickerResult = new ZZFaceResult(true, 0, 0);

        _elements = new ArrayList<>();

        for (ZZEffect2DItem_v2 item : items) {
            if(item.getRenderOrders() == null || item.getRenderOrders().length < 1){
                continue;
            }else if(item.getFaceIndexs() != null && item.getFaceIndexs().length != item.getRenderOrders().length){
                continue;
            }
            if(item.getStrikeType() == ZZEffectCommon.ZZEffectElementActionType){
                continue;
            }
            loadElement(item);
        }

        Collections.sort(_elements, new Comparator<ZZEffectElement>() {
            @Override
            public int compare(ZZEffectElement o1, ZZEffectElement o2) {
                return new Integer(o2.renderOrder).compareTo(o1.renderOrder);
            }
        });
    }

    private void loadElement(ZZEffect2DItem_v2 item) {
        ZZEffect2DElement_v2 element = new ZZEffect2DElement_v2();
        element.initWithItem(item, verticesBuffer, textureCoordinatesBuffer);
        /**
         *  遍历当前元素的渲染顺序数组，按照渲染顺序放入队列中相应的位置
         */
        for (int i = 0; i < item.getRenderOrders().length; i++) {
            ZZEffectElement e = new ZZEffectElement();
            e.initWithElement2d(element, 0, 0);//默认当前element对应第一张人脸
            if (item.getFaceIndexs() != null && item.getFaceIndexs().length > 0) {//指定当前element对应的人脸索引
                if(item.getFaceIndexs()[i] > ZZEffectCommon.ZZNumberOfFace - 1){
                    continue;
                }else{
                    e.faceIndex = item.getFaceIndexs()[i];
                }
            }
            //获取当前渲染顺序，代表在数组中的位置
            e.renderOrder = item.getRenderOrders()[i];
            _elements.add(e);
        }
    }

    public void updateWithFaceResult(List<ZZFaceResult> faceResult) {
        for (ZZEffectElement el : _elements) {
            //未配置对应人脸时，默认只配第一张人脸
            int[] tempFI = el.element2d._item.getFaceIndexs();
            if ((tempFI == null || tempFI.length <= 0) && faceResult.size() > 0) {
                drawElement(el.element2d, faceResult.get(0), 0);
            }else {
                int curIndex = el.faceIndex;
                if (curIndex == -1) {
                    el.element2d._curFaceIndex = ZZEffectCommon.ZZNumberOfFace;
                    el.element2d.updateWithFaceResult(_fullStickerResult);
                    el.element2d.render();
                    continue;
                }
                ZZFaceResult currentFace = curIndex >= faceResult.size() ? null : faceResult.get(curIndex);
                drawElement(el.element2d, currentFace, curIndex);
            }
        }
    }

    /**
     *  渲染指定的element
     *
     *  @param e           当前待渲染的element
     *  @param currentFace 当前element对应的人脸数据
     */
    private void drawElement(ZZEffect2DElement_v2 e, ZZFaceResult currentFace, int index) {
        e._curFaceIndex = index;
        if (currentFace == null) {
            e.updateWithNoFaceResult();
        }else {
            e.updateWithFaceResult(currentFace);
            e.render();
        }
    }

    public void renderStart() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glViewport(0, 0, bufferSize.width, bufferSize.height);
    }

    public int renderEnd() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return mFrameBufferTextures[0];
    }

    public void reset() {
        if(_elements != null && _elements.size() > 0) {
            for(ZZEffectElement e :_elements){
                e.element2d.reset();
            }
            _elements.clear();
        }
        verticesBuffer = null;
        textureCoordinatesBuffer = null;
        delete();
    }

    public void delete() {
        destroyFramebuffers();
    }

    private void initCameraFrameBuffer() {
        mFrameBuffers = new int[1];
        mFrameBufferTextures = new int[1];

        GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
        GLES20.glGenTextures(1, mFrameBufferTextures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bufferSize.width, bufferSize.height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    private void destroyFramebuffers() {
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(1, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
    }

}


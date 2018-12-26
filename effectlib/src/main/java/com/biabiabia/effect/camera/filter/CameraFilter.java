package com.biabiabia.effect.camera.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.support.annotation.RawRes;
import com.biabiabia.effect.R;
import com.biabiabia.effect.utils.OpenGlUtils;
import com.biabiabia.effect.utils.Rotation;
import com.biabiabia.effect.utils.TextureRotationUtil;
import com.biabiabia.effect.camera.gles.GlUtil;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class CameraFilter extends AbstractFilter implements IFilter {

    protected int mProgramHandle;
    private int maPositionLoc;
    private int muMVPMatrixLoc;
    private int muTexMatrixLoc;
    private int maTextureCoordLoc;
    private int mTextureLoc;

    protected FloatBuffer mGLCubeBuffer;
    protected FloatBuffer mGLTextureBuffer;

    protected int mIncomingWidth, mIncomingHeight;
    protected int mOutputWidth, mOutputHeight;

    protected int[] mFrameBuffers = null;
    protected int[] mFrameBufferTextures = null;

    public CameraFilter(Context applicationContext) {
        mGLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);
        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.getRotation(Rotation.NORMAL, false, true)).position(0);

        mProgramHandle = createProgram(applicationContext);
        if (mProgramHandle == 0) {
            //throw new RuntimeException("Unable to create program");
        }

        getGLSLValues();
    }

    @Override
    public void init() {
        onInit();
    }

    protected void onInit(){
    }

    public void extraInit(){
        initCameraFrameBuffer();
    }

    @Override
    public int getTextureTarget() {
        return GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
    }

    @Override
    public void setTextureSize(int width, int height) {
        if (width == 0 || height == 0) {
            return;
        }
        if (width == mIncomingWidth && height == mIncomingHeight) {
            return;
        }
        mIncomingWidth = width;
        mIncomingHeight = height;
    }

    @Override
    public void onDisplaySizeChanged(int width, int height) {
        mOutputWidth = width;
        mOutputHeight = height;
    }

    @Override
    protected int createProgram(Context applicationContext) {
        return GlUtil.createProgram(applicationContext, R.raw._vertex_shader,
                R.raw._fragment_shader_ext, QSFilterManager.FilterType.Normal.name());
    }

    @Override
    protected void getGLSLValues() {
        mTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexture");
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        muMVPMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        muTexMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexMatrix");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex,
                       int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix,
                       FloatBuffer texBuffer, int textureId, int texStride) {

        //todo   暂时去除了open gl 的错误检查以解决崩溃问题
        //GlUtil.checkGlError("draw start");

        useProgram();

        bindTexture(textureId);

        //runningOnDraw();

        bindGLSLValues(mvpMatrix, vertexBuffer==null?mGLCubeBuffer:vertexBuffer, coordsPerVertex, vertexStride,
                texMatrix, texBuffer==null?mGLTextureBuffer:texBuffer, texStride);

        drawArrays(firstVertex, vertexCount);

        unbindGLSLValues();

        unbindTexture();

        disuseProgram();
    }

    public int onDrawToTexture(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex,
                               int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix,
                               FloatBuffer texBuffer, int textureId, int texStride) {
        if(mFrameBuffers == null){
            return OpenGlUtils.NO_TEXTURE;
        }
        GLES20.glViewport(0, 0, mIncomingWidth, mIncomingHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);

        useProgram();
        bindTexture(textureId);
        bindGLSLValues(mvpMatrix, vertexBuffer==null?mGLCubeBuffer:vertexBuffer, coordsPerVertex, vertexStride,
                texMatrix, texBuffer==null?mGLTextureBuffer:texBuffer, texStride);
        drawArrays(firstVertex, vertexCount);
        unbindGLSLValues();
        unbindTexture();
        disuseProgram();

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(0, 0, mOutputWidth, mOutputHeight);
        return mFrameBufferTextures[0];
    }

    @Override
    protected void useProgram() {
        GLES20.glUseProgram(mProgramHandle);
        //GlUtil.checkGlError("glUseProgram");
    }

    @Override
    protected void bindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(mTextureLoc, 0);
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex,
                                  int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride,
                texBuffer);
    }

    @Override
    protected void drawArrays(int firstVertex, int vertexCount) {
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, firstVertex, vertexCount);
    }

    @Override
    protected void unbindGLSLValues() {
        GLES20.glDisableVertexAttribArray(maPositionLoc);
        GLES20.glDisableVertexAttribArray(maTextureCoordLoc);
    }

    @Override
    protected void unbindTexture() {
        GLES20.glBindTexture(getTextureTarget(), 0);
    }

    @Override
    protected void disuseProgram() {
        GLES20.glUseProgram(0);
    }

    @Override
    public void releaseProgram() {
        GLES20.glDeleteProgram(mProgramHandle);
        mProgramHandle = -1;
    }

    @Override
    public void destroy() {
        onDestroy();
    }

    protected void onDestroy() {
        releaseProgram();
        destroyFramebuffers();
    }

    protected void initCameraFrameBuffer() {
        if(mFrameBuffers != null){
            return;
        }
        if (mFrameBuffers == null && mIncomingWidth > 0 && mIncomingHeight > 0) {
            mFrameBuffers = new int[1];
            mFrameBufferTextures = new int[1];

            GLES20.glGenFramebuffers(1, mFrameBuffers, 0);
            GLES20.glGenTextures(1, mFrameBufferTextures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mIncomingWidth, mIncomingHeight, 0,
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
    }

    protected void destroyFramebuffers() {
        if (mFrameBufferTextures != null) {
            GLES20.glDeleteTextures(1, mFrameBufferTextures, 0);
            mFrameBufferTextures = null;
        }
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(1, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
    }

    protected Bitmap getTextureBitmap(Context context, @RawRes int rawId){
        InputStream in = context.getResources().openRawResource(rawId);
        Bitmap bitmap = BitmapFactory.decodeStream(in);
        try{
            in.close();
        }catch (Exception e){
        }
        return bitmap;
    }

    /////////// Set Runnable ////////////
    //protected void addRunnableOnDraw(final Runnable runnable) {
    //    synchronized (mRunnableOnDraw) {
    //        mRunnableOnDraw.addLast(runnable);
    //    }
    //}
    //
    //protected void setFloat(final int location, final float floatValue) {
    //    addRunnableOnDraw(new Runnable() {
    //        @Override public void run() {
    //            GLES20.glUniform1f(location, floatValue);
    //        }
    //    });
    //}
    //
    //@Override protected void runningOnDraw() {
    //    while (!mRunnableOnDraw.isEmpty()) {
    //        mRunnableOnDraw.removeFirst().run();
    //    }
    //}
}

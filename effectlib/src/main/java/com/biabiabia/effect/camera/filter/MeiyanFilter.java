package com.biabiabia.effect.camera.filter;

import android.content.Context;
import android.opengl.GLES20;
import com.biabiabia.effect.R;
import com.biabiabia.effect.camera.gles.GlUtil;

import java.nio.FloatBuffer;

public class MeiyanFilter extends CameraFilter {

    protected int mExtraTextureId;
    protected int maExtraTextureCoordLoc;
    protected int muExtraTextureLoc;

    public MeiyanFilter(Context context) {
        super(context);
    }

    protected void onInit(){
        super.onInit();
        initCameraFrameBuffer();
    }

    @Override protected int createProgram(Context applicationContext) {
        return GlUtil.createProgram(applicationContext, R.raw._vertex_shader_two_input,
         R.raw._fmeiyan2, QSFilterManager.FilterType.Meiyan.name());
    }

    @Override protected void getGLSLValues() {
        super.getGLSLValues();
        maExtraTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "aExtraTextureCoord");
        //muExtraTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "uExtraTexture");
    }

    @Override protected void bindTexture(int textureId) {
        super.bindTexture(textureId);
        //GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mExtraTextureId);
        //GLES20.glUniform1i(muExtraTextureLoc, 1);
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex,
            int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        super.bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix,
                texBuffer, texStride);
        GLES20.glEnableVertexAttribArray(maExtraTextureCoordLoc);
        GLES20.glVertexAttribPointer(maExtraTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride,
                texBuffer);
    }

    @Override protected void unbindGLSLValues() {
        super.unbindGLSLValues();
        GLES20.glDisableVertexAttribArray(maExtraTextureCoordLoc);
    }

    @Override protected void unbindTexture() {
        super.unbindTexture();
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    protected void onDestroy() {
        super.onDestroy();
        destroyFramebuffers();
    }

}
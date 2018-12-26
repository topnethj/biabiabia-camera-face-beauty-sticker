package com.biabiabia.effect.camera.filter;

import java.nio.FloatBuffer;

public interface IFilter {
    void init();

    void extraInit();

    int getTextureTarget();

    void setTextureSize(int width, int height);

    void onDisplaySizeChanged(int width, int height);

    void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount,
                int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer,
                int textureId, int texStride);

    int onDrawToTexture(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex,
                        int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix,
                        FloatBuffer texBuffer, int textureId, int texStride);

    void releaseProgram();

    void destroy();
}

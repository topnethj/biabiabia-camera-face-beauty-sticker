/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.douyaim.qsapp.camera.gles;

/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.graphics.Bitmap;
import android.opengl.Matrix;

import com.douyaim.effect.utils.OpenGlUtils;
import com.douyaim.qsapp.camera.filter.IFilter;

import java.nio.FloatBuffer;

/**
 * This class essentially represents a viewport-sized sprite that will be rendered with
 * a texture, usually from an external source like the camera or video decoder.
 */

public class FullFrameRect {
    private final Drawable2d mRectDrawable = new Drawable2d();
    private IFilter mFilter;
    public final float[] IDENTITY_MATRIX = new float[16];

    /**
     * Prepares the object.
     *
     * @param program The program to use.  FullFrameRect takes ownership, and will release
     * the program when no longer needed.
     */
    public FullFrameRect(IFilter program) {
        mFilter = program;
        Matrix.setIdentityM(IDENTITY_MATRIX, 0);
    }

    /**
     * Releases resources.
     * <p>
     * This must be called with the appropriate EGL context current (i.e. the one that was
     * current when the constructor was called).  If we're about to destroy the EGL context,
     * there's no value in having the caller make it current just to do this cleanup, so you
     * can pass a flag that will tell this function to skip any EGL-context-specific cleanup.
     */
    public void release(boolean doEglCleanup) {
        if (mFilter != null) {
            if (doEglCleanup) {
                mFilter.destroy();
            }
            mFilter = null;
        }
    }

    public void setFilter(IFilter filter) {
        this.mFilter = filter;
    }

    /**
     * Returns the program currently in use.
     */
    public IFilter getFilter() {
        return mFilter;
    }

    /**
     * Changes the program.  The previous program will be released.
     * <p>
     * The appropriate EGL context must be current.
     */
    public void changeProgram(IFilter newFilter) {
        if(mFilter != null){
            mFilter.destroy();
        }
        mFilter = newFilter;
    }

    /**
     * Creates a texture object suitable for use with drawFrame().
     */
    public int createTexture() {
        return GlUtil.createTexture(mFilter.getTextureTarget());
    }

    public int createTexture(Bitmap bitmap) {
        return GlUtil.createTexture(mFilter.getTextureTarget(), bitmap);
    }

    public void scaleMVPMatrix(float x, float y) {
        Matrix.setIdentityM(IDENTITY_MATRIX, 0);
        Matrix.scaleM(IDENTITY_MATRIX, 0, x, y, 1f);
    }

    /**
     * Draws a viewport-filling rect, texturing it with the specified texture object.
     */
    public void drawFrame(int textureId, float[] texMatrix, boolean dCoordinate) {
        // Use the identity matrix for MVP so our 2x2 FULL_RECTANGLE covers the viewport.
        if(mFilter == null){
            return;
        }
        mFilter.onDraw(IDENTITY_MATRIX, dCoordinate?null:mRectDrawable.getVertexArray(), 0,
                mRectDrawable.getVertexCount(), mRectDrawable.getCoordsPerVertex(),
                mRectDrawable.getVertexStride(), texMatrix, dCoordinate?null:mRectDrawable.getTexCoordArray(),
                textureId, mRectDrawable.getTexCoordStride());
    }

    public int drawTexture(int textureId, float[] texMatrix) {
        if(mFilter == null){
            return OpenGlUtils.NO_TEXTURE;
        }
        return mFilter.onDrawToTexture(IDENTITY_MATRIX, mRectDrawable.getVertexArray(), 0,
                mRectDrawable.getVertexCount(), mRectDrawable.getCoordsPerVertex(),
                mRectDrawable.getVertexStride(), texMatrix, mRectDrawable.getTexCoordArray(),
                textureId, mRectDrawable.getTexCoordStride());
    }

    public int drawTexture(int textureId, float[] texMatrix, FloatBuffer vertexBuffer, FloatBuffer texBuffer) {
        if(mFilter == null){
            return OpenGlUtils.NO_TEXTURE;
        }
        return mFilter.onDrawToTexture(IDENTITY_MATRIX, vertexBuffer, 0,
                mRectDrawable.getVertexCount(), mRectDrawable.getCoordsPerVertex(),
                mRectDrawable.getVertexStride(), texMatrix, texBuffer,
                textureId, mRectDrawable.getTexCoordStride());
    }

}

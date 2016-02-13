package com.pheiffware.lib.graphics;

import android.opengl.GLES20;

/**
 * Created by Steve on 2/13/2016.
 */
public enum FilterQuality {
    LOW // No mip-mapping, use "nearest" filter for everything
            {
                @Override
                public void applyToBoundTexture(boolean generateMipMaps) {
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
                }
            },
    MEDIUM // Allow mip-mapping, use "nearest" filter for everything
            {
                @Override
                public void applyToBoundTexture(boolean generateMipMaps) {
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
                    if (generateMipMaps) {
                        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
                        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST_MIPMAP_NEAREST);
                    } else {
                        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                    }

                }
            },
    HIGH // Allow mip-mapping, use linear filter for everything
            {
                @Override
                public void applyToBoundTexture(boolean generateMipMaps) {
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                    if (generateMipMaps) {
                        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
                        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
                    } else {
                        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                    }
                }
            };

    public abstract void applyToBoundTexture(boolean generateMipMaps);
}

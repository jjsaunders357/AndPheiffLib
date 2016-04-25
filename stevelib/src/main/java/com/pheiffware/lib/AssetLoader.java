package com.pheiffware.lib;

import com.pheiffware.lib.graphics.FilterQuality;
import com.pheiffware.lib.graphics.GraphicsException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Manages loading of files/resources from disc for the library.  Allows platform neutral ways of accessing resources, such as image files, shader code, etc.
 * <p/>
 * The assetPath argument to various methods, refers to some system resource such as a file path to load from.
 * <p/>
 * Created by Steve on 4/23/2016.
 */
public abstract class AssetLoader
{
    /**
     * Loads an image into a newly created texture.
     *
     * @param assetPath       image path
     * @param generateMipMaps Set to true if it makes sense to try to use mip-maps for this texture. This may be ignored based on given filter quality.
     * @param filterQuality   HIGH/MEDIUM/LOW (look up my definition)
     * @param sWrapMode       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @param tWrapMode       typically: GL_CLAMP_TO_EDGE, GL_CLAMP_TO_BORDER, GL_MIRRORED_REPEAT, GL_REPEAT
     * @return GL handle to texture
     * @throws GraphicsException
     */
    public abstract int loadGLTextureFromImage(String assetPath, boolean generateMipMaps,
                                               FilterQuality filterQuality, int sWrapMode, int tWrapMode) throws GraphicsException;


    /**
     * Load the contents of the given assetPath as a String.
     *
     * @param assetPath
     * @return
     */
    public abstract String loadAssetAsString(String assetPath) throws IOException;

    public abstract InputStream getInputStream(String assetPath) throws IOException;
}

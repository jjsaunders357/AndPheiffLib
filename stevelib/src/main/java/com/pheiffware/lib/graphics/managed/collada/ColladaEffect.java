package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.graphics.GColor;

/**
 * Holds the name of a reference key to a file name and color information.
 * Created by Steve on 2/15/2016.
 */
class ColladaEffect
{
    //Collada stores a reference to an entry which stores the file name, rather than directly storing file name.  This can be null if there is no texture.  Not really worth the trouble if you ask me.
    public final String imageFileNameKey;
    public final GColor ambientColor;
    //This will be null if an imageFileNameKey is defined
    public final GColor diffuseColor;
    public final GColor specularColor;
    public final float shininess;

    public ColladaEffect(String imageLibraryReference, GColor ambientColor, GColor diffuseColor, GColor specularColor, float shininess)
    {
        this.imageFileNameKey = imageLibraryReference;
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.shininess = shininess;
    }
}

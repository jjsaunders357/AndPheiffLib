package com.pheiffware.lib.graphics.managed.mesh;

import com.pheiffware.lib.graphics.GColor;

import org.w3c.dom.Element;

/**
 * Created by Steve on 2/14/2016.
 */
public class Material
{
    public final String imageFileName;
    public final GColor ambientColor;
    public final GColor diffuseColor;
    public final GColor specularColor;
    public final float shininess;

    public Material(String imageFileName, GColor ambientColor, GColor diffuseColor, GColor specularColor, float shininess)
    {
        this.imageFileName = imageFileName;
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.shininess = shininess;
    }
}

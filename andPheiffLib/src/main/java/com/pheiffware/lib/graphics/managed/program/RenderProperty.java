package com.pheiffware.lib.graphics.managed.program;

/**
 * An enum containing properties for ALL techniques.
 * <p/>
 * Created by Steve on 4/24/2016.
 */
public enum RenderProperty
{
    //2D
    //Texture used as an image for 2D rendering
    IMAGE_TEXTURE,

    //Holds the projection matrix
    PROJECTION_MATRIX,

    //Holds the view matrix
    VIEW_MATRIX,

    //Holds the model matrix
    MODEL_MATRIX,

    //Position/color of all positional lights along with their on/off status
    LIGHTING,

    //If the material is a solid color, this is used for diffuse and ambient lighting
    MAT_COLOR,

    //If the material is textured, this texture is used for diffuse and ambient lighting
    MAT_COLOR_TEXTURE,

    //The spectral lighting color of the material
    SPEC_MAT_COLOR,

    //The shininess of the material (exponent used during spectral lighting equations)
    SHININESS,

    //Contains data related to holographic projection
    HOLO_PROJECTION,

    //The position of a light to use to render a depth buffer from
    LIGHT_RENDER_POSITION,

    //This is the texture containing depth
    DEPTH_TEXTURE,

    //Array of depth textures, one per light.  Lights which don't use this will have null entries.
    CUBE_DEPTH_TEXTURES,

    SHADOW_PROJECTION_MAX_DEPTH,
    DEPTH_Z_CONST, DEPTH_Z_FACTOR,
    SPHERE_PROJECTION
}

#include include/consts.glsl

//Is the light on?
uniform bool onState[numLights];

//Position of light
uniform vec4 lightPositionEyeSpace[numLights];

//The light color * specular material color
uniform vec4 specLightMaterialColor[numLights];

#if texturedMaterial
    //The light color
    uniform vec4 lightColor[numLights];

     //Ambient light color
    uniform vec4 ambientLightColor;
#else
    //The light color * diff material color (pre-multiplied)
    uniform vec4 diffuseLightMaterialColor[numLights];

    //The ambient light color * material color (pre-multiplied)
    uniform vec4 ambientLightMaterialColor;
#endif
#type FRAGMENT
#include include/lightingInputs.glsl
#include include/lightingCalcs.glsl
#version 300 es
precision mediump float;

//Position of lights in absolute space
uniform vec4 lightPositionAbs[numLights];

// How shiny the material is.  This determines the exponent used in rendering.
uniform float shininess;

// How opaque the material.  Typically this, plus the specular highlighting will determine opaqueness.
uniform float materialAlpha;

//Position of the eye
uniform highp vec4 eyePosition;

//Position/normal of point being rendered in screen space
in vec4 fragPositionAbs;
in vec3 normalAbs;

//Color of the screen's surface.  Anything with a z position < 0, has this blended in.
uniform vec4 screenColor;

layout(location = 0) out vec4 fragColor;

vec4 blendScreen(vec4 color)
{
    return vec4(mix(color.rgb, screenColor.rgb, screenColor.a), color.a);
}

void main()
{
    vec4 totalLightMaterialColor = ambientLightMaterialColor;
    for(int i=0;i<numLights;i++)
    {
        if(onState[i])
        {
            totalLightMaterialColor += calcLightColor(fragPositionAbs.xyz - lightPositionAbs[i].xyz, //light to fragment vector
                                     eyePosition.xyz-fragPositionAbs.xyz,                            //fragment to eye vector
                                     normalAbs,                                                      //Surface normal
                                     diffuseLightMaterialColor[i],                                   //Light * diffuse material color
                                     specLightMaterialColor[i],                                      //Light * specular material color
                                     shininess);                                                     //Material shininess
        }
    }
    if(fragPositionAbs.z < 0.0)
    {
        totalLightMaterialColor = blendScreen(totalLightMaterialColor);
    }
	fragColor = totalLightMaterialColor + vec4(0.0, 0.0, 0.0, materialAlpha);
}


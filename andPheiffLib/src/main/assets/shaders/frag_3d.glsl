#type FRAGMENT
#include include/consts.glsl
#include include/lightingInputs.glsl
#include include/shadowInputs.glsl
#include include/lightingCalcs.glsl

#version 300 es
//TODO 0.33 = 2/6: Optimize precision
precision highp float;

// How shiny the material is.  This determines the exponent used in rendering.
uniform float shininess;

//Position of point being rendered in eye space
in vec4 positionEyeSpace;
in vec3 normalEyeSpace;

#if texturedMaterial
    //The diffuse material color sampler
    uniform mediump sampler2D diffuseMaterialColorSampler;

    //Material texture coordinate
    in vec2 texCoord;
#else
    // How opaque the material.  Typically this, plus the specular highlighting will determine opaqueness.
    uniform float materialAlpha;
#endif

layout(location = 0) out vec4 fragColor;

void performIteration(inout vec4 totalLightMaterialColor, vec4 diffuseLightMaterialColor, int lightIndex, mediump samplerCubeShadow cubeDepthSampler)
{
    if(onState[0])
    {
        vec4 color = calcLightColor(positionEyeSpace.xyz-lightPositionEyeSpace[lightIndex].xyz,  //light to fragment vector
                             -positionEyeSpace.xyz,                                 //fragment to eye vector (eye is at 0)
                             normalEyeSpace,                                        //Surface normal
                             diffuseLightMaterialColor,                             //Light * diffuse material color
                             specLightMaterialColor[lightIndex],                    //Light * specular material color
                             shininess);                                            //Material shininess
        if(castsCubeShadow[lightIndex])
        {
            applyShadow(color, fragPositionAbs, lightPositionAbs[lightIndex], cubeDepthSampler, shadowProjectionMaxDepth);
        }
        totalLightMaterialColor += color;
    }
}

void main()
{
    #if texturedMaterial
        //Base color of material
        vec4 sampledColor = texture(diffuseMaterialColorSampler,texCoord);

        //Base material color used for ambient and diffuse lighting adds 0.0 opaqueness
        vec4 materialColor = vec4(sampledColor.rgb,0.0);

        //The material alpha at this point is added in at the end, irrespective of lighting
        float materialAlpha = sampledColor.a;

        //Calc ambient color
        vec4 ambientLightMaterialColor = materialColor * ambientLightColor;

        vec4 totalLightMaterialColor = ambientLightMaterialColor;

        performIteration(totalLightMaterialColor,materialColor * lightColor[0],0,cubeDepthSampler0);
        performIteration(totalLightMaterialColor,materialColor * lightColor[1],1,cubeDepthSampler1);
        performIteration(totalLightMaterialColor,materialColor * lightColor[2],2,cubeDepthSampler2);
        performIteration(totalLightMaterialColor,materialColor * lightColor[3],3,cubeDepthSampler3);
    #else
        vec4 totalLightMaterialColor = ambientLightMaterialColor;
        performIteration(totalLightMaterialColor,diffuseLightMaterialColor[0],0,cubeDepthSampler0);
        performIteration(totalLightMaterialColor,diffuseLightMaterialColor[1],1,cubeDepthSampler1);
        performIteration(totalLightMaterialColor,diffuseLightMaterialColor[2],2,cubeDepthSampler2);
        performIteration(totalLightMaterialColor,diffuseLightMaterialColor[3],3,cubeDepthSampler3);
    #endif

    //Color of fragment is the combination of all colors
    fragColor = totalLightMaterialColor + vec4(0.0, 0.0, 0.0, materialAlpha);
}
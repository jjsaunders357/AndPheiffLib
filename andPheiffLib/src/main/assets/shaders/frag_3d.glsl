#type FRAGMENT
#include include/lightingUniforms.glsl
#include include/lightingCalcs.glsl
#version 300 es
//TODO 0.33 = 2/6: Optimize precision
precision highp float;

// How shiny the material is.  This determines the exponent used in rendering.
uniform float shininess;

//Position of point being rendered in eye space
in vec4 positionEyeSpace;
in vec3 normalEyeSpace;

#if enableShadows
    //Position of point being rendered in absolute space
    in vec3 fragPositionAbs;
#endif

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

vec4 performLightCalcs(int lightIndex,vec4 diffuseLightMaterialColor,mediump samplerCubeShadow cubeDepthSampler)
{
        if(onState[lightIndex])
        {
            vec4 color = calcLightColor(positionEyeSpace.xyz-lightPositionEyeSpace[lightIndex].xyz,  //light to fragment vector
                                     -positionEyeSpace.xyz,                                 //fragment to eye vector (eye is at 0)
                                     normalEyeSpace,                                        //Surface normal
                                     diffuseLightMaterialColor,                             //Light * diffuse material color
                                     specLightMaterialColor[lightIndex],                    //Light * specular material color
                                     shininess);                                            //Material shininess
            #if enableShadows
                color.rgb = color.rgb * calcCubeShadow(fragPositionAbs, lightPositionAbs[lightIndex], cubeDepthSampler, shadowProjectionMaxDepth);
//                color.rgb = color.rgb * calcCubeShadow(fragPositionAbs, lightPositionAbs[lightIndex], cubeDepthSampler, depthZConst, depthZFactor);
            #endif
            return color;
        }
        else
        {
            return vec4(0.0,0.0,0.0,0.0);
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

        totalLightMaterialColor += performLightCalcs(0,materialColor * lightColor[0],cubeDepthSampler0);
        totalLightMaterialColor += performLightCalcs(1,materialColor * lightColor[1],cubeDepthSampler1);
    #else
        vec4 totalLightMaterialColor = ambientLightMaterialColor;

        totalLightMaterialColor += performLightCalcs(0,diffuseLightMaterialColor[0],cubeDepthSampler0);
        totalLightMaterialColor += performLightCalcs(1,diffuseLightMaterialColor[1],cubeDepthSampler1);
    #endif

    //Color of fragment is the combination of all colors
    fragColor = totalLightMaterialColor + vec4(0.0, 0.0, 0.0, materialAlpha);
}
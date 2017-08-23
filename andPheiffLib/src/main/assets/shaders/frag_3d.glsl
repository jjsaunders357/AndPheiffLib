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
    //TODO 3.0 = 6/2: Wrong! this needs to reference the SHADOW MAP's max depth!!
    //Maximum depth projected into texture
    uniform float projectionMaxDepth;

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

void main()
{
    #if texturedMaterial
        //Base color of material
        vec4 sampledColor = texture(diffuseMaterialColorSampler,texCoord);

        //Base material color used for ambient and diffuse lighting adds 0.0 opaqueness
        vec4 baseMaterialColor = vec4(sampledColor.rgb,0.0);

        //The material alpha at this point is added in at the end, irrespective of lighting
        float materialAlpha = sampledColor.a;

        //Calc ambient color
        vec4 ambientLightMaterialColor = baseMaterialColor * ambientLightColor;
    #endif

    vec4 totalLightMaterialColor = ambientLightMaterialColor;
    for(int i=0;i<numLights;i++)
    {
        if(onState[i])
        {
            #if texturedMaterial
                vec4 diffuse = baseMaterialColor * lightColor[i];
            #else
                vec4 diffuse = diffuseLightMaterialColor[i];
            #endif
            vec4 color = calcLightColor(positionEyeSpace.xyz-lightPositionEyeSpace[i].xyz,  //light to fragment vector
                                     -positionEyeSpace.xyz,                                 //fragment to eye vector (eye is at 0)
                                     normalEyeSpace,                                        //Surface normal
                                     diffuse,                                               //Light * diffuse material color
                                     specLightMaterialColor[i],                             //Light * specular material color
                                     shininess);                                            //Material shininess
            #if enableShadows
                color.rgb = color.rgb * calcCubeShadow(fragPositionAbs, lightPositionAbs[i], cubeDepthSampler, projectionMaxDepth);
            #endif
            totalLightMaterialColor += color;
        }
    }
    //Color of fragment is the combination of all colors
    fragColor = totalLightMaterialColor + vec4(0.0, 0.0, 0.0, materialAlpha);
}
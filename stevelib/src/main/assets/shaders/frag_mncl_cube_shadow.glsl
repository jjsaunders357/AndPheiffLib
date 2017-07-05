#version 300 es
precision highp float;

const float ZERO=0.0;
const int numLights = 4;

//Is the light on?
uniform bool onState[numLights];

//Position of light
uniform vec4 lightPositionEyeSpace[numLights];

//Position of light in absolute space
uniform vec4 lightPosition[numLights];

//The light color * specular material color
uniform vec4 specLightMaterialColor[numLights];

//The light color * diff material color
uniform vec4 diffuseLightMaterialColor[numLights];

//The ambient light color * material color
uniform vec4 ambientLightMaterialColor;

// How shiny the material is.  This determines the exponent used in rendering.
uniform float shininess;

// How opaque the material.  Typically this, plus the specular highlighting will determine opaqueness.
uniform float materialAlpha;

//Maximum depth projected into texture
uniform float projectionMaxDepth;

uniform mediump samplerCube cubeDepthSampler;

//Position of point being rendered in eye space
in vec4 positionEyeSpace;
in vec3 normalEyeSpace;

//Position of point being rendered in absolute space
in vec3 absPosition;

layout(location = 0) out vec4 fragColor;

vec4 light_color(vec4 absLightPosition, vec4 lightPositionEyeSpace, vec4 diffuseLightMaterialColor, vec4 specLightMaterialColor)
{
    //Normalize the surface's normal
    vec3 surfaceNormal = normalize(normalEyeSpace);

    //Incoming light vector to current position
    vec3 incomingLightDirection = normalize(positionEyeSpace.xyz-lightPositionEyeSpace.xyz);

    //Reflected light vector from current position
    vec3 outgoingLightDirection = reflect(incomingLightDirection,surfaceNormal);

    //Vector from position to eye.  Since all geometry is assumed to be in eye space, the eye is always at the origin.
    vec3 positionToEyeDirection = normalize(-positionEyeSpace.xyz);

    //Calculate how bright various types of light are
	float diffuseBrightness = max(dot(incomingLightDirection,-surfaceNormal),ZERO);
	float specBrightness = max(dot(outgoingLightDirection, positionToEyeDirection),ZERO);
    specBrightness = pow(specBrightness,shininess);

    vec4 color = diffuseBrightness * diffuseLightMaterialColor + specBrightness * specLightMaterialColor;

    vec3 lightToPositionAbs = absPosition - absLightPosition.xyz;
    float depthSample = texture(cubeDepthSampler, lightToPositionAbs).r;
    depthSample = depthSample * projectionMaxDepth;

    float depth = max(max(abs(lightToPositionAbs.x),abs(lightToPositionAbs.y)),abs(lightToPositionAbs.z));
    float visibleness = step(depth,depthSample+0.1);

	return vec4(visibleness*color.rgb,color.a);
}

void main()
{
    vec4 totalLightMaterialColor = ambientLightMaterialColor;
    for(int i=0;i<numLights;i++)
    {
        if(onState[i])
        {
            totalLightMaterialColor += light_color(lightPosition[i],lightPositionEyeSpace[i],diffuseLightMaterialColor[i],specLightMaterialColor[i]);
        }
    }
    //Color of fragment is the combination of all colors
	fragColor = totalLightMaterialColor + vec4(ZERO, ZERO, ZERO, materialAlpha);
}


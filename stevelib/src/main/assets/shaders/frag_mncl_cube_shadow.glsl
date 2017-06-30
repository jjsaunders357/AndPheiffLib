#version 300 es
precision highp float;

const float zero=0.0;
const int numLights = 4;

//Is the light on?
uniform bool onState[numLights];

//Position of light
uniform vec4 lightPositionEyeSpace[numLights];

//Position of light in absolute space
uniform vec4 lightPositionAbsoluteSpace[numLights];

//The light color * specular material color
uniform vec4 specLightMaterialColor[numLights];

//The light color * diff material color
uniform vec4 diffuseLightMaterialColor[numLights];

//The ambient light color * material color
uniform vec4 ambientLightMaterialColor;

// How shiny the material is.  This determines the exponent used in rendering.
uniform float shininess;

//Maximum distance the light shines.  This is used to uspack the distance from the value in the depth buffer.
uniform float maxLightDistanceSquared;

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
	float diffuseBrightness = max(dot(incomingLightDirection,-surfaceNormal),zero);
	float specBrightness = max(dot(outgoingLightDirection, positionToEyeDirection),zero);
    specBrightness = pow(specBrightness,shininess);

    vec3 lightToPositionAbs = absPosition - absLightPosition.xyz;
    float depthSample = texture(cubeDepthSampler, lightToPositionAbs).r;
    //float depthSample = texture(cubeDepthSampler, vec3(0.0,0.0,-1.0)).r;
    float trash=pow((depthSample+maxLightDistanceSquared+lightToPositionAbs.x)/10000.0,10.0);

    float distanceSquared = dot(lightToPositionAbs,lightToPositionAbs);
    float sampledDistanceSquared = depthSample * maxLightDistanceSquared;

//    float depthSample = texture(cubeDepthSampler, vec4(lightToPositionAbs,0.01)).x;
    //depthSample = 2.0 * depthSample - 1.0;
    //If sampledDistanceSquared<distance squared then this is 0, otherwise 1.  It uses the slight bias given.
    //float visibleness = step(distanceSquared,sampledDistanceSquared+400.01);
//    float visibleness = step(0.41,depthSample+0.01);
    float visibleness = step(distanceSquared,sampledDistanceSquared+0.5);
    //is visible if x<y
    //TODO: Shaders using color, need to remove alpha from the colors.  Instead, should use 3 components for all colors and provide single transparency value
   	//Sum (light brightness) * (light color) * (material color) for diff and spec.
	return visibleness*(diffuseBrightness * diffuseLightMaterialColor + specBrightness * specLightMaterialColor)*(1.0+trash);
}

void main()
{
    vec4 totalLightMaterialColor = ambientLightMaterialColor;
    for(int i=0;i<numLights;i++)
    {
        if(onState[i])
        {
            totalLightMaterialColor += light_color(lightPositionAbsoluteSpace[i],lightPositionEyeSpace[i],diffuseLightMaterialColor[i],specLightMaterialColor[i]);
        }
    }
    //Color of fragment is the combination of all colors
	fragColor = vec4(totalLightMaterialColor.xyz,1);
}


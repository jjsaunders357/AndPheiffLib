#version 300 es
precision mediump float;

const float zero=0.0;
const int numLights = 4;

//Is the light on?
uniform bool onState[numLights];

//Position of light
uniform vec4 lightPositionEyeSpace[numLights];

//Position of light in absolute space
uniform vec3 lightPositionAbsoluteSpace[numLights];

//The light color * specular material color
uniform vec4 specLightMaterialColor[numLights];

//The light color * diff material color
uniform vec4 diffuseLightMaterialColor[numLights];

//The ambient light color * material color
uniform vec4 ambientLightMaterialColor;

// How shiny the material is.  This determines the exponent used in rendering.
uniform float shininess;

uniform lowp samplerCubeShadow cubeDepthSampler;

//Position of point being rendered in eye space
in vec4 positionEyeSpace;
in vec3 normalEyeSpace;

//Position of point being rendered in absolute space
in vec3 absPosition;


layout(location = 0) out vec4 fragColor;

vec4 light_color(vec3 absLightPosition, vec4 lightPositionEyeSpace, vec4 diffuseLightMaterialColor, vec4 specLightMaterialColor)
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


    vec3 lightToPositionAbs = absPosition - absLightPosition;

    //Distance1 = distance value sampled from cube map based on vector from light position, to position being rendered (in absolute space).
    //Distance2 = the distance between the light and the point being rendered.

    float near = 0.1;
    float far = 100.0;
    float fakeDepth = (length(lightToPositionAbs)*(far+near)/(2.0*far*near)+1.0)/2.0-19.0;

    float visibleness = texture(cubeDepthSampler,vec4(lightToPositionAbs,fakeDepth));
    //visibleness=visibleness*0.0001+1.0;
	//Sum (light brightness) * (light color) * (material color) for diff and spec.
	return visibleness*(diffuseBrightness * diffuseLightMaterialColor + specBrightness * specLightMaterialColor);
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
	fragColor = totalLightMaterialColor;
}


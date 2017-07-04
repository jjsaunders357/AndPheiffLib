#version 300 es
precision mediump float;

const float ZERO=0.0;
const int numLights = 4;

//Is the light on?
uniform bool onState[numLights];

//Position of light
uniform vec4 lightPositionEyeSpace[numLights];

//The light color * specular material color (this will carry opaqueness typically)
uniform vec4 specLightMaterialColor[numLights];

//The light color * diff material color (this will be transparent typically as it adds nothing to determining opaqueness)
uniform vec4 diffuseLightMaterialColor[numLights];

//The ambient light color * material color (this will be transparent typically as it adds nothing to determining opaqueness)
uniform vec4 ambientLightMaterialColor;

// How shiny the material is.  This determines the exponent used in rendering.
uniform float shininess;

// How opaque the material.  Typically this, plus the specular highlighting will determine opaqueness.
uniform float materialAlpha;

//Position of point being rendered in eye space
in vec4 positionEyeSpace;
in vec3 normalEyeSpace;

layout(location = 0) out vec4 fragColor;

vec4 light_color(vec4 lightPositionEyeSpace,vec4 diffuseLightMaterialColor, vec4 specLightMaterialColor)
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

	//Sum (light brightness) * (light color) * (material color) for diff and spec.
	return diffuseBrightness * diffuseLightMaterialColor + specBrightness * specLightMaterialColor;
}
void main()
{
    vec4 totalLightMaterialColor = ambientLightMaterialColor;
    for(int i=0;i<numLights;i++)
    {
        if(onState[i])
        {
            totalLightMaterialColor += light_color(lightPositionEyeSpace[i],diffuseLightMaterialColor[i],specLightMaterialColor[i]);
        }
    }
    //Color of fragment is the combination of all colors
	fragColor = totalLightMaterialColor + vec4(ZERO, ZERO, ZERO,materialAlpha);
}


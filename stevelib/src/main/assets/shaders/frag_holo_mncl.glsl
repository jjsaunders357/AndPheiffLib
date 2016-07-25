precision mediump float;

const float ZERO=0.0;
const float ONE=1.0;
const int numLights = 4;

//Is the light on?
uniform bool onState[numLights];

//Position of light
uniform vec4 lightPosition[numLights];

//The light color * specular material color
uniform vec4 specLightMaterialColor[numLights];

//The light color * diff material color
uniform vec4 diffuseLightMaterialColor[numLights];

//The ambient light color * material color
uniform vec4 ambientLightMaterialColor;

// How shiny the material is.  This determines the exponent used in rendering.
uniform float shininess;

//Position of the eye
uniform vec4 eyePosition;

//Position/normal of point being rendered in screen space
varying vec4 position;
varying vec3 normal;

//Color of the screen's surface.  Anything with a z position < 0, has this blended in.
uniform vec4 screenColor;

//Near visible plane, relative to eye.  In other words, minimum distance from eye, in z direction, towards screen, which is visible.
uniform float zNear;

//Near zDistance at which fragments start to fade.  They completely disappear by the time they reach zNear
uniform float zNearFadeStart;

vec4 light_color(vec4 lightPosition,vec4 diffuseLightMaterialColor, vec4 specLightMaterialColor)
{
    //Normalize the surface's normal
    vec3 surfaceNormal = normalize(normal);

    //Incoming light vector to current position
    vec3 incomingLightDirection = normalize(position.xyz - lightPosition.xyz);

    //Reflected light vector from current position
    vec3 outgoingLightDirection = reflect(incomingLightDirection,surfaceNormal);

    //Vector from position to eye.  Since all geometry is assumed to be in eye space, the eye is always at the origin.
    vec3 positionToEyeDirection = normalize(eyePosition.xyz-position.xyz);

    //Calculate how bright various types of light are
	float diffuseBrightness = max(dot(incomingLightDirection,-surfaceNormal),ZERO);
	float specBrightness = max(dot(outgoingLightDirection, positionToEyeDirection),ZERO);
    specBrightness = pow(specBrightness,shininess);

	//Sum (light brightness) * (light color) * (material color) for diff and spec.
	return diffuseBrightness * diffuseLightMaterialColor + specBrightness * specLightMaterialColor;
}

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
            totalLightMaterialColor += light_color(lightPosition[i],diffuseLightMaterialColor[i],specLightMaterialColor[i]);
        }
    }
    if(position.z < ZERO)
    {
        totalLightMaterialColor = blendScreen(totalLightMaterialColor);
    }

    if(eyePosition.z-position.z < zNearFadeStart)
    {
        float alpha = min((eyePosition.z-position.z-zNear)/(zNearFadeStart-zNear),ONE);
        totalLightMaterialColor.a*=alpha;
    }
    //float alpha = clamp((eyePosition.z-position.z-zNear)/(zNearFadeStart-zNear),ZERO,ONE);

//    float alpha = clamp((eyePosition.z-position.z-zNear)/(zNearFadeStart-zNear),ZERO,ONE);
//    totalLightMaterialColor.a *= alpha * 0.000001;
	gl_FragColor = totalLightMaterialColor;
}


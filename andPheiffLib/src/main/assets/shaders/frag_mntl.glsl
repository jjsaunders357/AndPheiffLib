#version 300 es
precision mediump float;

const float ZERO=0.0;
const int numLights = 4;

//Is the light on?
uniform bool onState[numLights];

//Position of light
uniform vec4 lightPositionEyeSpace[numLights];

//The light color * specular material color
uniform vec4 specLightMaterialColor[numLights];

//Light color
uniform vec4 lightColor[numLights];

//Ambient light color
uniform vec4 ambientLightColor;

// How shiny the material is.  This determines the exponent used in rendering.
uniform float shininess;

//Texture color of object
uniform sampler2D materialColorSampler;

//Position of point being rendered in eye space
in vec4 positionEyeSpace;
in vec3 normalEyeSpace;
in vec2 texCoord                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        ;

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
    //Base color of material
    vec4 sampledColor = texture(materialColorSampler,texCoord);

    //Base material color used for ambient and diffuse lighting adds 0.0 opaqueness
    vec4 baseMaterialColor = vec4(sampledColor.rgb,ZERO);

    //The material alpha at this point is added in at the end, irrespective of lighting
    float materialAlpha = sampledColor.a;

    //Calc ambient color
    vec4 ambientLightMaterialColor = baseMaterialColor * ambientLightColor;

    vec4 totalLightMaterialColor = ambientLightMaterialColor;
    for(int i=0;i<numLights;i++)
    {
        if(onState[i])
        {
            //Calc diffuse color
            vec4 diffuseLightMaterialColor = baseMaterialColor * lightColor[i];
            totalLightMaterialColor += light_color(lightPositionEyeSpace[i],diffuseLightMaterialColor,specLightMaterialColor[i]);
        }
    }
    fragColor = totalLightMaterialColor + vec4(ZERO, ZERO, ZERO, materialAlpha);
}

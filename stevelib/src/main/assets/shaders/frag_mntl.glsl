precision mediump float;

//Specular color of material - Assume specular always reflects all light
const vec4 specMaterialColor = vec4(1.0,1.0,1.0,1.0);

//Texture color of object
uniform sampler2D materialColorTexture;

//Position of light
uniform vec3 lightPosition;

//Light color and intensity
uniform vec4 lightColorIntensity;

//Ambient light color and intensity
uniform vec4 ambientLightColorIntensity;

// How shiny the material is.  This determines the exponent used in rendering.
uniform float shininess;

//From vertex shader
varying vec4 varyingPosition;
varying vec3 varyingNormal;
varying vec2 varyingTexCoord                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        ;


void main()
{
    //Base color of material
    vec4 baseMaterialColor = texture2D(materialColorTexture,varyingTexCoord);

    //Normalize the surface's normal
    vec3 surfaceNormal = normalize(varyingNormal);

    //Calc ambient color
    vec4 ambientColor = baseMaterialColor * ambientLightColorIntensity;
    //Calc diffuse color
    vec4 diffuseColor = baseMaterialColor * lightColorIntensity;
    //Calc specular color
    vec4 specColor = specMaterialColor * lightColorIntensity;

    //Incoming light vector to current position
    vec3 incomingLightDirection = normalize(varyingPosition.xyz-lightPosition);

    //Reflected light vector from current position
    vec3 outgoingLightDirection = reflect(incomingLightDirection,surfaceNormal);

    //Vector from position to eye.  Since all geometry is assumed to be in eye space, the eye is always at the origin.
    vec3 positionToEyeDirection = normalize(-varyingPosition.xyz);

    //Calculate how bright various types of light are
	float diffuseBrightness = max(dot(incomingLightDirection,-surfaceNormal),0.0);
	float specBrightness = max(dot(outgoingLightDirection, positionToEyeDirection),0.0);
    specBrightness = pow(specBrightness,shininess);

    //Color of fragment is the combination of all colors
	gl_FragColor = ambientColor + diffuseBrightness * diffuseColor + specBrightness * specColor;
}

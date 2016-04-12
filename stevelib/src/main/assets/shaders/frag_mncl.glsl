precision mediump float;


//Position of light
uniform vec3 lightPositionEyeSpace;

//TODO: Make customizable
//Specular color of material - Assume specular always reflects all light
const vec4 specLightMaterialColor = vec4(1.0,1.0,1.0,1.0);

//The light color * material color
uniform vec4 diffuseLightMaterialColor;

//The ambient light color * material color
uniform vec4 ambientLightMaterialColor;

// How shiny the material is.  This determines the exponent used in rendering.
uniform float shininess;

//From vertex shader
varying vec4 positionEyeSpace;
varying vec3 normalEyeSpace;

void main()
{
    //Normalize the surface's normal
    vec3 surfaceNormal = normalize(normalEyeSpace);

    //Incoming light vector to current position
    vec3 incomingLightDirection = normalize(positionEyeSpace.xyz-lightPositionEyeSpace);

    //Reflected light vector from current position
    vec3 outgoingLightDirection = reflect(incomingLightDirection,surfaceNormal);

    //Vector from position to eye.  Since all geometry is assumed to be in eye space, the eye is always at the origin.
    vec3 positionToEyeDirection = normalize(-positionEyeSpace.xyz);

    //Calculate how bright various types of light are
	float diffuseBrightness = max(dot(incomingLightDirection,-surfaceNormal),0.0);
	float specBrightness = max(dot(outgoingLightDirection, positionToEyeDirection),0.0);
    specBrightness = pow(specBrightness,shininess);

    //Color of fragment is the combination of all colors
	gl_FragColor = ambientLightMaterialColor + diffuseBrightness * diffuseLightMaterialColor + specBrightness * specLightMaterialColor;
}

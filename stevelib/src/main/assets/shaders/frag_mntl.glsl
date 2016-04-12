precision mediump float;



//Position of light
uniform vec3 lightPositionEyeSpace;

//Specular color of material - Assume specular always reflects all light
uniform vec4 specMaterialColor;

//Light color and intensity
uniform vec4 diffuseLightColor;

//Ambient light color and intensity
uniform vec4 ambientLightColor;

// How shiny the material is.  This determines the exponent used in rendering.
uniform float shininess;

//Texture color of object
uniform sampler2D diffuseMaterialTexture;

//From vertex shader
varying vec4 positionEyeSpace;
varying vec3 normalEyeSpace;
varying vec2 texCoord                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        ;


void main()
{
    //Base color of material
    vec4 baseMaterialColor = texture2D(diffuseMaterialTexture,texCoord);

    //Normalize the surface's normal
    vec3 surfaceNormal = normalize(normalEyeSpace);

    //Calc ambient color
    vec4 ambientColor = baseMaterialColor * ambientLightColor;
    //Calc diffuse color
    vec4 diffuseColor = baseMaterialColor * diffuseLightColor;
    //Calc specular color
    vec4 specColor = specMaterialColor * diffuseLightColor;

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
	gl_FragColor = ambientColor + diffuseBrightness * diffuseColor + specBrightness * specColor;
}

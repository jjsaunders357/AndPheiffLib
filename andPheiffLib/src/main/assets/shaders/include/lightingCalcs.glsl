#include include/shadowCalcs.glsl

vec4 calcLightColor(vec3 lightToFragment,
                    vec3 fragmentToEye,
                    vec3 surfaceNormal,
                    vec4 diffuseLightMaterialColor,
                    vec4 specLightMaterialColor,
                    float shininess)
{
    //Normalize the surface's normal
    vec3 normalUnit = normalize(surfaceNormal);

    //Incoming light vector to current position
    vec3 lightToFragmentUnit = normalize(lightToFragment);

    //Vector from position to eye.  Since all geometry is assumed to be in eye space, the eye is always at the origin.
    vec3 fragmentToEyeUnit = normalize(fragmentToEye);

    //Reflected light vector from current position
    vec3 reflectDirection = reflect(lightToFragmentUnit, normalUnit);

    //Calculate how bright various types of light are
    float diffuseBrightness = max(dot(lightToFragmentUnit, -normalUnit),0.0);
    float specBrightness = max(dot(reflectDirection, fragmentToEyeUnit),0.0);
    specBrightness = pow(specBrightness, shininess);

    return diffuseBrightness * diffuseLightMaterialColor + specBrightness * specLightMaterialColor;
}
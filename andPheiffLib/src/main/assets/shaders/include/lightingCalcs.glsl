
#if enableShadows
    const float shadowDepthBias = 0.1;
    /**
    Calculates the amount of light transmission taking into account shadows.
    1.0 - no shadow,
    0.0 - 100% shadow,
    @param fragPositionAbs position of fragment in absolute space
    @param lightPositionAbs position of light in absolute space
    */


    float calcCubeShadow(vec3 fragPositionAbs, vec4 lightPositionAbs, mediump samplerCubeShadow cubeDepthSampler, float shadowProjectionMaxDepth)
//    float calcCubeShadow(vec3 fragPositionAbs, vec4 lightPositionAbs, mediump samplerCubeShadow cubeDepthSampler, float depthZConst, float depthZFactor)
    {
//        float trash = texture(cubeDepthSampler, vec3(1.0,0.0,0.0)).r + shadowProjectionMaxDepth + fragPositionAbs.x + lightPositionAbs.x;
//        trash = trash * 0.00001;
//        vec3 lightToFragment = fragPositionAbs - lightPositionAbs.xyz;
//        return texture(cubeDepthSampler,lightToFragment).r+trash;
//
//
        vec3 lightToFragment = fragPositionAbs - lightPositionAbs.xyz;
//
////        float rawDepth = -max(max(abs(lightToFragment.x),abs(lightToFragment.y)),abs(lightToFragment.z))-0.2;
////        float depth = (rawDepth * depthZConst + depthZFactor) / -rawDepth;
////        depth = 0.5 * depth + 0.5;
//
        float depth = max(max(abs(lightToFragment.x),abs(lightToFragment.y)),abs(lightToFragment.z));
        depth = depth / shadowProjectionMaxDepth - shadowDepthBias;
//
//
//        //Sample the depth texture.  Does lookup based on lightToPositionAbs, but then compares result to depth, returning 0 or 1 (or possibly in between if multiple samples are taken).
        return texture(cubeDepthSampler, vec4(lightToFragment,depth));
    }
#endif

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


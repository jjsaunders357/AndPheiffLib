#include include/consts.glsl
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

void applyShadow(inout vec4 color, vec3 fragPositionAbs, vec4 lightPositionAbs, mediump samplerCubeShadow cubeDepthSampler, float shadowProjectionMaxDepth)
{
    color.rgb = color.rgb * calcCubeShadow(fragPositionAbs,lightPositionAbs,cubeDepthSampler,shadowProjectionMaxDepth);
}
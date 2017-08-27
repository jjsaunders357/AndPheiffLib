#include include/consts.glsl

/**
Converts a positive depth value into its corresponding, non-linear, depth buffer, value.
@param depth a positive (linear) depth value
@param depthZConst a precalculated value extracted from the projection matrix used for depth extraction
@param depthZFactor a precalculated value extracted from the projection matrix used for depth extraction
*/
float depthToBuffer(float depth, float depthZConst, float depthZFactor)
{
    return depthZFactor / (depth - shadowDepthBias) + depthZConst;
}
/**
Converts a difference vector, between the "center" of a cube map and a point into a linear depth value
"observed by" the cube map.
@param diff vector from cube map center to a point
*/
float differenceToDepth(vec3 diff)
{
    vec3 absDiff = abs(diff);
    return max(max(absDiff.x,absDiff.y),absDiff.z);
}

/**
Calculates the amount of light transmission taking into account shadows.
1.0 - no shadow,
0.0 - 100% shadow,
@param fragPositionAbs position of fragment in absolute space
@param lightPositionAbs position of light in absolute space
@param cubeDepthSampler the depth sampler to use
@param depthZConst a precalculated value extracted from the projection matrix used for depth extraction
@param depthZFactor a precalculated value extracted from the projection matrix used for depth extraction
*/
float calcCubeShadow(vec3 fragPositionAbs, vec4 lightPositionAbs, mediump samplerCubeShadow cubeDepthSampler, float depthZConst, float depthZFactor)
{
    vec3 lightToFragment = fragPositionAbs - lightPositionAbs.xyz;
    float depth = differenceToDepth(lightToFragment);
    float bufferDepth = depthToBuffer(depth, depthZConst, depthZFactor);

    //Sample the depth texture.  Does lookup based on lightToPositionAbs, but then compares result to depth, returning 0 or 1 (or possibly in between if multiple samples are taken).
    return texture(cubeDepthSampler, vec4(lightToFragment,bufferDepth));
}

/**
Given a color, this modifies the non-alpha components to account for being shadowed.
*/
void applyShadow(inout vec4 color, vec3 fragPositionAbs, vec4 lightPositionAbs, mediump samplerCubeShadow cubeDepthSampler, float depthZConst, float depthZFactor)
{
    color.rgb = color.rgb * calcCubeShadow(fragPositionAbs,lightPositionAbs,cubeDepthSampler,depthZConst,depthZFactor);
}
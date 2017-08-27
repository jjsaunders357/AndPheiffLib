#include include/lightingInputs.glsl

//Position of lights in absolute space
uniform vec4 lightPositionAbs[numLights];

//Position of point being rendered in absolute space
in vec3 fragPositionAbs;

//Maximum depth projected into texture
uniform float shadowProjectionMaxDepth;

//uniform float depthZConst;
//uniform float depthZFactor;

//Shadow cube samplers
uniform bool castsCubeShadow[numLights];

uniform mediump samplerCubeShadow cubeDepthSampler0;
uniform mediump samplerCubeShadow cubeDepthSampler1;
uniform mediump samplerCubeShadow cubeDepthSampler2;
uniform mediump samplerCubeShadow cubeDepthSampler3;
#include include/lightingInputs.glsl

//Position of lights in absolute space
uniform vec4 lightPositionAbs[numLights];

//Position of point being rendered in absolute space
in vec3 fragPositionAbs;

//Precalculated values, extracted from the projection matrix, used for depth extraction
uniform float depthZConst;
uniform float depthZFactor;

//On/off for casting omni-directional shadows
uniform bool castsCubeShadow[numLights];

//Shadow cube samplers
uniform mediump samplerCubeShadow cubeDepthSampler0;
uniform mediump samplerCubeShadow cubeDepthSampler1;
uniform mediump samplerCubeShadow cubeDepthSampler2;
uniform mediump samplerCubeShadow cubeDepthSampler3;
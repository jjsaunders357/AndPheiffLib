#version 300
#include test_include2.glsl
#include test_include3.glsl
const float otherFloatConst = 8.0;

float computeSomething(float param)
{
    return param * 7.0 * magicConst + func2(2.0,3.0);
}
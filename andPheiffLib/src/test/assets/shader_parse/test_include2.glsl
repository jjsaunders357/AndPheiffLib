#version 300
#include test_include3.glsl
const float magicConst = 17.2;

float func2(float param1,float param2)
{
    return param1 * param2 + func3(param1,param2);
}

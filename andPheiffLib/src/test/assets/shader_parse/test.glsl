#version 300
#type VERTEX
#include test_include5.glsl
#include test_include1.glsl
#include test_include3.glsl
#include test_include4.glsl

const int intConst = 5;
const float floatConst = 6.0;
const vec2 vecConst = vec2(6.0,7.0);

void main()
{
    //Comment
    int x = 5 + 7;/*Junk
    float y = 6.0 + 7.0;*/
    float y = 6.0 + 7.0;
    float z = 8.0 + 9.0 * magicConst * computeSomething(3.0);
    float w = func5(y,z) + func4(y,z);
}

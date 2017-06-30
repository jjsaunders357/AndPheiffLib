#version 300 es
precision mediump float;

//Maximum distance the light shines.  This is used to pack the distance as a depth into the buffer
uniform float maxLightDistanceSquared;

//The position of the fragment in light space.  Since light is at origin, this can be used to compute distance easily.
in vec3 positionInLightSpace;

void main()
{
    //float trash = pow((positionInLightSpace.x+maxLightDistanceSquared)/10000.0,500.0);
    gl_FragDepth = dot(positionInLightSpace,positionInLightSpace)/maxLightDistanceSquared;
}

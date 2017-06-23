precision mediump float;
//Transforms vertices within screen space
uniform mat4 modelMatrix;

//Transforms normals within screen space
uniform mat3 normalMatrix;

//Position of the eye
uniform vec4 eyePosition;

//Near visible plane, relative to eye.  In other words, minimum distance from eye, in z direction, towards screen, which is visible.
uniform float zNear;

//Far visible plane
uniform float zFar;

// x / y ratio
uniform float aspectRatio;

//Position of the vertex in screen space.  0,0,0 represents the center of the surface of the screen.  -z goes into the screen, +z projects out from the screen.
//A length of 1 corresponds to 1/2 the width of the screen.
attribute vec4 vertexPosition4;
attribute vec3 vertexNormal;
varying vec4 position;
varying vec3 normal;
void main()
{
	normal = normalize(normalMatrix * vertexNormal);
	position = modelMatrix * vertexPosition4;
    position.y *= aspectRatio;
	float w = eyePosition.z - position.z;
	float x = position.x * w - position.z * (eyePosition.x - position.x);
	float y = position.y * w - position.z * (eyePosition.y - position.y);

// z mapped to range [0,1]
    float z = (w-zNear)/(zFar-zNear);

// z mapped to range [-1,1]
    z = 2.0 * z - 1.0;

// z mapped to range [-w,w] (pre-homogeneous divide).
    z *= w;
	gl_Position = vec4(x,y,z,w);
}
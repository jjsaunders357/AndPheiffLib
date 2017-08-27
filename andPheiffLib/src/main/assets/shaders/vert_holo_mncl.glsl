#type VERTEX
#version 300 es
precision highp float;


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
in vec4 vertexPosition4;
in vec3 vertexNormal;
out vec4 fragPositionAbs;
out vec3 normalAbs;
//TODO 1.5 = 3/2: Convert into normal shader with appropriate perspective
void main()
{
	normalAbs = normalize(normalMatrix * vertexNormal);
	fragPositionAbs = modelMatrix * vertexPosition4;
    fragPositionAbs.y *= aspectRatio;
	float w = eyePosition.z - fragPositionAbs.z;
	float x = fragPositionAbs.x * w - fragPositionAbs.z * (eyePosition.x - fragPositionAbs.x);
	float y = fragPositionAbs.y * w - fragPositionAbs.z * (eyePosition.y - fragPositionAbs.y);

// z mapped to range [0,1]
    float z = (w-zNear)/(zFar-zNear);

// z mapped to range [-1,1]
    z = 2.0 * z - 1.0;

// z mapped to range [-w,w] (pre-homogeneous divide).
    z *= w;

	gl_Position = vec4(x,y,z,w);
}
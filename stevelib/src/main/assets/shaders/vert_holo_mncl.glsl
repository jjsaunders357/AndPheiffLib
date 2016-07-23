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

//Position of the vertex in screen space.  0,0,0 represents the center of the surface of the screen.  -z goes into the screen, +z projects out from the screen.
attribute vec4 vertexPosition;
attribute vec3 vertexNormal;
varying vec4 position;
varying vec3 normal;
void main()
{
	normal = normalize(normalMatrix * vertexNormal);
	position = modelMatrix * vertexPosition;
    position.y *= 0.6;
	float wp = eyePosition.z - position.z;
	float xp = position.x * wp - position.z * (eyePosition.x - position.x);
	float yp = position.y * wp - position.z * (eyePosition.y - position.y);
	float zp = wp * (2.0*(wp - zNear) / (zFar - zNear)-1.0);
	gl_Position = vec4(xp,yp,zp,wp);
}
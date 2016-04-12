//Transforms vertices to eye space
uniform mat4 eyeTransformMatrix;
//Projects vertices in eye space
uniform mat4 eyeProjectionMatrix;
//Transforms normals to eye space
uniform mat3 eyeNormalMatrix;

attribute vec4 vertexPosition;
attribute vec3 vertexNormal;
varying vec4 positionEyeSpace;
varying vec3 normalEyeSpace;

void main()
{
	normalEyeSpace = normalize(eyeNormalMatrix * vertexNormal);
	positionEyeSpace = eyeTransformMatrix * vertexPosition;
	gl_Position = eyeProjectionMatrix * positionEyeSpace;
}
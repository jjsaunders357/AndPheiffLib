//Transforms vertices to eye space
uniform mat4 viewModelMatrix;
//Projects vertices in eye space
uniform mat4 projectionMatrix;
//Transforms normals to eye space
uniform mat3 normalMatrix;

attribute vec4 vertexPosition4;
attribute vec3 vertexNormal;
varying vec4 positionEyeSpace;
varying vec3 normalEyeSpace;

void main()
{
	normalEyeSpace = normalize(normalMatrix * vertexNormal);
	positionEyeSpace = viewModelMatrix * vertexPosition4;
	gl_Position = projectionMatrix * positionEyeSpace;
}
#version 300 es
//Transforms vertices to eye space
uniform mat4 viewModelMatrix;
//Projects vertices in eye space
uniform mat4 projectionMatrix;
//Transforms normals to eye space
uniform mat3 normalMatrix;

in vec4 vertexPosition4;
in vec3 vertexNormal;
out vec4 positionEyeSpace;
out vec3 normalEyeSpace;

void main()
{
	normalEyeSpace = normalize(normalMatrix * vertexNormal);
	positionEyeSpace = viewModelMatrix * vertexPosition4;
	gl_Position = projectionMatrix * positionEyeSpace;
}
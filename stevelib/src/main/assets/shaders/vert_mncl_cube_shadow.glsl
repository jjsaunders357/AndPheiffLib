#version 300 es
//Transforms vertices to model space
uniform mat4 modelMatrix;
//Transforms vertices to eye space
uniform mat4 viewModelMatrix;
//Projects vertices in eye space
uniform mat4 projectionMatrix;
//Transforms normals to eye space
uniform mat3 normalMatrix;

in vec4 vertexPosition4;
in vec3 vertexNormal;

//Absolute position
out vec3 absPosition;

//Position, in eye space
out vec4 positionEyeSpace;

//Normal, in eye space
out vec3 normalEyeSpace;

void main()
{
    absPosition=(modelMatrix*vertexPosition4).xyz;
	normalEyeSpace = normalize(normalMatrix * vertexNormal);
	positionEyeSpace = viewModelMatrix * vertexPosition4;
	gl_Position = projectionMatrix * positionEyeSpace;
}
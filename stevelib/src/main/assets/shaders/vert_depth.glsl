#version 300 es
//Projects and transforms vertices to eye space
uniform mat4 projectionViewModelMatrix;

in vec4 vertexPosition4;

void main()
{
	gl_Position = projectionViewModelMatrix * vertexPosition4;
}
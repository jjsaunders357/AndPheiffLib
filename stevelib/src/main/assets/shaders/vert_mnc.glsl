#version 300 es
//Transform and apply view to vertices
uniform mat4 transformViewMatrix;

in vec4 vertexPosition4;
in vec4 vertexNormal;
in vec4 vertexColor;

out vec4 outNormal;
out vec4 outColor;

void main()
{
	outColor = vertexColor;
	outNormal = vertexNormal;
	gl_Position = transformViewMatrix * vertexPosition4;
}
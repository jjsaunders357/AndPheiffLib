//Transform and apply view to vertices
uniform mat4 transformViewMatrix;

attribute vec4 vertexPosition;
attribute vec4 vertexNormal;
attribute vec4 vertexColor;

varying vec4 varyingNormal;
varying vec4 varyingColor;

void main()
{
	varyingColor = vertexColor;
	varyingNormal = vertexNormal;
	gl_Position = transformViewMatrix * vertexPosition;
}
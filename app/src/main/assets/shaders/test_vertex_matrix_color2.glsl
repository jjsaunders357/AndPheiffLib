//Transform and apply view to vertices
uniform mat4 transformViewMatrix;

attribute vec4 vertexPosition;
attribute vec4 vertexColor;
attribute vec4 vertexColor2;

varying vec4 varyingColor;

void main()
{
	varyingColor.r = vertexColor.r + vertexColor2.r;
	varyingColor.gba = vertexColor.gba;
	gl_Position = transformViewMatrix * vertexPosition;
}
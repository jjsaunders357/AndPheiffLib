//Transform and apply view to vertices
uniform mat4 transformViewMatrix;

attribute vec4 vertexPosition4;
attribute vec4 vertexColor;
attribute vec2 vertexTexCoord;
varying vec2 varyingTexCoord;
varying vec4 varyingColor;

void main()
{
	varyingColor.rgba = vertexColor.rgba;
	varyingTexCoord = vertexTexCoord;
	gl_Position = transformViewMatrix * vertexPosition4;
}
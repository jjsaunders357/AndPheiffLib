//Transform and apply view to vertices
uniform mat4 transformViewMatrix;
uniform vec3 lightPosition;
attribute vec4 vertexPosition;
attribute vec4 vertexNormal;
attribute vec4 vertexColor;

varying vec4 varyingNormal;
varying vec4 varyingColor;

void main()
{
	varyingColor = vertexColor;
	varyingNormal = vec4(normalize(vertexNormal.xyz),0);
	gl_Position = transformViewMatrix * vertexPosition;
}
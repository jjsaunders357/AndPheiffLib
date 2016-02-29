//Transform and apply view to vertices
uniform mat4 projectionMatrix;
uniform mat4 transformMatrix;
uniform vec4 lightPosition;
uniform vec4 eyePosition;
uniform vec4 ambientColorIntensity;
uniform vec4 lightColorIntensity;
uniform float shininess;
attribute vec4 vertexPosition;
attribute vec4 vertexNormal;
attribute vec4 vertexColor;
varying vec4 varyingPosition;
varying vec4 varyingNormal;
varying vec4 varyingColor;

void main()
{
	varyingColor = vertexColor;
	varyingNormal = normalize(vertexNormal);
	varyingPosition = transformMatrix * vertexPosition;
	gl_Position = projectionMatrix * varyingPosition;
}
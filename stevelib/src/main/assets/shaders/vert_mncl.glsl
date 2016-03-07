//Projects transformed vertices
uniform mat4 projectionMatrix;
//Transforms vertices
uniform mat4 transformMatrix;
//Transforms normals
uniform mat3 normalMatrix;

uniform vec4 lightPosition;
uniform vec4 ambientColorIntensity;
uniform vec4 lightColorIntensity;
uniform float shininess;
attribute vec4 vertexPosition;
attribute vec3 vertexNormal;
attribute vec4 vertexColor;
varying vec4 varyingPosition;
varying vec3 varyingNormal;
varying vec4 varyingColor;

void main()
{
	varyingColor = vertexColor;
	varyingNormal = normalize(normalMatrix * vertexNormal);
	varyingPosition = transformMatrix * vertexPosition;
	gl_Position = projectionMatrix * varyingPosition;
}
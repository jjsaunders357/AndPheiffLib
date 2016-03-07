//Projects transformed vertices
uniform mat4 projectionMatrix;
//Transforms vertices
uniform mat4 transformMatrix;
//Transforms normals
uniform mat4 normalMatrix;

//TODO: Make normals, light positions, etc 3 vectors instead of 4 vectors
uniform vec4 lightPosition;
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
	varyingNormal = normalize(normalMatrix * vertexNormal);
	varyingPosition = transformMatrix * vertexPosition;
	gl_Position = projectionMatrix * varyingPosition;
}
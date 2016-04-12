//Projects transformed vertices
uniform mat4 projectionMatrix;
//Transforms vertices
uniform mat4 transformMatrix;
//Transforms normals
uniform mat3 normalMatrix;

uniform vec4 lightPosition;
uniform vec4 ambientLightColorIntensity;
uniform vec4 lightColorIntensity;
uniform float shininess;
attribute vec4 vertexPosition;
attribute vec3 vertexNormal;
attribute vec2 vertexTexCoord;
varying vec4 varyingPosition;
varying vec3 varyingNormal;
varying vec2 varyingTexCoord                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        ;

void main()
{
	varyingTexCoord = vertexTexCoord;
	varyingNormal = normalize(normalMatrix * vertexNormal);
	varyingPosition = transformMatrix * vertexPosition;
	gl_Position = projectionMatrix * varyingPosition;
}
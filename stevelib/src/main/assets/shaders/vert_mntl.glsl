//Transforms vertices to eye space
uniform mat4 viewModelMatrix;
//Projects vertices in eye space
uniform mat4 projectionMatrix;
//Transforms normals to eye space
uniform mat3 normalMatrix;

attribute vec4 vertexPosition;
attribute vec3 vertexNormal;
attribute vec2 vertexTexCoord;
varying vec4 positionEyeSpace;
varying vec3 normalEyeSpace;
varying vec2 texCoord;

void main()
{
	texCoord = vertexTexCoord;
	//TODO: Decide on normalization policy
	normalEyeSpace = normalize(normalMatrix * vertexNormal);
	positionEyeSpace = viewModelMatrix * vertexPosition;
	gl_Position = projectionMatrix * positionEyeSpace;
}

#type VERTEX
#version 300 es
precision highp float;

uniform mat4 projectionMatrix;

//Transforms vertices to eye space
uniform mat4 viewModelMatrix;

//Transforms normals to eye space
uniform mat3 normalMatrix;

in vec4 vertexPosition4;
in vec3 vertexNormal;

//Position, in eye space
out vec4 positionEyeSpace;

//Normal, in eye space
out vec3 normalEyeSpace;

#if texturedMaterial
    in vec2 vertexTexCoord;
    out vec2 texCoord;
#endif

//Transforms vertices to model space
uniform mat4 modelMatrix;

//Absolute position
out vec3 fragPositionAbs;

void main()
{
    #if texturedMaterial
	    texCoord = vertexTexCoord;
    #endif

    fragPositionAbs = (modelMatrix * vertexPosition4).xyz;

	//TODO 1.5 = 3/2: Decide on normalization policy
	normalEyeSpace = normalize(normalMatrix * vertexNormal);
	positionEyeSpace = viewModelMatrix * vertexPosition4;
    gl_Position = projectionMatrix * positionEyeSpace;
}
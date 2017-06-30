#version 300 es

//Transforms vertices to light space and performs projection
uniform mat4 projectionViewModelMatrix;

//Transforms vertices to light space
uniform mat4 viewModelMatrix;

//Vertex position
in vec4 vertexPosition4;

//Position of vertex (in light space) sent to frag shader for distance calculation at each point
out vec3 positionInLightSpace;

void main()
{
    positionInLightSpace = (viewModelMatrix * vertexPosition4).xyz;
	vec4 test = projectionViewModelMatrix * vertexPosition4;
	//TODO: If depth being output is less than depth written in fragment shader, then fragment shader is blocked!!
    test.z=1.0;
	gl_Position = test;
}
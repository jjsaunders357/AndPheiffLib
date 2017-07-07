#version 300 es
precision highp float;

//Linear depth projection inputs
uniform float projectionScaleX;
uniform float projectionScaleY;
uniform float projectionMaxDepth;

//Projects and transforms vertices to eye space
//uniform mat4 projectionViewModelMatrix;

uniform mat4 viewModelMatrix;

in vec4 vertexPosition4;

void main()
{
	vec4 positionEyeSpace = viewModelMatrix * vertexPosition4;

	gl_Position = vec4(
	    positionEyeSpace.x * projectionScaleX,
	    positionEyeSpace.y * projectionScaleY,
	    //Z - After division by w, z will LINEARLY map from [0,projectionMaxDepth] --> [-1,1]
	    positionEyeSpace.z * (positionEyeSpace.z + projectionMaxDepth * 0.5) / (projectionMaxDepth * 0.5),
    	-positionEyeSpace.z
    );
}
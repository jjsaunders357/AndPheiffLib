#type VERTEX
#version 300 es
precision highp float;

//Transforms vertices to eye space
uniform mat4 viewModelMatrix;

//Transforms normals to eye space
uniform mat3 normalMatrix;

//Linear depth projection inputs
uniform float projectionScaleX;
uniform float projectionScaleY;
uniform float projectionMaxDepth;

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

#if enableShadows
    //Transforms vertices to model space
    uniform mat4 modelMatrix;

    //Absolute position
    out vec3 fragPositionAbs;
#endif

void main()
{
    #if texturedMaterial
	    texCoord = vertexTexCoord;
    #endif
    #if enableShadows
        fragPositionAbs = (modelMatrix * vertexPosition4).xyz;
    #endif

	//TODO 1.5 = 3/2: Decide on normalization policy
	normalEyeSpace = normalize(normalMatrix * vertexNormal);
	positionEyeSpace = viewModelMatrix * vertexPosition4;

	gl_Position = vec4(
	    positionEyeSpace.x * projectionScaleX,
	    positionEyeSpace.y * projectionScaleY,
	    //Z - After division by w, z will LINEARLY map from [0,projectionMaxDepth] --> [-1,1]
	    positionEyeSpace.z * (positionEyeSpace.z + projectionMaxDepth * 0.5) / (projectionMaxDepth * 0.5),
    	-positionEyeSpace.z
    );
}
#version 300 es
precision mediump float;
in vec4 inColor;
in vec4 inNormal;

layout(location = 0) out vec4 fragColor;

void main()
{
    //Using normal or else it disappears from attribute list (multipy by 0.0 DOES NOT WORK).
	fragColor = inColor+inNormal*0.0001;
}

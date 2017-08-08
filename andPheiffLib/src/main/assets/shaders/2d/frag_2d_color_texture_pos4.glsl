#type FRAGMENT
#version 300 es
precision mediump float;
in vec4 color;
in vec2 texCoord;
uniform sampler2D materialColorSampler;

layout(location = 0) out vec4 fragColor;
void main()
{
	fragColor = color * texture(materialColorSampler,texCoord);
}

precision mediump float;
varying vec4 varyingColor;
varying vec2 varyingTexCoord;
uniform sampler2D texture;
void main()
{
	gl_FragColor = varyingColor + texture2D(texture,varyingTexCoord);
}

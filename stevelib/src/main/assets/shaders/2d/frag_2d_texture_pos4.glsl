precision mediump float;
varying vec2 varyingTexCoord;
uniform sampler2D materialColorSampler;
void main()
{
	gl_FragColor = texture2D(materialColorSampler,varyingTexCoord);
}

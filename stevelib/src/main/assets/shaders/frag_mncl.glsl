precision mediump float;
uniform vec3 lightPosition;
varying vec4 varyingColor;
varying vec4 varyingNormal;

void main()
{
	vec3 lightDirection = normalize(lightPosition);
	float brightness = max(dot(lightDirection,varyingNormal.xyz),0.0);
	brightness=pow(brightness,1.0);
	gl_FragColor = varyingColor*vec4(1.0,1.0,1.0,1.0)*brightness;
}

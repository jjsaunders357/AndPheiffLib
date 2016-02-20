precision mediump float;
varying vec4 varyingColor;
varying vec4 varyingNormal;

void main()
{
    //Using normal or else it disappears from attribute list (multipy by 0.0 DOES NOT WORK).
	gl_FragColor = varyingColor+varyingNormal*0.0001;
}

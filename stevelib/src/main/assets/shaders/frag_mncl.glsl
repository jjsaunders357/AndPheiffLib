precision mediump float;
uniform vec4 lightPosition;
uniform vec4 eyePosition;
uniform vec4 ambientColorIntensity;
uniform vec4 lightColorIntensity;
uniform float shininess;
varying vec4 varyingPosition;
varying vec4 varyingColor;
varying vec4 varyingNormal;
void main()
{
    vec4 diffuseMaterialColor = varyingColor;
    vec4 specMaterialColor = vec4(1.0,1.0,1.0,1.0);

    vec4 incomingLightDirection = normalize(varyingPosition-lightPosition);
//    vec4 outgoingLightDirection = reflect(incomingLightDirection,varyingNormal);
    vec4 outgoingLightDirection = incomingLightDirection - (2.0 * dot(incomingLightDirection,varyingNormal) * varyingNormal);
    vec4 positionToEyeDirection = normalize(eyePosition - varyingPosition);

    //Calc ambient
    vec4 ambientColor = diffuseMaterialColor * ambientColorIntensity;

    //Calc diffuse
    vec4 diffuseColor = diffuseMaterialColor * lightColorIntensity;
	float diffuseBrightness = max(dot(incomingLightDirection,-varyingNormal),0.0);

    //Calc specular
    vec4 specColor = specMaterialColor * lightColorIntensity;
	float specBrightness = max(dot(outgoingLightDirection, positionToEyeDirection),0.0);
    specBrightness = pow(specBrightness,shininess);
	gl_FragColor = specBrightness * specColor + diffuseBrightness * diffuseColor + ambientColor;
}

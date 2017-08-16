#type FRAGMENT
#version 300 es
#const colorVertex2D true
#const textured2D true
precision mediump float;

#if colorVertex2D
    in vec4 color;
#endif
#if textured2D
    in vec2 texCoord;
    uniform sampler2D imageTexture;
#endif

layout(location = 0) out vec4 fragColor;
void main()
{
    //Both color and texture should not be false!
    #if textured2D
        #if colorVertex2D
       	    fragColor = color * texture(imageTexture,texCoord);
        #else
       	    fragColor = texture(imageTexture,texCoord);
        #endif
    #else
       	fragColor = color;
    #endif
}

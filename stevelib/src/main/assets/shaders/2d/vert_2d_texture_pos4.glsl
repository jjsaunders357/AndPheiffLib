/*
2 dimensional vertex shader.
All x values are interpreteted following the convention that -1 is the left of the screen and +1 is the right of the screen.
Depending on aspect ratio, incoming y-values may have magnitude <> 1 at the edge of the screen.
z values are used for deciding what appears in front of what.
*/

//Transforms vertices according position/orientation then move them into eye space and finally
//to project them in 2D, which is a simple scale along the y-axis as described above.
uniform mat4 projectionViewModelMatrix;

attribute vec4 vertexPosition4;
attribute vec2 vertexTexCoord;
varying vec2 varyingTexCoord;

void main()
{
	varyingTexCoord = vertexTexCoord;
	gl_Position = projectionViewModelMatrix * vertexPosition4;
}
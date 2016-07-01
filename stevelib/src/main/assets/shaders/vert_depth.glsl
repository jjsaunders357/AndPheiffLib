//Projects and transforms vertices to eye space
uniform mat4 projectionViewModelMatrix;

attribute vec4 vertexPosition;

void main()
{
	gl_Position = projectionViewModelMatrix * vertexPosition;
}
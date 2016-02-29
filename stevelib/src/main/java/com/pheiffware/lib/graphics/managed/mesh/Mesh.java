package com.pheiffware.lib.graphics.managed.mesh;

import com.pheiffware.lib.graphics.GColor;

import java.util.Map;

/**
 * Created by Steve on 2/14/2016.
 */
public class Mesh
{
    //The number of unique vertices.  Each array in vertex data is this length
    public final int numUniqueVertices;
    //Data for each unique vertex.  A map from names like POSITION, NORMAL, TEXCOORD, etc to actual arrays holding vertex data.  The same vertex may be referenced multiple times in the vertexIndices array.
    public final Map<String, float[]> uniqueVertexData;
    //Indices to the data itself.  These are grouped together to form primitives (typically triangles)
    public final short[] vertexIndices;

    public Mesh(int numUniqueVertices, Map<String, float[]> uniqueVertexData, short[] vertexIndices)
    {
        this.numUniqueVertices = numUniqueVertices;
        this.uniqueVertexData = uniqueVertexData;
        this.vertexIndices = vertexIndices;
    }

    /**
     * Generates data for shaders requiring per vertex color data.
     *
     * @param gColor Color to use
     * @return Array for putting into a openGL buffer
     */
    public float[] generateSingleColorData(GColor gColor)
    {
        float[] colors = new float[numUniqueVertices * 4];
        int index = 0;
        for (int i = 0; i < getNumUniqueVertices(); i++)
        {
            colors[index++] = gColor.getRed();
            colors[index++] = gColor.getGreen();
            colors[index++] = gColor.getBlue();
            colors[index++] = gColor.getAlpha();
        }
        return colors;
    }

    public int getNumVertexIndices()
    {
        return vertexIndices.length;
    }

    public int getNumUniqueVertices()
    {
        return numUniqueVertices;
    }

}

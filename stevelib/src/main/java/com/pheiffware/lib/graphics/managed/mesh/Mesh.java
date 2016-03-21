package com.pheiffware.lib.graphics.managed.mesh;

import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.graphics.GColor;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steve on 2/14/2016.
 */
public class Mesh
{
    //The number of unique vertices.  Each array in vertex data is this length
    public final int numUniqueVertices;
    //Data for each unique vertex.  A map from names like POSITION, NORMAL, TEXCOORD, etc to actual arrays holding vertex data.  The same vertex may be referenced multiple times in the vertexIndices array.
    //POSITION - Stored as 4 element homogeneous coords
    //NORMAL - Stored as 3 element vectors
    //TEXCOORD - Stored as 2 element coords
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

    public Mesh newTransformedMesh(Matrix4 transformMatrix)
    {
        float[] positionData = getPositionData();
        float[] normalData = getNormalData();
        Matrix3 normalTransform = transformMatrix.newNormalTransformMatrix3();
        float[] transformedPositionData = transformMatrix.newTransformedVectors(positionData);
        float[] transformedNormalData = normalTransform.newTransformedVectors(normalData);

        HashMap<String, float[]> transformedVertexData = new HashMap<>(uniqueVertexData);
        transformedVertexData.put(Collada.COLLADA_VERTEX_POSITION, transformedPositionData);
        transformedVertexData.put(Collada.COLLADA_VERTEX_NORMAL, transformedNormalData);
        return new Mesh(numUniqueVertices, transformedVertexData, vertexIndices);
    }

    public float[] getPositionData()
    {
        return uniqueVertexData.get(Collada.COLLADA_VERTEX_POSITION);
    }

    public float[] getNormalData()
    {
        //TODO: Normal should figure out size of normal coordinates (always translate to size 3)
        return uniqueVertexData.get(Collada.COLLADA_VERTEX_NORMAL);
    }

    public float[] getTexCoordData()
    {
        return uniqueVertexData.get(Collada.COLLADA_VERTEX_TEXCOORD);
    }
}

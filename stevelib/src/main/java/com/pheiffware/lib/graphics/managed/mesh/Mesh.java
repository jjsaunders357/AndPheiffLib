package com.pheiffware.lib.graphics.managed.mesh;

import com.pheiffware.lib.geometry.collada.Collada;
import com.pheiffware.lib.graphics.Color4F;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.utils.MapCounter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Steve on 2/14/2016.
 */
public class Mesh
{
    //The number of unique vertices.  Each array in vertex data has this number of items.  Each item may vary in length.
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
     * Create a new mesh out of a collection of similar meshes.  All meshes must share the same vertex attributes.
     *
     * @param meshes a collection of meshes
     */
    public Mesh(Collection<Mesh> meshes)
    {
        //Count index and vertex data list sizes
        int indices = 0;
        int uniqueVerticesCounter = 0;
        MapCounter<String> vertexDataSizes = new MapCounter<>();

        for (Mesh mesh : meshes)
        {
            indices += mesh.getNumIndices();
            uniqueVerticesCounter += mesh.getNumVertices();
            for (String vertexDataKey : mesh.uniqueVertexData.keySet())
            {
                int vertexDataLength = mesh.uniqueVertexData.get(vertexDataKey).length;
                vertexDataSizes.addCount(vertexDataKey, vertexDataLength);
            }
        }
        numUniqueVertices = uniqueVerticesCounter;

        //Create new arrays big enough to hold combined data
        vertexIndices = new short[indices];
        uniqueVertexData = new HashMap<>();
        for (Map.Entry<String, Integer> entry : vertexDataSizes.entrySet())
        {
            uniqueVertexData.put(entry.getKey(), new float[entry.getValue()]);
        }

        //Copy data from each individual mesh to this mesh
        indices = 0;
        MapCounter<String> vertexDataOffsets = new MapCounter<>();
        for (Mesh mesh : meshes)
        {
            short[] srcIndexData = mesh.vertexIndices;
            System.arraycopy(
                    srcIndexData, 0,
                    vertexIndices, indices, srcIndexData.length);
            indices += srcIndexData.length;
            for (String vertexDataKey : mesh.uniqueVertexData.keySet())
            {
                int offset = vertexDataOffsets.getCount(vertexDataKey);
                float[] srcVertexData = mesh.uniqueVertexData.get(vertexDataKey);
                System.arraycopy(
                        srcVertexData, 0,
                        uniqueVertexData.get(vertexDataKey), offset,
                        srcVertexData.length);
                vertexDataOffsets.addCount(vertexDataKey, srcVertexData.length);
            }
        }
    }

    /**
     * Generates data for shaders requiring per vertex color data.
     *
     * @param color4F Color to use
     * @return Array for putting into a openGL buffer
     */
    public float[] generateSingleColorData(Color4F color4F)
    {
        float[] colors = new float[numUniqueVertices * 4];
        int index = 0;
        for (int i = 0; i < getNumVertices(); i++)
        {
            colors[index++] = color4F.getRed();
            colors[index++] = color4F.getGreen();
            colors[index++] = color4F.getBlue();
            colors[index++] = color4F.getAlpha();
        }
        return colors;
    }

    public int getNumIndices()
    {
        return vertexIndices.length;
    }

    public int getNumVertices()
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
        return uniqueVertexData.get(Collada.COLLADA_VERTEX_NORMAL);
    }

    public float[] getTexCoordData()
    {
        return uniqueVertexData.get(Collada.COLLADA_VERTEX_TEXCOORD);
    }
}

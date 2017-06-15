package com.pheiffware.lib.graphics;

import com.pheiffware.lib.graphics.managed.program.VertexAttribute;
import com.pheiffware.lib.utils.dataContainers.MapCounterInt;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * A list of vertices along with list of indices describing their arrangement into triangles.  Each vertex attribute is stored in its own array. Created by Steve on 2/14/2016.
 */
public class Mesh
{
    //The number of vertices.  Each array in vertex data has this number of items.  Each item may vary in length.
    private final int numVertices;

    //Data for each vertex mapped from standard attributes to arrays of floats with the data.
    private final EnumMap<VertexAttribute, float[]> vertexAttributeData;

    //Indices to the data itself.  These are grouped together to form primitives (typically triangles)
    private final short[] vertexIndices;

    public Mesh(int numVertices, EnumMap<VertexAttribute, float[]> vertexAttributeData, short[] vertexIndices)
    {
        this.numVertices = numVertices;
        this.vertexAttributeData = vertexAttributeData;
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
        MapCounterInt<VertexAttribute> vertexDataSizes = new MapCounterInt<>();

        for (Mesh mesh : meshes)
        {
            indices += mesh.getNumIndices();
            uniqueVerticesCounter += mesh.getNumVertices();
            for (VertexAttribute vertexAttribute : mesh.vertexAttributeData.keySet())
            {
                int vertexDataLength = mesh.vertexAttributeData.get(vertexAttribute).length;
                vertexDataSizes.addCount(vertexAttribute, vertexDataLength);
            }
        }
        numVertices = uniqueVerticesCounter;

        //Create new arrays big enough to hold combined data
        vertexIndices = new short[indices];
        vertexAttributeData = new EnumMap<>(VertexAttribute.class);
        for (Map.Entry<VertexAttribute, Integer> entry : vertexDataSizes.entrySet())
        {
            vertexAttributeData.put(entry.getKey(), new float[entry.getValue()]);
        }

        //Copy data from each individual mesh to this mesh.  This requires offsetting references to vertices in the index buffer appropriately.
        indices = 0;
        uniqueVerticesCounter = 0;
        MapCounterInt<VertexAttribute> vertexDataOffsets = new MapCounterInt<>();
        for (Mesh mesh : meshes)
        {
            copyIndexArray(mesh.vertexIndices, vertexIndices, indices, uniqueVerticesCounter);
            indices += mesh.vertexIndices.length;
            for (VertexAttribute vertexAttribute : mesh.vertexAttributeData.keySet())
            {
                int offset = vertexDataOffsets.get(vertexAttribute);
                float[] srcVertexData = mesh.vertexAttributeData.get(vertexAttribute);
                System.arraycopy(
                        srcVertexData, 0,
                        vertexAttributeData.get(vertexAttribute), offset,
                        srcVertexData.length);
                vertexDataOffsets.addCount(vertexAttribute, srcVertexData.length);
            }
            uniqueVerticesCounter += mesh.getNumVertices();
        }
    }

    /**
     * Copies index data from the source mesh to the combined mesh.  Copied indices are adjusted by an offset.
     *
     * @param srcMeshIndexData  source index data
     * @param combinedIndexData destination index data
     * @param destinationOffset location in destination to copy at
     * @param indexOffset       how much to offset each copied index
     */
    private void copyIndexArray(short[] srcMeshIndexData, short[] combinedIndexData, int destinationOffset, int indexOffset)
    {
        for (int i = 0; i < srcMeshIndexData.length; i++)
        {
            combinedIndexData[i + destinationOffset] = (short) (srcMeshIndexData[i] + indexOffset);
        }
    }

    /**
     * Creates a new mesh by applying the given transform to this mesh's positions/normals.  Original mesh is not affected.
     *
     * @param transformMatrix
     * @return
     */
    public Mesh newTransformedMesh(Matrix4 transformMatrix)
    {
        float[] positionData = getPositionData();
        float[] normalData = getNormalData();
        Matrix3 normalTransform = transformMatrix.newNormalTransformMatrix3();
        if (positionData != null)
        {
            positionData = transformMatrix.newTransformedVectors(positionData);
        }
        if (normalData != null)
        {
            normalData = normalTransform.newTransformedVectors(normalData);
        }
        EnumMap<VertexAttribute, float[]> transformedVertexData = new EnumMap<>(vertexAttributeData);
        transformedVertexData.put(VertexAttribute.POSITION, positionData);
        transformedVertexData.put(VertexAttribute.NORMAL, normalData);
        return new Mesh(numVertices, transformedVertexData, vertexIndices);
    }

    /**
     * Generates data for shaders requiring per vertex color data.
     *
     * @param color4F Color to use
     * @return Array for putting into a openGL buffer
     */
    public float[] generateSingleColorData(Color4F color4F)
    {
        float[] colors = new float[numVertices * 4];
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

    public final int getNumIndices()
    {
        return vertexIndices.length;
    }

    public final int getNumVertices()
    {
        return numVertices;
    }

    /**
     * Get a set of the attributes contained in this mesh.
     *
     * @return
     */
    public final EnumSet<VertexAttribute> getAttributes()
    {
        return EnumSet.copyOf(vertexAttributeData.keySet());
    }

    public final float[] getAttributeData(VertexAttribute vertexAttribute)
    {
        return vertexAttributeData.get(vertexAttribute);
    }

    public boolean hasAttributeData(VertexAttribute vertexAttribute)
    {
        return vertexAttributeData.containsKey(vertexAttribute);
    }

    public final float[] getPositionData()
    {
        return vertexAttributeData.get(VertexAttribute.POSITION);
    }

    public final float[] getNormalData()
    {
        return vertexAttributeData.get(VertexAttribute.NORMAL);
    }

    public final float[] getTexCoordData()
    {
        return vertexAttributeData.get(VertexAttribute.TEXCOORD);
    }

    public final short[] getVertexIndices()
    {
        return vertexIndices;
    }

}

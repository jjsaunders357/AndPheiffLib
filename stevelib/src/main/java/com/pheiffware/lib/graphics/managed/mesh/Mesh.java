package com.pheiffware.lib.graphics.managed.mesh;

import com.pheiffware.lib.graphics.Color4F;
import com.pheiffware.lib.graphics.Matrix3;
import com.pheiffware.lib.graphics.Matrix4;
import com.pheiffware.lib.graphics.managed.program.Attribute;
import com.pheiffware.lib.utils.MapCounter;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * A list of vertices along with list of indices describing their arrangement into triangles.  Each vertex attribute is stored in its own array. Created by Steve on 2/14/2016.
 */
public class Mesh
{
    //The number of vertices.  Each array in vertex data has this number of items.  Each item may vary in length.
    private final int numVertices;

    //Data for each vertex mapped from standard attributes to arrays of floats with the data.
    private final EnumMap<Attribute, float[]> vertexAttributeData;

    //Indices to the data itself.  These are grouped together to form primitives (typically triangles)
    private final short[] vertexIndices;

    public Mesh(int numVertices, EnumMap<Attribute, float[]> vertexAttributeData, short[] vertexIndices)
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
        MapCounter<Attribute> vertexDataSizes = new MapCounter<>();

        for (Mesh mesh : meshes)
        {
            indices += mesh.getNumIndices();
            uniqueVerticesCounter += mesh.getNumVertices();
            for (Attribute vertexAttribute : mesh.vertexAttributeData.keySet())
            {
                int vertexDataLength = mesh.vertexAttributeData.get(vertexAttribute).length;
                vertexDataSizes.addCount(vertexAttribute, vertexDataLength);
            }
        }
        numVertices = uniqueVerticesCounter;

        //Create new arrays big enough to hold combined data
        vertexIndices = new short[indices];
        vertexAttributeData = new EnumMap<>(Attribute.class);
        for (Map.Entry<Attribute, Integer> entry : vertexDataSizes.entrySet())
        {
            vertexAttributeData.put(entry.getKey(), new float[entry.getValue()]);
        }

        //Copy data from each individual mesh to this mesh.  This requires offsetting references to vertices in the index buffer appropriately.
        indices = 0;
        uniqueVerticesCounter = 0;
        MapCounter<Attribute> vertexDataOffsets = new MapCounter<>();
        for (Mesh mesh : meshes)
        {
            copyIndexArray(mesh.vertexIndices, vertexIndices, indices, uniqueVerticesCounter);
            indices += mesh.vertexIndices.length;
            for (Attribute attribute : mesh.vertexAttributeData.keySet())
            {
                int offset = vertexDataOffsets.getCount(attribute);
                float[] srcVertexData = mesh.vertexAttributeData.get(attribute);
                System.arraycopy(
                        srcVertexData, 0,
                        vertexAttributeData.get(attribute), offset,
                        srcVertexData.length);
                vertexDataOffsets.addCount(attribute, srcVertexData.length);
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
        EnumMap<Attribute, float[]> transformedVertexData = new EnumMap<>(vertexAttributeData);
        transformedVertexData.put(Attribute.POSITION, positionData);
        transformedVertexData.put(Attribute.NORMAL, normalData);
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

    public final float[] getAttributeData(Attribute attribute)
    {
        return vertexAttributeData.get(attribute);
    }

    public boolean hasAttributeData(Attribute attribute)
    {
        return vertexAttributeData.containsKey(attribute);
    }

    public final float[] getPositionData()
    {
        return vertexAttributeData.get(Attribute.POSITION);
    }

    public final float[] getNormalData()
    {
        return vertexAttributeData.get(Attribute.NORMAL);
    }

    public final float[] getTexCoordData()
    {
        return vertexAttributeData.get(Attribute.TEXCOORD);
    }

    public final short[] getVertexIndices()
    {
        return vertexIndices;
    }

}

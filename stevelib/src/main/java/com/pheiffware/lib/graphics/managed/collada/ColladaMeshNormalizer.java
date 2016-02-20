package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.graphics.managed.mesh.Mesh;

import java.util.HashMap;
import java.util.Map;

/**
 * Does the ugly job of untangling the ridiculous Collada input meshes vertex index data.  The test cases demonstrate this better though examples than I can explain here.
 *
 * Created by Steve on 2/15/2016.
 */
class ColladaMeshNormalizer
{
    private final ColladaMesh colladaMesh;
    private final Map<String, float[]> uncollatedData = new HashMap<>();
    private short[] uninterleavedIndices;
    private short numUniqueIndices;

    /**
     * Creates a new index for each unique combination of indices within a stride.
     */
    private void generateUninterleavedIndices()
    {
        uninterleavedIndices = new short[colladaMesh.vertexCount];
        Map<VertexIndexGroup, Short> indexMap = new HashMap<>();
        int uncollatedIndex = 0;
        numUniqueIndices = 0;
        short[] interleavedIndices = colladaMesh.interleavedIndices;
        int collatedIndexStride = colladaMesh.vertexStride;
        for (int collatedIndex = 0; collatedIndex < interleavedIndices.length; collatedIndex += collatedIndexStride)
        {
            VertexIndexGroup vertexIndexGroup = new VertexIndexGroup(interleavedIndices, collatedIndex, collatedIndexStride);
            Short uniqueIndex = indexMap.get(vertexIndexGroup);
            if (uniqueIndex == null)
            {
                uniqueIndex = numUniqueIndices;
                numUniqueIndices++;
                indexMap.put(vertexIndexGroup, uniqueIndex);
            }
            uninterleavedIndices[uncollatedIndex] = uniqueIndex;
            uncollatedIndex++;
        }
    }

    /**
     * For each input, lookup its collated value for each vertex and store at the corresponding uncollated index

     */
    private void generateUncollatedData()
    {
        short[] interleavedIndices = colladaMesh.interleavedIndices;
        int collatedIndexStride = colladaMesh.vertexStride;
        for (Map.Entry<String, ColladaInput> entry : colladaMesh.inputs.entrySet())
        {
            String key = entry.getKey();
            ColladaInput input = entry.getValue();
            float[] destFloats = new float[numUniqueIndices * input.source.stride];
            int collatedIndex = input.offset;
            for (int i = 0; i < uninterleavedIndices.length; i++)
            {
                short sourceIndex = interleavedIndices[collatedIndex];
                collatedIndex += collatedIndexStride;
                short destIndex = uninterleavedIndices[i];
                input.transfer(sourceIndex, destFloats, destIndex);
            }
            uncollatedData.put(key, destFloats);
        }
    }

    public ColladaMeshNormalizer(Map<String, ColladaInput> vertexDataInputs, short[] interleavedIndices, int vertexCount)
    {
        this(new ColladaMesh(vertexDataInputs, interleavedIndices, vertexCount));
    }

    public ColladaMeshNormalizer(ColladaMesh colladaMesh)
    {
        this.colladaMesh = colladaMesh;
    }

    public Mesh generateMesh()
    {
        generateUninterleavedIndices();
        generateUncollatedData();
        return new Mesh(uninterleavedIndices, uncollatedData);
    }

    /**
     * Holds a group of indices representing different aspects of vertex.  It is setup for hashing these combinations.
     */
    private static class VertexIndexGroup
    {
        private final short[] indices;

        public VertexIndexGroup(short[] interleavedIndices, int start, int length)
        {
            indices = new short[length];
            for (int i = 0; i < indices.length; i++)
            {
                indices[i] = interleavedIndices[start + i];
            }
        }

        @Override
        public int hashCode()
        {
            int code = 0;
            int mult = 1;
            for (int i = 0; i < indices.length; i++)
            {
                code += indices[i] * mult;
                mult *= 97;
            }
            return code;
        }

        @Override
        public boolean equals(Object o)
        {
            VertexIndexGroup other = (VertexIndexGroup) o;
            for (int i = 0; i < indices.length; i++)
            {
                if (indices[i] != other.indices[i])
                {
                    return false;
                }
            }
            return true;
        }
    }

}

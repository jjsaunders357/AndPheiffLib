package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.graphics.managed.mesh.Mesh;

import java.util.HashMap;
import java.util.Map;

/**
 * Does the ugly job of untangling the ridiculous Collada input meshes vertex index data.
 *
 * Created by Steve on 2/15/2016.
 */
//TODO: Make part of ColladaGeometry
class ColladaMeshUncollator
{
    private final ColladaMesh colladaMesh;
    private final Map<String, float[]> uncollatedData = new HashMap<>();
    private short[] uncollatedIndices;
    private short numUniqueIndices;

    /**
     * Creates a new index for each unique combination of indices within a stride.
     */
    private void generateUncollatedIndices()
    {
        uncollatedIndices = new short[colladaMesh.vertexCount];
        Map<VertexIndexGroup, Short> indexMap = new HashMap<>();
        int uncollatedIndex = 0;
        numUniqueIndices = 0;
        short[] collatedIndices = colladaMesh.collatedIndices;
        int collatedIndexStride = colladaMesh.vertexStride;
        for (int collatedIndex = 0; collatedIndex < collatedIndices.length; collatedIndex += collatedIndexStride)
        {
            VertexIndexGroup vertexIndexGroup = new VertexIndexGroup(collatedIndices, collatedIndex, collatedIndexStride);
            Short uniqueIndex = indexMap.get(vertexIndexGroup);
            if (uniqueIndex == null)
            {
                uniqueIndex = numUniqueIndices;
                numUniqueIndices++;
                indexMap.put(vertexIndexGroup, uniqueIndex);
            }
            uncollatedIndices[uncollatedIndex] = uniqueIndex;
            uncollatedIndex++;
        }
    }

    /**
     * For each input, lookup its collated value for each vertex and store at the corresponding uncollated index

     */
    private void generateUncollatedData()
    {
        short[] collatedIndices = colladaMesh.collatedIndices;
        int collatedIndexStride = colladaMesh.vertexStride;
        for (Map.Entry<String, ColladaInput> entry : colladaMesh.inputs.entrySet())
        {
            String key = entry.getKey();
            ColladaInput input = entry.getValue();
            float[] destFloats = new float[numUniqueIndices * input.source.stride];
            int collatedIndex = input.offset;
            for (int i = 0; i < uncollatedIndices.length; i++)
            {
                short sourceIndex = collatedIndices[collatedIndex];
                collatedIndex += collatedIndexStride;
                short destIndex = uncollatedIndices[i];
                input.transfer(sourceIndex, destFloats, destIndex);
            }
            uncollatedData.put(key, destFloats);
        }
    }

    public ColladaMeshUncollator(Map<String, ColladaInput> vertexDataInputs, short[] collatedIndices, int vertexCount)
    {
        this(new ColladaMesh(vertexDataInputs, collatedIndices, vertexCount));
    }

    public ColladaMeshUncollator(ColladaMesh colladaMesh)
    {
        this.colladaMesh = colladaMesh;
        generateUncollatedIndices();
        generateUncollatedData();
    }

    public Mesh createMesh()
    {
        return new Mesh(uncollatedIndices, uncollatedData);
    }

    /**
     * Holds a group of indices representing different aspects of vertex.  It is setup for hashing these combinations.
     */
    private static class VertexIndexGroup
    {
        private final short[] indices;

        public VertexIndexGroup(short[] collatedIndices, int start, int length)
        {
            indices = new short[length];
            for (int i = 0; i < indices.length; i++)
            {
                indices[i] = collatedIndices[start + i];
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

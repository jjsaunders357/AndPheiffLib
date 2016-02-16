package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.graphics.managed.mesh.Mesh;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Does the ugly job of untangling the ridiculous Collada input meshes vertex index data.
 * Collada index data is:
 * 1. Collated - There is a different index scheme for each aspect of each vertex.  (an aspect is something like position, normal, etc).
 * 2. Non uniform - Each aspect uses different vertex indices into source uncollatedData array.  Opengl requires only one uniform index buffer.  (Direct3D doesn't allow this either which is why its a terrible scheme).
 * <p/>
 * This produces new arrays of floats for each type of input such as POSITION, NORMAL, etc and maps them to the corresponding name.
 * Each array of floats is the same length in terms of number item counts.  For example, a position array might have 12 elements, but a texture uncollatedData array would have 8.
 * One consistent index buffer can be used to index into these arrays.
 * <p/>
 * To do this it looks for the total number of unique combinations of indices for each vertex.  For each unique combination there is one vertex worth of data in each aspect's array of floats
 * Created by Steve on 2/15/2016.
 */
public class ColladaMeshUncollator
{
    private final Map<String, float[]> uncollatedData = new HashMap<>();
    private final int collatedIndexStride;
    private short[] uncollatedIndices;
    private short numUniqueIndices;

//    /**
//     * Computes the input which has the maximum item count.  This is not the number of floats, but rather the number of groups of floats such as POSITIONS or TEXTCOORDS.
//     * All inputs need to be scaled up to this size.
//     * @param inputs
//     * @return the input with the maximum count
//     */
//    private ColladaInput computeMaxCountInput(Collection<ColladaInput> inputs)
//    {
//        ColladaInput maxCountInput = null;
//        int maxCount = -1;
//        for (ColladaInput input : inputs)
//        {
//            if (input.source.count > maxCount)
//            {
//                maxCount = input.source.count;
//                maxCountInput = input;
//            }
//        }
//        return maxCountInput;
//    }

    /**
     * Creates a new index for each unique combination of indices within a stride.
     *
     * @param collatedIndices     interleaved indices from which to exact the official uniform list
     * @param collatedIndexStride
     */
    private void generateUncollatedIndices(short[] collatedIndices, int collatedIndexStride)
    {
        uncollatedIndices = new short[collatedIndices.length / collatedIndexStride];

        Map<VertexIndexGroup, Short> indexMap = new HashMap<>();
        int uncollatedIndex = 0;
        numUniqueIndices = 0;
        for (int collatedIndex = 0; collatedIndex < collatedIndices.length; collatedIndex += this.collatedIndexStride)
        {
            VertexIndexGroup vertexIndexGroup = new VertexIndexGroup(collatedIndices, collatedIndex, this.collatedIndexStride);
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
     *
     * @param collatedIndices
     * @param inputs
     */
    private void generateUncollatedData(short[] collatedIndices, Map<String, ColladaInput> inputs)
    {
        for (Map.Entry<String, ColladaInput> entry : inputs.entrySet())
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

    public ColladaMeshUncollator(short[] collatedIndices, int vertexCount, Map<String, ColladaInput> inputs)
    {
        collatedIndexStride = collatedIndices.length / vertexCount;
        generateUncollatedIndices(collatedIndices, collatedIndexStride);
        generateUncollatedData(collatedIndices, inputs);
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

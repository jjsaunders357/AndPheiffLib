package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.ElementObjectFactory;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to parse mesh elements into ColladaMesh instances.
 * Created by Steve on 2/19/2016.
 */

class ColladaMeshFactory
{
    //an already parsed map of data sources
    private final Map<String, ColladaSource> sources;
    //an already parsed map of input from the vertices element
    private final Map<String, ColladaInput> verticesElementInputs;

    public ColladaMeshFactory(Map<String, ColladaSource> sources, Map<String, ColladaInput> verticesElementInputs)
    {
        this.sources = sources;
        this.verticesElementInputs = verticesElementInputs;
    }

    /**
     * From a polylist element and set of already loaded vertex inputs, create a complete ColladaMesh object.  This object contains all the raw information required to create Mesh.
     *
     * @param polyListElement the element to parse from
     * @return a complete ColladaMesh object which can be used to construct a mesh or null if there is no data
     * @throws XMLParseException
     */
    public ColladaMesh fromPolyListElement(Element polyListElement) throws XMLParseException
    {
        Map<String, ColladaInput> inputs = new HashMap<>();
        DomUtils.putSubElementsInMap(inputs, polyListElement, "input", "semantic", new ColladaInputFactory(sources));
        //If there are no inputs, then skip this
        if (inputs.size() == 0)
        {
            return null;
        }
        Element indicesElement = DomUtils.assertGetSingleSubElement(polyListElement, "p");

        //All polygons must be triangles or we can't handle this mesh
        Element vcountElement = DomUtils.assertGetSingleSubElement(polyListElement, "vcount");
        float[] polygonVertexCounts = DomUtils.getFloatsFromElement(vcountElement);
        int vertexCount = 0;
        for (int i = 0; i < polygonVertexCounts.length; i++)
        {
            if (polygonVertexCounts[i] != 3)
            {
                throw new XMLParseException("\"vcount\" tag contained value which was not 3");
            }
            vertexCount += polygonVertexCounts[i];
        }
        short[] interleavedIndices = DomUtils.getShortsFromElement(indicesElement);
        String materialID = polyListElement.getAttribute("material");
        return generateRawMeshData(interleavedIndices, vertexCount, inputs);
    }

    /**
     * From a triangle element and set of already loaded vertex inputs, create a complete ColladaMesh object.  This object contains all the raw information required to create Mesh.
     *
     * @param triangleElement the element to parse from
     * @return a complete ColladaMesh object which can be used to construct a mesh or null if there is no data
     * @throws XMLParseException
     */
    public ColladaMesh fromTrianglesElement(Element triangleElement) throws XMLParseException
    {
        Map<String, ColladaInput> inputs = new HashMap<>();
        DomUtils.putSubElementsInMap(inputs, triangleElement, "input", "semantic", new ColladaInputFactory(sources));
        //If there are no inputs, then skip this
        if (inputs.size() == 0)
        {
            return null;
        }
        int vertexCount = Integer.valueOf(triangleElement.getAttribute("count")) * 3;
        Element indicesElement = DomUtils.assertGetSingleSubElement(triangleElement, "p");
        short[] interleavedIndices = DomUtils.getShortsFromElement(indicesElement);
        return generateRawMeshData(interleavedIndices, vertexCount, inputs);
    }

    /**
     * Takes the essential data from either a parsed polylist or triangles tag and does rest of the work.  This is mostly removing the mostly useless VERTEX input, getting its offset
     * and then replacing this useless input with copies of vertexInputs, which have had their offsets set to the useless VERTEX input's value.
     *
     * @param interleavedIndices   index references to the input data
     * @param vertexCount       number of vertices, used to determine vertex stride
     * @param polyElementInputs an already parsed map of inputs from the polylist/triangles tag
     * @return a complete ColladaMesh object which can be used to construct a mesh or null if there is no data
     * @throws XMLParseException
     */
    private ColladaMesh generateRawMeshData(short[] interleavedIndices, int vertexCount, Map<String, ColladaInput> polyElementInputs) throws XMLParseException
    {
        //Remove VERTEX input as it is a stand in for the vertexInputs passed in.  However, get its offset and apply it to all other inputs.
        ColladaInput uselessVertexInput = polyElementInputs.remove("VERTEX");
        //Its illegal not to have a vertex input if size is non-zero
        if (uselessVertexInput == null)
        {
            throw new XMLParseException("Missing VERTEX input from triangles/polylist tag");
        }
        int vertexInputOffset = uselessVertexInput.offset;

        //Copy vertex inputs into the input map using the offset from "uselessVertexInput"
        for (Map.Entry<String, ColladaInput> entry : verticesElementInputs.entrySet())
        {
            ColladaInput vertexInput = entry.getValue();
            polyElementInputs.put(entry.getKey(), new ColladaInput(vertexInput.semantic, vertexInput.source, vertexInputOffset));
        }

        return new ColladaMesh(polyElementInputs, interleavedIndices, vertexCount);
    }
}

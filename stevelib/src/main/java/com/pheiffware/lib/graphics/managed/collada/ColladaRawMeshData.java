package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all data related to vertices along with a set of collated indices to the data.  Data is in a raw form not directly useable by opengl and need to be "uncollated" 1st.
 * Created by Steve on 2/16/2016.
 */
public class ColladaRawMeshData
{
    /**
     * From a polylist element and set of already loaded vertex inputs, create a complete ColladaRawMeshData object.  This object contains all the raw information required to create Mesh.
     *
     * @param polyListElement       the element to parse from
     * @param sources               an already parsed map of data sources
     * @param verticesElementInputs an already parsed map of input from the vertices tag
     * @return a complete ColladaRawMeshData object which can be used to construct a mesh or null if there is no data
     * @throws XMLParseException
     */
    public static ColladaRawMeshData fromPolyListElement(Element polyListElement, Map<String, ColladaSource> sources, Map<String, ColladaInput> verticesElementInputs) throws XMLParseException
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
        short[] collatedIndices = DomUtils.getShortsFromElement(indicesElement);
        String materialID = polyListElement.getAttribute("material");
        return generateRawMeshData(collatedIndices, vertexCount, inputs, verticesElementInputs);
    }

    /**
     * From a triangle element and set of already loaded vertex inputs, create a complete ColladaRawMeshData object.  This object contains all the raw information required to create Mesh.
     *
     * @param triangleElement       the element to parse from
     * @param sources               an already parsed map of data sources
     * @param verticesElementInputs an already parsed map of input from the vertices tag
     * @return a complete ColladaRawMeshData object which can be used to construct a mesh or null if there is no data
     * @throws XMLParseException
     */
    public static ColladaRawMeshData fromTrianglesElement(Element triangleElement, Map<String, ColladaSource> sources, Map<String, ColladaInput> verticesElementInputs) throws XMLParseException
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
        short[] collatedIndices = DomUtils.getShortsFromElement(indicesElement);
        return generateRawMeshData(collatedIndices, vertexCount, inputs, verticesElementInputs);
    }

    /**
     * Takes the essential data from either a parsed polylist or triangles tag and does rest of the work.  This is mostly removing the mostly useless VERTEX input, getting its offset
     * and then replacing this useless input with copies of vertexInputs, which have had their offsets set to the useless VERTEX input's value.
     *
     * @param collatedIndices       index references to the input data
     * @param vertexCount           number of vertices, used to determine vertex stride
     * @param polyElementInputs     an already parsed map of inputs from the polylist/triangles tag
     * @param verticesElementInputs an already parsed map of input from the vertices tag
     * @return a complete ColladaRawMeshData object which can be used to construct a mesh or null if there is no data
     * @throws XMLParseException
     */
    private static ColladaRawMeshData generateRawMeshData(short[] collatedIndices, int vertexCount, Map<String, ColladaInput> polyElementInputs, Map<String, ColladaInput> verticesElementInputs) throws XMLParseException
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

        return new ColladaRawMeshData(polyElementInputs, collatedIndices, vertexCount);
    }


    //A combined set of input from the external vertices and vertices in a polylist/triangles tag
    public final Map<String, ColladaInput> inputs;
    //Index references to the input data
    public final short[] collatedIndices;
    //How many vertices are referenced total
    public final int vertexCount;
    //How to step through the collatedIndices
    public final int vertexStride;

    public ColladaRawMeshData(Map<String, ColladaInput> vertexDataInputs, short[] collatedIndices, int vertexCount)
    {

        this.inputs = vertexDataInputs;
        this.collatedIndices = collatedIndices;
        this.vertexCount = vertexCount;
        this.vertexStride = collatedIndices.length / vertexCount;
    }
}

/*
        Example from blender:
        <polylist material="Steel-material" count="12">
          <input semantic="VERTEX" source="#CubeTexMesh-mesh-vertices" offset="0"/>
          <input semantic="NORMAL" source="#CubeTexMesh-mesh-normals" offset="1"/>
          <input semantic="TEXCOORD" source="#CubeTexMesh-mesh-map-0" offset="2" set="0"/>
          <vcount>3 3 3 3 3 3 3 3 3 3 3 3 </vcount>
          <p>11 8 18 10 8 19 8 8 20 15 9 21 14 9 22 10 9 23 13 10 24 12 10 25 14 10 26 9 11 27 8 11 28 12 11 29 10 12 30 14 12 31 12 12 32 15 13 33 11 13 34 9 13 35 9 8 54 11 8 55 8 8 56 11 9 57 15 9 58 10 9 59 15 10 60 13 10 61 14 10 62 13 11 63 9 11 64 12 11 65 8 12 66 10 12 67 12 12 68 13 13 69 15 13 70 9 13 71</p>
        </polylist>
        Example from sketchup
        <triangles count="12" material="Material2">
            <input offset="0" semantic="VERTEX" source="#ID16" />
            <p>0 1 2 1 0 3 4 5 6 5 4 7 8 9 10 9 8 11 12 13 14 13 12 15 16 17 18 17 16 19 20 21 22 21 20 23</p>
        </triangles>
        Note: Wherever VERTEX is present, replace it with whatever inputs are in the vertices tag, BUT with VERTEX's offset!  Must make copies of VERTEX's inputs as multiple ColladaVertexLists may share these inputs.
        <vertices id="ID16">
            <input semantic="POSITION" source="#ID14" />
            <input semantic="NORMAL" source="#ID15" />
        </vertices>

 */
package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds all data related to vertices along with a set of interleaved indices to the data.  Data is in a raw form not directly useable by opengl and need to be "normalized" 1st.
 * Created by Steve on 2/16/2016.
 */
class ColladaMesh
{
    //A combined set of input from the external vertices and vertices in a polylist/triangles tag
    public final Map<String, ColladaInput> inputs;
    //Index references to the input data
    public final short[] interleavedIndices;
    //How many vertices are referenced total
    public final int vertexCount;
    //How to step through the interleavedIndices
    public final int vertexStride;

    public ColladaMesh(Map<String, ColladaInput> vertexDataInputs, short[] interleavedIndices, int vertexCount)
    {
        this.inputs = vertexDataInputs;
        this.interleavedIndices = interleavedIndices;
        this.vertexCount = vertexCount;
        this.vertexStride = interleavedIndices.length / vertexCount;
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
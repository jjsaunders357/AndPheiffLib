package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.graphics.managed.mesh.Mesh;
import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.ElementObjectFactory;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses "geometry" tags and extracts ColladaGeometry objects.
 *
 * Created by Steve on 2/15/2016.
 */
class ColladaGeometryFactory implements ElementObjectFactory<ColladaGeometry>
{
    //When position/normals are loaded, a 1/0 is appended to the end of the loaded data to create a homogeneous coordinate/vector
    private final boolean homogenizeCoordinates;

    public ColladaGeometryFactory(boolean homogenizeCoordinates)
    {
        this.homogenizeCoordinates = homogenizeCoordinates;
    }

    @Override
    public ColladaGeometry createFromElement(Element element) throws XMLParseException
    {
        //Parse source tags
        Map<String, ColladaSource> sources = new HashMap<>();
        Element meshElement = DomUtils.assertGetSingleSubElement(element, "mesh");
        DomUtils.putSubElementsInMap(sources, meshElement, "source", "id", new ColladaSourceFactory());

        //Parse vertices inputs
        Map<String, ColladaInput> vertexInputs = new HashMap<>();
        Element vertices = DomUtils.assertGetSingleSubElement(meshElement, "vertices");
        DomUtils.putSubElementsInMap(vertexInputs, vertices, "input", "semantic", new ColladaInputFactory(sources));

        ColladaMeshFactory colladaMeshFactory = new ColladaMeshFactory(sources, vertexInputs);
        ColladaGeometry colladaGeometry = new ColladaGeometry();

        List<Element> polylists = DomUtils.getSubElements(meshElement, "polylist");
        for (Element polyListElement : polylists)
        {
            String materialID = polyListElement.getAttribute("material");
            ColladaMesh colladaMesh = colladaMeshFactory.fromPolyListElement(polyListElement);
            if (colladaMesh != null)
            {
                ColladaMeshNormalizer colladaMeshNormalizer = new ColladaMeshNormalizer(colladaMesh, homogenizeCoordinates);
                Mesh mesh = colladaMeshNormalizer.generateMesh();
                colladaGeometry.add(materialID, mesh);
            }
        }
        List<Element> triangles = DomUtils.getSubElements(meshElement, "triangles");
        for (Element trianglesElement : triangles)
        {
            String materialID = trianglesElement.getAttribute("material");
            ColladaMesh colladaMesh = colladaMeshFactory.fromTrianglesElement(trianglesElement);
            if (colladaMesh != null)
            {
                ColladaMeshNormalizer colladaMeshNormalizer = new ColladaMeshNormalizer(colladaMesh, homogenizeCoordinates);
                Mesh mesh = colladaMeshNormalizer.generateMesh();
                colladaGeometry.add(materialID, mesh);
            }
        }
        return colladaGeometry;
    }


}
/*Example Blender
//source tags - For each parameter, IF name attribute is defined, read value and advance.  If name is not defined, ignore value and advance.  If there are not enough params, still step by stride.
//input tags - Lookup source, semantic just tells you size of what you are getting, VERTEX = 3, TEXCOORD = 2.  You get values from <p>, always advancing by the number of things being read.  Offset specifies where, in each block the given value is.
//vcount - should always contain 3's for triangles.  If it doesn't then we can't read file.
<geometry id="CubeTexMesh-mesh" name="CubeTexMesh">
  <mesh>
    <source id="CubeTexMesh-mesh-positions">
      <float_array id="CubeTexMesh-mesh-positions-array" count="24">-1 -1 -1 -1 -1 1 -1 1 -1 -1 1 1 1 -1 -1 1 -1 1 1 1 -1 1 1 1</float_array>
      <technique_common>
        <accessor source="#CubeTexMesh-mesh-positions-array" count="8" stride="3">
          <param name="X" semantic="float"/>
          <param name="Y" semantic="float"/>
          <param name="Z" semantic="float"/>
        </accessor>
      </technique_common>
    </source>
    <source id="CubeTexMesh-mesh-normals">
      <float_array id="CubeTexMesh-mesh-normals-array" count="18">-1 0 0 0 1 0 1 0 0 0 -1 0 0 0 -1 0 0 1</float_array>
      <technique_common>
        <accessor source="#CubeTexMesh-mesh-normals-array" count="6" stride="3">
          <param name="X" semantic="float"/>
          <param name="Y" semantic="float"/>
          <param name="Z" semantic="float"/>
        </accessor>
      </technique_common>
    </source>
    <source id="CubeTexMesh-mesh-map-0">
      <float_array id="CubeTexMesh-mesh-map-0-array" count="72">0.9998999 9.998e-5 0.9998999 0.9999001 9.998e-5 0.9999001 0 0 0 0 0 0 9.998e-5 0.9999001 1.0004e-4 9.998e-5 0.9999001 9.998e-5 9.998e-5 0.9999001 1.0004e-4 9.998e-5 0.9999001 9.998e-5 9.998e-5 9.998e-5 0.9999001 9.998e-5 0.9999001 0.9999001 0.9999001 0.9999001 1.0004e-4 0.9999001 9.998e-5 9.998e-5 1.0001e-4 1.00099e-4 0.9998999 9.998e-5 9.998e-5 0.9999001 0 0 0 0 0 0 0.9999001 0.9999001 9.998e-5 0.9999001 0.9999001 9.998e-5 0.9999001 0.9999001 9.998e-5 0.9999001 0.9999001 9.998e-5 1.0004e-4 0.9999001 9.998e-5 9.998e-5 0.9999001 0.9999001 0.9999001 9.998e-5 0.9999001 0.9999001 9.998e-5 9.998e-5</float_array>
      <technique_common>
        <accessor source="#CubeTexMesh-mesh-map-0-array" count="36" stride="2">
          <param name="S" semantic="float"/>
          <param name="T" semantic="float"/>
        </accessor>
      </technique_common>
    </source>
    <vertices id="CubeTexMesh-mesh-vertices">
      <input semantic="POSITION" source="#CubeTexMesh-mesh-positions"/>
    </vertices>
    <polylist material="Steel-material" count="12">
      <input semantic="VERTEX" source="#CubeTexMesh-mesh-vertices" offset="0"/>
      <input semantic="NORMAL" source="#CubeTexMesh-mesh-normals" offset="1"/>
      <input semantic="TEXCOORD" source="#CubeTexMesh-mesh-map-0" offset="2" set="0"/>
      <vcount>3 3 3 3 3 3 3 3 3 3 3 3 </vcount>
      <p>3 0 0 2 0 1 0 0 2 7 1 3 6 1 4 2 1 5 5 2 6 4 2 7 6 2 8 1 3 9 0 3 10 4 3 11 2 4 12 6 4 13 4 4 14 7 5 15 3 5 16 1 5 17 1 0 18 3 0 19 0 0 20 3 1 21 7 1 22 2 1 23 7 2 24 5 2 25 6 2 26 5 3 27 1 3 28 4 3 29 0 4 30 2 4 31 4 4 32 5 5 33 7 5 34 1 5 35</p>
    </polylist>
  </mesh>
</geometry>

Example Sketchup:
    <mesh>
        <source id="ID16">
            <float_array id="ID20" count="12">254.1723 167.9948 87.90097 80.79727 167.9948 0 254.1723 167.9948 0 80.79727 167.9948 87.90097</float_array>
            <technique_common>
                <accessor count="4" source="#ID20" stride="3">
                    <param name="X" semantic="float" />
                    <param name="Y" semantic="float" />
                    <param name="Z" semantic="float" />
                </accessor>
            </technique_common>
        </source>
        <source id="ID17">
            <float_array id="ID21" count="12">0 -1 0 0 -1 0 0 -1 0 0 -1 0</float_array>
            <technique_common>
                <accessor count="4" source="#ID21" stride="3">
                    <param name="X" semantic="float" />
                    <param name="Y" semantic="float" />
                    <param name="Z" semantic="float" />
                </accessor>
            </technique_common>
        </source>
        <source id="ID19">
            <float_array id="ID22" count="8">5.494906 1.841259 1.882927 0.009988701 5.494906 0.009988701 1.882927 1.841259</float_array>
            <technique_common>
                <accessor count="4" source="#ID22" stride="2">
                    <param name="S" semantic="float" />
                    <param name="T" semantic="float" />
                </accessor>
            </technique_common>
        </source>
        <vertices id="ID18">
            <input semantic="POSITION" source="#ID16" />
            <input semantic="NORMAL" source="#ID17" />
        </vertices>
        <triangles count="2" material="Material2">
            <input offset="0" semantic="VERTEX" source="#ID18" />
            <input offset="1" semantic="TEXCOORD" source="#ID19" />
            <p>0 0 1 1 2 2 1 1 0 0 3 3</p>
        </triangles>
    </mesh>
 */
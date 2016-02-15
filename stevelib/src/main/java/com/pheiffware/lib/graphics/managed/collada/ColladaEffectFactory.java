package com.pheiffware.lib.graphics.managed.collada;


import com.pheiffware.lib.graphics.GColor;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Used to extract ColladaEffect from <effects></effects> tags.
 * Created by Steve on 2/15/2016.
 */
public class ColladaEffectFactory implements ElementObjectFactory<ColladaEffect>
{
    @Override
    public ColladaEffect createFromElement(String id, Element element) throws ColladaParseException
    {

        Element profileCommon = Collada.assertGetSingleSubElement(element, "profile_COMMON");
        String imageFileReference = getImageFileReference(profileCommon);


        Element technique = Collada.assertGetSingleSubElement(profileCommon, "technique");
        Element phong = Collada.getSingleSubElement(technique, "phong");
        GColor ambientColor;
        GColor diffuseColor;
        GColor specularColor;
        float shininess;

        if (phong != null)
        {
            ambientColor = Collada.getColorSubElement(Collada.assertGetSingleSubElement(phong, "ambient"));
            diffuseColor = Collada.getColorSubElement(Collada.assertGetSingleSubElement(phong, "diffuse"));
            specularColor = Collada.getColorSubElement(Collada.assertGetSingleSubElement(phong, "specular"));
            shininess = Collada.getFloatSubElement(Collada.assertGetSingleSubElement(phong, "shininess"));
        }
        else
        {
            Element lambert = Collada.assertGetSingleSubElement(technique, "lambert");
            ambientColor = new GColor(1.0f, 1.0f, 1.0f, 1.0f);
            diffuseColor = Collada.getColorSubElement(Collada.assertGetSingleSubElement(lambert, "diffuse"));
            specularColor = new GColor(1.0f, 1.0f, 1.0f, 1.0f);
            shininess = 1;
        }
        return new ColladaEffect(imageFileReference, ambientColor, diffuseColor, specularColor, shininess);
    }

    private String getImageFileReference(Element technique) throws ColladaParseException
    {
        //Extract image reference or null if this technique does not contain an image
        NodeList newparamNodes = technique.getElementsByTagName("newparam");
        for (int i = 0; i < newparamNodes.getLength(); i++)
        {
            Element newparamElement = (Element) newparamNodes.item(i);
            Element surface = Collada.getSingleSubElement(newparamElement, "surface");
            if (surface != null)
            {
                String type = surface.getAttribute("type");
                if (type.equals("2D"))
                {
                    Element init_from = Collada.getSingleSubElement(newparamElement, "init_from");
                    String imageReference = init_from.getFirstChild().getTextContent();
                    return imageReference;
                }
            }
        }
        return null;
    }

/*
Sketchup effect example:
    <effect id="ID12">
        <profile_COMMON>
            <newparam sid="ID14">
                <surface type="2D">
                    <init_from>ID13</init_from>   ////This id is an image reference
                </surface>
            </newparam>
            <newparam sid="ID15">
                <sampler2D>
                    <source>ID14</source>
                </sampler2D>
            </newparam>
            <technique sid="COMMON">
                <lambert>
                    <diffuse>
                        <texture texture="ID15" texcoord="UVSET0" />   ////This is either a texture or a color.  If a color then the above "newparam" won't be present.
                    </diffuse>
                </lambert>
            </technique>
        </profile_COMMON>
    </effect>

Blender effect example:
    <effect id="Steel-effect">
      <profile_COMMON>
        <newparam sid="steel_png-surface">
          <surface type="2D">
            <init_from>steel_png</init_from>
          </surface>
        </newparam>
        <newparam sid="steel_png-sampler">
          <sampler2D>
            <source>steel_png-surface</source>
          </sampler2D>
        </newparam>
        <technique sid="common">
          <phong>
            <emission>
              <color sid="emission">0 0 0 1</color>
            </emission>
            <ambient>
              <color sid="ambient">0 0 0 1</color>
            </ambient>
            <diffuse>
              <texture texture="steel_png-sampler" texcoord="UVMap"/>
            </diffuse>
            <specular>
              <color sid="specular">0.5 0.5 0.5 1</color>
            </specular>
            <shininess>
              <float sid="shininess">50</float>
            </shininess>
            <index_of_refraction>
              <float sid="index_of_refraction">1</float>
            </index_of_refraction>
          </phong>
        </technique>
      </profile_COMMON>
    </effect>
*/
}
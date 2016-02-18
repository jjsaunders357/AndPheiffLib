package com.pheiffware.lib.graphics.managed.collada;


import com.pheiffware.lib.graphics.GColor;
import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.ElementObjectFactory;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

import java.util.List;

/**
 * Used to extract ColladaEffect from <effects></effects> tags.
 * Created by Steve on 2/15/2016.
 */
public class ColladaEffectFactory implements ElementObjectFactory<ColladaEffect>
{
    @Override
    public ColladaEffect createFromElement(Element element) throws XMLParseException
    {

        Element profileCommon = DomUtils.assertGetSingleSubElement(element, "profile_COMMON");
        String imageFileReference = getImageFileReference(profileCommon);


        Element technique = DomUtils.assertGetSingleSubElement(profileCommon, "technique");
        Element phong = DomUtils.getSingleSubElement(technique, "phong");
        GColor ambientColor = null;
        GColor diffuseColor = null;
        GColor specularColor = null;
        float shininess;

        if (phong != null)
        {
            ambientColor = DomUtils.getColorSubElement(DomUtils.assertGetSingleSubElement(phong, "ambient"));
            diffuseColor = DomUtils.getColorSubElement(DomUtils.assertGetSingleSubElement(phong, "diffuse"));
            specularColor = DomUtils.getColorSubElement(DomUtils.assertGetSingleSubElement(phong, "specular"));
            //In percent
            shininess = DomUtils.getFloatSubElement(DomUtils.assertGetSingleSubElement(phong, "shininess")) / 100.0f;
        }
        else
        {
            Element lambert = DomUtils.assertGetSingleSubElement(technique, "lambert");
            diffuseColor = DomUtils.getColorSubElement(DomUtils.assertGetSingleSubElement(lambert, "diffuse"));
            shininess = Collada.DEFAULT_SHININESS;
        }
        if (ambientColor == null)
        {
            ambientColor = Collada.DEFAULT_AMBIENT;
        }
        if (diffuseColor == null)
        {
            diffuseColor = Collada.DEFAULT_DIFFUSE_TEXTURE;
        }
        if (specularColor == null)
        {
            specularColor = Collada.DEFAULT_SPECULAR;
        }
        return new ColladaEffect(imageFileReference, ambientColor, diffuseColor, specularColor, shininess);
    }

    private String getImageFileReference(Element technique) throws XMLParseException
    {
        //Extract image reference or null if this technique does not contain an image
        List<Element> newparamElements = DomUtils.getSubElements(technique, "newparam");
        for (Element newparamElement : newparamElements)
        {
            Element surface = DomUtils.getSingleSubElement(newparamElement, "surface");
            if (surface != null)
            {
                String type = surface.getAttribute("type");
                if (type.equals("2D"))
                {
                    Element init_from = DomUtils.assertGetSingleSubElement(surface, "init_from");
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
                <surface semantic="2D">
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
          <surface semantic="2D">
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
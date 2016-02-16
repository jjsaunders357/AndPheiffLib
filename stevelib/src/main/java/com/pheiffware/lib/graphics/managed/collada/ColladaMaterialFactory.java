package com.pheiffware.lib.graphics.managed.collada;

import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.ElementObjectFactory;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * Created by Steve on 2/15/2016.
 */
public class ColladaMaterialFactory implements ElementObjectFactory<Material>
{
    private final Map<String, String> imageFileNames;
    private final Map<String, ColladaEffect> effects;

    public ColladaMaterialFactory(Map<String, String> imageFileNames, Map<String, ColladaEffect> effects)
    {
        this.imageFileNames = imageFileNames;
        this.effects = effects;
    }

    @Override
    public Material createFromElement(Element element) throws XMLParseException
    {
        String name = element.getAttribute("name");
        Element instance_effect = DomUtils.assertGetSingleSubElement(element, "instance_effect");
        String url = instance_effect.getAttribute("url");
        String effectKey = url.substring(1);
        ColladaEffect effect = effects.get(effectKey);

        //Will be null in imageReference is null
        String imageFileName = imageFileNames.get(effect.imageFileNameKey);

        return new Material(name, imageFileName, effect.ambientColor, effect.diffuseColor, effect.specularColor, effect.shininess);
    }
/*Example:
    <material id="ID4" name="material">
        <instance_effect url="#ID3" />
    </material>
*/
}
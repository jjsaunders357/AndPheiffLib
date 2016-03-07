package com.pheiffware.lib.geometry.collada;

import com.pheiffware.lib.graphics.managed.mesh.Material;
import com.pheiffware.lib.utils.dom.DomUtils;
import com.pheiffware.lib.utils.dom.ElementObjectFactory;
import com.pheiffware.lib.utils.dom.XMLParseException;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * Used to extract material from a material element.
 * Created by Steve on 2/15/2016.
 */
class ColladaMaterialFactory implements ElementObjectFactory<Material>
{
    //A map from image file name ids to image file names
    private final Map<String, String> imageFileNamesFromIDs;
    private final Map<String, ColladaEffect> effectsFromIDs;

    public ColladaMaterialFactory(Map<String, String> imageFileNamesFromIDs, Map<String, ColladaEffect> effectsFromIDs)
    {
        this.imageFileNamesFromIDs = imageFileNamesFromIDs;
        this.effectsFromIDs = effectsFromIDs;
    }

    @Override
    public Material createFromElement(Element element) throws XMLParseException
    {
        String name = element.getAttribute("name");
        Element instance_effect = DomUtils.assertGetSubElement(element, "instance_effect");
        String url = instance_effect.getAttribute("url");
        String effectKey = url.substring(1);
        ColladaEffect effect = effectsFromIDs.get(effectKey);

        //Will be null in imageReference is null
        String imageFileName = imageFileNamesFromIDs.get(effect.imageFileNameKey);

        return new Material(name, imageFileName, effect.ambientColor, effect.diffuseColor, effect.specularColor, effect.shininess);
    }
/*Example:
    <material id="ID4" name="material">
        <instance_effect url="#ID3" />
    </material>
*/
}
